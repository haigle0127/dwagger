<template>
  <a-layout-content class="dwagger-body-content">
    <a-card :bordered="false" class="auth-card">
      <div class="auth-header">
        <div class="auth-heading">
          <a-typography-title :heading="5" class="auth-title">{{ $t('auth.title') }}</a-typography-title>
          <a-typography-text type="secondary" class="auth-subtitle">
            {{ $t('auth.subtitle', { group: groupName }) }}
          </a-typography-text>
        </div>
        <a-space :size="8">
          <a-button @click="logout">{{ $t('auth.cancel') }}</a-button>
          <a-button type="primary" @click="save">{{ $t('auth.save') }}</a-button>
        </a-space>
      </div>
      <a-alert type="info" class="auth-tip">{{ $t('auth.tip') }}</a-alert>
      <a-table v-if="securityArrs.length" :columns="columns" :data="securityArrs" :pagination="false"
               row-key="id" size="medium">
        <template #key="{ record }">
          <a-space :size="6">
            <a-typography-text code>{{ record.key }}</a-typography-text>
            <a-tag size="small" :bordered="false">{{ record.type }}</a-tag>
          </a-space>
        </template>
        <template #name="{ record }">{{ record.name }}</template>
        <template #in="{ record }">
          <a-tag size="small" :color="record.in === 'header' ? 'arcoblue' : 'orange'">{{ record.in }}</a-tag>
        </template>
        <template #value="{ record }">
          <a-input v-model="record.value" :placeholder="$t('auth.placeholder')" allow-clear/>
        </template>
        <template #operation="{ record }">
          <a-button type="text" status="danger" @click="clearValue(record)">{{ $t('auth.clear') }}</a-button>
        </template>
      </a-table>
      <a-empty v-else :description="$t('auth.empty')"/>
    </a-card>
  </a-layout-content>
</template>

<script>
import Constants from "@/store/constants";
import KUtils from "@/core/utils";
import localStore from "@/store/local.js";
import { eventBus } from "@/core/eventBus";
import { Message as message, Modal } from '@arco-design/web-vue';

export default {
  name: "Authorize",
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      securityArrs: [],
      groupName: ""
    };
  },
  computed: {
    columns() {
      return [
        { title: this.$t('auth.tableHeader.key'), dataIndex: 'key', slotName: 'key', width: 240 },
        { title: this.$t('auth.tableHeader.name'), dataIndex: 'name', width: 200 },
        { title: this.$t('auth.tableHeader.in'), dataIndex: 'in', width: 110 },
        { title: this.$t('auth.tableHeader.value'), dataIndex: 'value', slotName: 'value' },
        { title: this.$t('auth.tableHeader.operator'), slotName: 'operation', width: 100 }
      ];
    }
  },
  created() {
    const instance = this.data && this.data.instance;
    if (!instance) {
      return;
    }
    this.groupName = instance.name;
    // 拷贝一份，未点保存前不影响调试页已读取的参数
    this.securityArrs = (instance.securityArrs || []).map(item => Object.assign({}, item));
    this.loadCachedValues();
  },
  methods: {
    loadCachedValues() {
      const key = Constants.globalSecurityParamPrefix + this.data.instance.id;
      localStore.getItem(key).then(cached => {
        if (!KUtils.arrNotEmpty(cached)) {
          return;
        }
        this.securityArrs.forEach(security => {
          const cache = cached.find(item => item.id === security.id) ||
              cached.find(item => item.key === security.key && item.name === security.name);
          if (cache && KUtils.strNotBlank(cache.value)) {
            security.value = cache.value;
          }
        });
      });
    },
    // 请求头只允许可见 ASCII，中文等字符会导致浏览器拒绝发送
    isValidHeaderValue(value) {
      return !KUtils.strNotBlank(value) || /^[\x20-\x7E\t]*$/.test(value);
    },
    save() {
      if (this.securityArrs.some(security => security.in === 'header' && !this.isValidHeaderValue(security.value))) {
        message.warning(this.$t('auth.valueInvalid'));
        return;
      }
      const instance = this.data.instance;
      const key = Constants.globalSecurityParamPrefix + instance.id;
      const securityValues = {};
      this.securityArrs.forEach(security => {
        securityValues[security.id] = security.value;
      });
      Promise.all([
        localStore.setItem(key, this.securityArrs),
        localStore.getItem(Constants.globalSecurityParameterObject).then(values => {
          return localStore.setItem(Constants.globalSecurityParameterObject,
              Object.assign({}, values || {}, securityValues));
        })
      ]).then(() => {
        // 同步实例，切换回该分组时仍能读到最新值
        instance.securityArrs.forEach(security => {
          const current = this.securityArrs.find(item => item.id === security.id);
          if (current) {
            security.value = current.value;
          }
        });
        eventBus.emit('security-updated', {
          instanceId: instance.id,
          securityArrs: this.securityArrs
        });
        message.success(this.$t('auth.saved'));
      });
    },
    clearValue(record) {
      record.value = '';
    },
    logout() {
      Modal.confirm({
        title: this.$t('auth.cancel'),
        content: this.$t('message.auth.confirm'),
        okText: this.$t('global.ok'),
        cancelText: this.$t('global.cancel'),
        onOk: () => {
          const instance = this.data.instance;
          const key = Constants.globalSecurityParamPrefix + instance.id;
          this.securityArrs.forEach(security => {
            security.value = '';
            const current = instance.securityArrs.find(item => item.id === security.id);
            if (current) {
              current.value = '';
            }
          });
          localStore.removeItem(key).then(() => {
            eventBus.emit('security-updated', {
              instanceId: instance.id,
              securityArrs: this.securityArrs
            });
            message.success(this.$t('message.auth.success'));
          });
        }
      });
    }
  }
};
</script>

<style scoped>
.auth-card {
  width: 98%;
  margin: 20px auto;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
}

.auth-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px 16px;
  margin-bottom: 16px;
}

.auth-title {
  margin: 0;
}

.auth-subtitle {
  display: block;
  margin-top: 4px;
}

.auth-tip {
  margin-bottom: 16px;
}
</style>
