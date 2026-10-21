<template>
  <a-layout-content class="dwagger-body-content">
    <div class="markdown-body editormd-preview-container" v-if="settings.enableHomeCustom">
      <Markdown :source="settings.homeCustomLocation" />
    </div>
    <div class="home" v-else>
      <!-- 顶部概览卡片 -->
      <a-card class="hero-card" :bordered="false">
        <a-typography-title :heading="3" class="hero-title">
          {{ swaggerCurrentInstance.title }}
        </a-typography-title>
        <a-typography-paragraph class="hero-desc" v-if="swaggerCurrentInstance.description">
          <span v-html="swaggerCurrentInstance.description"></span>
        </a-typography-paragraph>
        <a-space wrap :size="10">
          <a-tag class="hero-tag" :bordered="false" v-if="swaggerCurrentInstance.version">
            {{ $t('homePage.version') }} {{ swaggerCurrentInstance.version }}
          </a-tag>
          <a-tag class="hero-tag" :bordered="false" v-if="swaggerCurrentInstance.name">
            {{ $t('homePage.groupName') }} {{ swaggerCurrentInstance.name }}
          </a-tag>
          <a-tag class="hero-tag" :bordered="false" v-if="swaggerCurrentInstance.host">
            {{ swaggerCurrentInstance.host }}
          </a-tag>
        </a-space>
      </a-card>

      <a-row class="card-row" :gutter="16">
        <!-- 服务信息 -->
        <a-col :xs="24" :lg="12">
          <a-card class="info-card" :bordered="false">
            <template #title>
              <span class="card-title">
                <icon-info-circle />{{ $t('homePage.serviceInfo') }}
              </span>
            </template>
            <a-descriptions :column="1" size="large">
              <a-descriptions-item v-for="row in serviceRows" :key="row.label" :label="row.label">
                <span v-html="row.value || '-'"></span>
              </a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>
        <!-- 分组信息 -->
        <a-col :xs="24" :lg="12">
          <a-card class="info-card" :bordered="false">
            <template #title>
              <span class="card-title">
                <icon-apps />{{ $t('homePage.groupInfo') }}
              </span>
            </template>
            <a-descriptions :column="1" size="large">
              <a-descriptions-item v-for="row in groupRows" :key="row.label" :label="row.label">
                <span v-html="row.value || '-'"></span>
              </a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>
      </a-row>

      <!-- 接口统计 -->
      <a-card class="method-card" :bordered="false">
        <template #title>
          <span class="card-title">
            <icon-bar-chart />{{ $t('homePage.apiCountNumber') }}
          </span>
        </template>
        <a-space wrap :size="12">
          <a-tag v-for="param in swaggerCurrentInstance.pathArrs"
                 :key="param.method"
                 :color="methodColors[param.method] || 'gray'"
                 class="method-tag">
            <span class="method-name">{{ param.method }}</span>
            <span class="method-count">{{ param.count }}</span>
          </a-tag>
        </a-space>
      </a-card>
    </div>
  </a-layout-content>
</template>
<script>
import { computed, defineAsyncComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import { useGlobalsStore } from '@/store/modules/global.js'
import { IconApps, IconBarChart, IconInfoCircle } from '@arco-design/web-vue/es/icon'

export default {
  props: {
    data: {
      type: Object
    }
  },
  components: {
    "Markdown": defineAsyncComponent(() => import('@/components/Markdown/index.vue')),
    IconInfoCircle,
    IconApps,
    IconBarChart,
  },
  setup() {
    const globalsStore = useGlobalsStore()
    const { t } = useI18n()
    const swaggerCurrentInstance = computed(() => {
      return globalsStore.swaggerCurrentInstance
    })

    const settings = computed(() => {
      return globalsStore.settings
    })

    // 服务信息行：标签 + 取值，全部来自当前文档实例
    const serviceRows = computed(() => {
      const ins = swaggerCurrentInstance.value
      return [
        { label: t('homePage.author'), value: ins.contact },
        { label: t('homePage.version'), value: ins.version },
        { label: t('homePage.host'), value: ins.host },
        { label: t('homePage.basePath'), value: ins.basePath },
        { label: t('homePage.serviceUrl'), value: ins.termsOfService },
      ]
    })

    // 分组信息行
    const groupRows = computed(() => {
      const ins = swaggerCurrentInstance.value
      return [
        { label: t('homePage.groupName'), value: ins.name },
        { label: t('homePage.groupUrl'), value: ins.url },
        { label: t('homePage.groupLocation'), value: ins.location },
      ]
    })

    return {
      swaggerCurrentInstance,
      settings,
      serviceRows,
      groupRows,
      // HTTP 方法对应 Arco 预设色，保持和标签体系一致
      methodColors: {
        GET: 'arcoblue',
        POST: 'green',
        PUT: 'orange',
        DELETE: 'red',
        PATCH: 'cyan',
        HEAD: 'purple',
        OPTIONS: 'blue',
      },
      title: 'dwagger'
    }
  }
};
</script>
<style scoped>
.home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 顶部概览：保留渐变主视觉，内容交给 Arco 排版组件 */
.hero-card {
  background: linear-gradient(135deg, var(--dwagger-hero-start) 0%, var(--dwagger-hero-end) 100%);
}

.hero-card :deep(.arco-card-body) {
  padding: 32px 28px;
}

.hero-title {
  margin: 0 0 10px;
  color: #fff;
}

.hero-desc {
  margin: 0 0 16px;
  color: rgba(255, 255, 255, 0.85);
  font-size: 15px;
  line-height: 24px;
}

.hero-tag {
  color: #fff;
  background: rgba(255, 255, 255, 0.16);
  border-radius: 20px;
  padding: 4px 14px;
  font-size: 13px;
}

.card-row {
  margin-top: 16px;
}

.card-row .info-card {
  height: 100%;
}

.card-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.info-card :deep(.arco-descriptions-item-label) {
  width: 110px;
  color: #86909c;
}

.info-card :deep(.arco-descriptions-item-value) {
  color: #1d2129;
  word-break: break-all;
}

/* 接口统计 */
.method-card {
  margin-top: 16px;
}

.method-tag {
  height: 36px;
  padding: 0 16px;
  font-size: 14px;
  font-weight: 600;
  line-height: 34px;
  border-radius: 8px;
}

.method-count {
  margin-left: 10px;
  padding-left: 10px;
  border-left: 1px solid currentColor;
  opacity: 0.7;
}
</style>
