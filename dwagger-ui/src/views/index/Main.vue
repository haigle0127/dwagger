<template>
  <a-layout-content class="dwagger-body-content">
    <div class="markdown-body editormd-preview-container" v-if="settings.enableHomeCustom">
      <Markdown :source="settings.homeCustomLocation" />
    </div>
    <div class="home" v-else>
      <!-- 顶部概览卡片 -->
      <div class="hero">
        <h2 class="hero-title">{{ swaggerCurrentInstance.title }}</h2>
        <p class="hero-desc" v-if="swaggerCurrentInstance.description"
           v-html="swaggerCurrentInstance.description"></p>
        <div class="hero-tags">
          <span class="hero-tag" v-if="swaggerCurrentInstance.version">
            {{ $t('homePage.version') }} {{ swaggerCurrentInstance.version }}
          </span>
          <span class="hero-tag" v-if="swaggerCurrentInstance.name">
            {{ $t('homePage.groupName') }} {{ swaggerCurrentInstance.name }}
          </span>
          <span class="hero-tag" v-if="swaggerCurrentInstance.host">
            {{ swaggerCurrentInstance.host }}
          </span>
        </div>
      </div>

      <div class="card-row">
        <!-- 服务信息 -->
        <div class="card">
          <div class="card-title">{{ $t('homePage.serviceInfo') }}</div>
          <div class="info-line" v-for="row in serviceRows" :key="row.label">
            <span class="info-label">{{ row.label }}</span>
            <span class="info-value" v-html="row.value || '-'"></span>
          </div>
        </div>
        <!-- 分组信息 -->
        <div class="card">
          <div class="card-title">{{ $t('homePage.groupInfo') }}</div>
          <div class="info-line" v-for="row in groupRows" :key="row.label">
            <span class="info-label">{{ row.label }}</span>
            <span class="info-value" v-html="row.value || '-'"></span>
          </div>
        </div>
      </div>

      <!-- 接口统计 -->
      <div class="card">
        <div class="card-title">{{ $t('homePage.apiCountNumber') }}</div>
        <div class="method-list">
          <div class="method-item" v-for="param in swaggerCurrentInstance.pathArrs"
               :key="param.method" :class="'method-' + String(param.method).toLowerCase()">
            <span class="method-name">{{ param.method }}</span>
            <span class="method-count">{{ param.count }}</span>
          </div>
        </div>
      </div>
    </div>
  </a-layout-content>
</template>
<script>
import { computed, defineAsyncComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import { useGlobalsStore } from '@/store/modules/global.js'

export default {
  props: {
    data: {
      type: Object
    }
  },
  components: {
    "Markdown": defineAsyncComponent(() => import('@/components/Markdown/index.vue'))
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
      title: 'dwagger'
    }
  }
};
</script>
<style scoped>
.home {
  padding: 16px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 顶部概览 */
.hero {
  background: linear-gradient(135deg, var(--dwagger-hero-start) 0%, var(--dwagger-hero-end) 100%);
  border-radius: 10px;
  color: #fff;
  padding: 32px 28px;
  margin-bottom: 16px;
}

.hero-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 10px;
}

.hero-desc {
  font-size: 15px;
  line-height: 24px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 16px;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-tag {
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  padding: 4px 14px;
  font-size: 13px;
}

/* 信息卡片 */
.card-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.card-row .card {
  flex: 1;
  min-width: 320px;
}

.card {
  background: #fff;
  border: 1px solid #e5e6eb;
  border-radius: 10px;
  padding: 20px 24px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: #1d2129;
  padding-left: 10px;
  border-left: 4px solid var(--dwagger-primary);
  margin-bottom: 14px;
}

.info-line {
  display: flex;
  align-items: baseline;
  min-height: 38px;
  line-height: 38px;
  border-bottom: 1px dashed #e5e6eb;
}

.info-line:last-child {
  border-bottom: none;
}

.info-label {
  width: 120px;
  flex-shrink: 0;
  color: #86909c;
  font-size: 14px;
}

.info-value {
  color: #1d2129;
  font-size: 14px;
  word-break: break-all;
}

/* 接口统计 */
.method-list {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
}

.method-item {
  display: flex;
  align-items: center;
  border-radius: 8px;
  overflow: hidden;
  font-size: 14px;
}

.method-name {
  color: #fff;
  font-weight: 600;
  padding: 8px 16px;
}

.method-count {
  background: #fff;
  color: #1d2129;
  font-weight: 600;
  padding: 8px 18px;
}

.method-get .method-name { background: #61affe; }
.method-post .method-name { background: #49cc90; }
.method-put .method-name { background: #fca130; }
.method-delete .method-name { background: #f93e3e; }
.method-patch .method-name { background: #50e3c2; }
.method-head .method-name { background: #9012fe; }
.method-options .method-name { background: #0d5aa7; }
</style>
