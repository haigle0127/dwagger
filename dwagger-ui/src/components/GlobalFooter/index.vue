<template>
  <div class="globalFooter">
    <a-row v-if="settings.enableFooterCustom">
      <markdown v-if="settings.enableFooterCustom" :source="settings.footerCustomContent" />
    </a-row>
    <div style="text-align: center" v-else-if="settings.enableFooter">
      <div class="copyright">TIM | Copyright
        <icon-copyright /> 2026-dwagger
      </div>
    </div>
  </div>
</template>
<script>
import { computed } from 'vue'
import { useGlobalsStore } from '@/store/modules/global.js'
import Markdown from '../Markdown/index.vue'
import { IconCopyright } from '@arco-design/web-vue/es/icon'

export default {
  name: "GlobalFooter",
  components: { Markdown, IconCopyright },
  props: {
    links: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  setup() {
    const globalsStore = useGlobalsStore()
    return {
      settings: computed(() => {
        return globalsStore.settings;
      })
    }
  }
};
</script>

<style lang="less" scoped>
@import "./index.less";
</style>

