<template>
  <a-menu :style="style" class="contextmenu" v-show="visible">
    <a-menu-item :key="item.key" v-for="item in itemList" @click="() => handleClick(item.key)">
      <component :is="iconMap[item.icon]" class="contextmenu-icon" role="menuitemicon" />
      <span>{{ item.text }}</span>
    </a-menu-item>
  </a-menu>
</template>

<script>
import { markRaw } from 'vue'
import { IconCloseCircle, IconLeft, IconRight } from '@arco-design/web-vue/es/icon'

export default {
  name: "Contextmenu",
  components: {
    IconLeft,
    IconRight,
    IconCloseCircle,
  },
  props: {
    visible: {
      type: Boolean,
      required: false,
      default: false
    },
    itemList: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  emits: ['update:visible', 'select'],
  data() {
    return {
      left: 0,
      top: 0,
      target: null,
      onMousedown: null,
      onContextmenu: null,
      // 菜单项的图标名沿用 i18n 里的 ant 命名，这里映射成 Arco 图标
      // 组件对象不能进响应式，否则 Vue 会警告并产生额外代理开销
      iconMap: {
        'caret-left': markRaw(IconLeft),
        'caret-right': markRaw(IconRight),
        'close-circle': markRaw(IconCloseCircle)
      }
    };
  },
  computed: {
    style() {
      return {
        left: this.left + "px",
        top: this.top + "px"
      };
    }
  },
  created() {
    this.onMousedown = e => this.closeMenu(e);
    this.onContextmenu = e => this.setPosition(e);
    window.addEventListener("mousedown", this.onMousedown);
    window.addEventListener("contextmenu", this.onContextmenu);
  },
  unmounted() {
    window.removeEventListener("mousedown", this.onMousedown);
    window.removeEventListener("contextmenu", this.onContextmenu);
  },
  methods: {
    closeMenu(e) {
      // 点在菜单内部时不能提前隐藏，否则 click 还没触发菜单就消失了
      if (e.target.closest && e.target.closest('.contextmenu')) {
        return;
      }
      this.$emit("update:visible", false);
    },
    setPosition(e) {
      this.left = e.clientX;
      this.top = e.clientY;
      this.target = e.target;
    },
    handleClick(key) {
      this.$emit("select", key, this.target);
      this.$emit("update:visible", false);
    }
  }
};
</script>
<style lang="less" scoped>
.contextmenu {
  position: fixed;
  z-index: 10000;
  /* arco-menu 默认 width:100%，fixed 下会铺满整个视口，改成按内容收缩 */
  width: max-content;
  min-width: 132px;
  padding: 4px 0;
  background: #fff;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 21, 41, 0.12);
}

.contextmenu :deep(.arco-menu-item) {
  height: 32px;
  margin: 0;
  padding: 0 12px;
  font-size: 13px;
  line-height: 32px;
  color: #4e5969;

  &:hover {
    color: var(--dwagger-primary);
    background: var(--dwagger-page-bg);
  }
}

.contextmenu-icon {
  margin-right: 8px;
  font-size: 13px;
}
</style>
