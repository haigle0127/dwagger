<template>
  <span :class="className + ' headerSearch'" @click="enterSearchMode">
    <a-auto-complete key="AutoComplete" :class="'input '+ (searchMode ? 'show' : '')"
                     :model-value="value" @update:model-value="(v) => onSearchChange(v)"
                     @search="(v) => onSearch && onSearch(v)" @keydown="(e) => onKeyDown(e)"
                     :placeholder="placeholder" allowClear />
    <icon-search @click="buttonSearch" />
  </span>
</template>

<script>
import { onUnmounted, ref } from 'vue'
import { IconSearch } from '@arco-design/web-vue/es/icon'
export default {
  name: "HeaderSearch",
  components: {
    IconSearch
  },
  props: {
    onPressEnter: {
      type: Function
    },
    placeholder: {
      type: String
    },
    className: {
      type: String
    },
    onSearch: {
      type: Function
    },
    onChange: {
      type: Function
    }
  },
  setup(props) {
    let timeout;

    onUnmounted(() => {
      clearTimeout(timeout);
    })

    const value = ref('')
    const searchMode = ref(true)

    return {
      value,
      searchMode,
      onKeyDown(e) {
        if (e.key === "Enter") {
          timeout = setTimeout(() => {
            props.onPressEnter(value.value); // Fix duplicate onPressEnter
          }, 0);
        }
      },
      buttonSearch() {
        props.onPressEnter(value.value); // Fix duplicate onPressEnter
      },
      onSearchChange(v) {
        value.value = v;
        if (props.onChange) {
          props.onChange();
        }
      },
      enterSearchMode() {
        searchMode.value = true;
        // this.input.focus();
      },
      leaveSearchMode() {
        /* this.value = "";
        this.searchMode = false; */
      }
    }
  }
};
</script>

<style lang="less" scoped>
@import "./index.less";
</style>
