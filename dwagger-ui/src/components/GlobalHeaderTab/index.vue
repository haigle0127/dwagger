<template>
  <a-layout-content class="dwagger-body-content">
    <a-tabs :value="activeKey" type="editable-card" class="dwagger-tab">
      <a-tab-pane v-for="pane in panes" :title="pane.title" :key="pane.key" :closable="pane.closable">
        <component :is="pane.content"></component>
      </a-tab-pane>
    </a-tabs>

  </a-layout-content>

</template>
<script>
export default {
  props: {
    panes: {
      type: Array
    },
    activeKey: {
      type: String
    }
  }
};
</script>
