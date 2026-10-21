<template>
  <a-layout-content class="dwagger-body-content">
    <div class="globalparameters">
      <a-alert type="info" class="gp-tip">
        <span v-html="$t('global.note')"></span>
      </a-alert>
    </div>
    <div class="globalparameters">
      <a-button type="primary" @click="addGlobalParameters">
        <icon-plus type="plus" />
        <span>{{ $t('global.add') }}</span>
      </a-button>
    </div>
    <div class="globalparameters">
      <a-table :columns="columns" rowKey="pkid" size="small" :data="globalParameters" :pagination="pagination"
               bordered>
        <template #operation="{ record }">
          <a-button status="danger" @click="deleteParam(record)" style="margin-left:10px;">
            <template #icon>
              <icon-delete/>
            </template>
            {{ $t('global.delete') }}
          </a-button>
        </template>
        <template #paramContentLabel="{ record }">
          <a-textarea :default-value="record.value" @change="(val) => headerContentChange(val, record)"
                      :auto-size="{ minRows: 2, maxRows: 6 }" allow-clear />
        </template>
        <template #paramTypeLable="{ record }">
          <a-select :default-value="record.in" @change="(val) => globalParamTypeChange(val, record)">
            <a-option value="header">header</a-option>
            <a-option value="query">query</a-option>
          </a-select>
        </template>
      </a-table>
    </div>
    <!--参数编辑及新增-->
    <a-modal :title="modelTitle" :cancelText="$t('global.cancel')" :okText="$t('global.ok')" v-model:visible="visible"
             @ok="handleOk" @cancel="handleCancel">
      <a-form :model="form" ref="formRef">
        <a-form-item
            field="name" :rules="[{ required: true, message: '' }]"
            :label-col-props="labelCol" :wrapper-col-props="wrapperCol" :label="$t('global.form.name')">
          <a-input v-model="form.name" :placeholder="$t('global.form.validate.name')"/>
        </a-form-item>
        <a-form-item
            field="value" :rules="[{ required: true, message: '' }]"
            :label-col-props="labelCol" :wrapper-col-props="wrapperCol" :label="$t('global.form.value')">
          <a-input v-model="form.value" :placeholder="$t('global.form.validate.value')"/>
        </a-form-item>
        <a-form-item
            field="in" :rules="{ required: true, message: '请选择参数类型' }"
            :label-col-props="labelCol" :wrapper-col-props="wrapperCol" :label="$t('global.form.type')">
          <a-select v-model="form.in">
            <a-option value="header">header</a-option>
            <a-option value="query">query</a-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-content>
</template>
<script>
import Constants from "@/store/constants";
import { eventBus } from "@/core/eventBus";
import { useGlobalsStore } from '@/store/modules/global.js'
import { computed, reactive, ref } from 'vue'
import localStore from '@/store/local.js'
import { useI18n } from 'vue-i18n'
import { IconPlus } from '@arco-design/web-vue/es/icon'
import { Message as message } from '@arco-design/web-vue';export default {
  props: {
    data: {
      type: Object
    }
  },
  components: {
    IconPlus
  },
  setup() {
    const store = useGlobalsStore()
    const language = computed(() => {
      return store.language
    })

    const { messages } = useI18n()

    const formRef = ref(null)

    const form = reactive({
      name: '',
      value: '',
      in: 'header'
    })
    return {
      language,
      messages,
      formRef,
      form
    }
  },
  data() {
    return {
      modelTitle: "新增参数",
      columns: [],
      visible: false,
      pagination: false,
      groupId: "",
      globalParameters: [],
      labelCol: {
        xs: { span: 21 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 27 },
        sm: { span: 15 }
      }
    };
  },
  watch: {
    language: function (val, oldval) {
      this.initI18n();
    }
  },
  created() {
    this.groupId = this.data.instance.id;
    const key = this.groupId;
    // this.$localStore.setItem(Constants.globalParameter, 'test')
    localStore.getItem(Constants.globalParameter).then((val) => {
      console.log(val)
      if (val != null) {
        if (val[key] != undefined && val[key] != null) {
          this.globalParameters = val[key];
        }
      } else {
        const obj = {}
        obj[key] = [];
        localStore.setItem(Constants.globalParameter, obj);
      }
    });
    this.initI18n();
  },
  methods: {
    getCurrentI18nInstance() {
      return this.messages[this.language];
    },
    initI18n() {
      var inst = this.getCurrentI18nInstance();
      this.modelTitle = inst.global.model;
      var slotMap = {
        value: 'paramContentLabel',
        in: 'paramTypeLable',
        operation: 'operation'
      };
      this.columns = (inst.global.tableHeader || []).map(col => {
        const slotName = slotMap[col.dataIndex];
        return slotName ? Object.assign({}, col, { slotName }) : Object.assign({}, col);
      });
    },
    headerContentChange(globalParamValue, record) {
      var pkid = record.pkid;
      // 更新参数,同步参数到本地local
      var newArrs = [];
      this.globalParameters.forEach(gp => {
        if (gp.pkid != pkid) {
          // 直接push
          newArrs.push(gp);
        } else {
          // 新的值
          newArrs.push({
            name: gp.name,
            value: globalParamValue,
            in: gp.in,
            pkid: pkid
          });
        }
      });
      this.globalParameters = newArrs;
      this.storeGlobalParameters();
    },
    globalParamTypeChange(globalParamValue, record) {
      const tmpArrs = this.globalParameters;
      // 旧pkid
      const pkid = record.pkid
      const name = record.name
      const newpkid = name + globalParamValue
      // 判断是否已经存在该参数
      const fl = this.globalParameters.filter(
          gp => gp.name == name && gp.in == globalParamValue
      ).length
      if (fl == 0) {
        const newArrs = []
        // 由于in类型已经更改,重新生成数据类型
        this.globalParameters.forEach(gp => {
          if (gp.pkid != pkid) {
            // 直接push
            newArrs.push(gp);
          } else {
            // 新的值
            newArrs.push({
              name: gp.name,
              value: gp.value,
              in: globalParamValue,
              pkid: newpkid
            });
          }
        });
        this.globalParameters = newArrs;
        this.storeGlobalParameters();
      } else {
        message.info("参数已存在,不可重复添加");
        this.globalParameters = [];
        setTimeout(() => {
          this.globalParameters = tmpArrs;
        }, 10);
      }
    },
    storeGlobalParameters() {
      localStore.getItem(Constants.globalParameter).then((val) => {
        const dfv = val;
        dfv[this.groupId] = this.globalParameters;
        localStore.setItem(Constants.globalParameter, dfv);
        // 广播全局参数更新事件
        eventBus.emit('global-parameters-updated', { 
          groupId: this.groupId, 
          parameters: this.globalParameters 
        });
      })
    },
    deleteParam(record) {
      const np = []
      this.globalParameters.forEach(function (gp) {
        if (!(gp.name == record.name && gp.in == record.in)) {
          np.push(gp);
        }
      });
      this.globalParameters = np;
      this.storeGlobalParameters();
    },
    handleOk(e) {
      e.preventDefault();
      const key = this.groupId;
      this.formRef.validate().then((errors) => {
        if (errors) {
          return;
        }
        const values = this.form;
        // 判断是否存在参数
        const fl = this.globalParameters.filter(
            gp => gp.name == values.name && gp.in == values.in
        ).length
        if (fl == 0) {
          const pkid = values.name + values.in
          const nvl = { ...values, pkid: pkid }
          this.globalParameters.push(nvl);
          localStore.getItem(Constants.globalParameter).then((val) => {
            const dfv = val;
            dfv[key] = this.globalParameters;
            localStore.setItem(Constants.globalParameter, dfv);
            // 广播全局参数更新事件
            eventBus.emit('global-parameters-updated', {
              groupId: key,
              parameters: this.globalParameters
            })
          })
          this.visible = false;
        } else {
          message.info("参数已存在,不可重复添加");
        }
      })
    },
    handleCancel(e) {
      // console("Clicked cancel button");
      this.visible = false;
    },
    addGlobalParameters() {
      console.log(this.form)
      this.formRef.resetFields();
      this.visible = true;
      // this.$emit("childrenMethods", "addGlobalParameters", data);
    }
  }
};
</script>

<style scoped>
.globalparameters {
  width: 98%;
  margin: 10px auto;
}

.gp-tip {
  margin-bottom: 10px;
}

.gptable {
  margin-top: 10px;
}
</style>
