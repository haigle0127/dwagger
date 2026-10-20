<template functional>
  <a-sub-menu :key="menuInfo.key">
    <span slot="title">
      <icon-email /><span>{{ menuInfo.name }}</span>
    </span>
    <template v-for="item in menuInfo.children">
      <a-menu-item v-if="!item.children" :key="item.key">
        <icon-bar-chart />
        <span>{{ item.name }}</span>
      </a-menu-item>
      <sub-menu v-else :key="item.key" :menu-info="item" />
    </template>
  </a-sub-menu>
</template>
<script>
import { IconEmail, IconBarChart } from '@arco-design/web-vue/es/icon'
export default {
  props: ["menuInfo"],
  components: {
    IconEmail,
    IconBarChart
  }
};
</script>