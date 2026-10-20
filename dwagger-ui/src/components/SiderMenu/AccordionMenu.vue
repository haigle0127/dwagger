<template>
  <div class="accordion-menu">
    <template v-for="item in menuData" :key="item.key">
      <!-- 有子项：折叠面板 -->
      <template v-if="hasChildren(item)">
        <div class="menu-parent" :class="{ 'is-open': isOpen(item.key), 'is-icon-only': collapsed }"
             @click="toggle(item.key)">
          <template v-if="collapsed">
            <my-icon v-if="item.icon" :type="item.icon" class="menu-icon" />
            <span v-else class="menu-abbr">{{ abbr(item.name) }}</span>
          </template>
          <template v-else>
            <my-icon v-if="item.icon" :type="item.icon" class="menu-icon" />
            <span class="menu-name">{{ item.name }}</span>
            <span class="menu-badge" v-if="item.num">{{ item.num }}</span>
            <icon-down class="menu-arrow" />
          </template>
        </div>
        <div class="menu-children" v-show="isOpen(item.key) && !collapsed">
          <accordion-menu :menu-data="item.children" :collapsed="collapsed" :depth="depth + 1"/>
        </div>
      </template>
      <!-- 叶子项：直接跳转 -->
      <router-link v-else :to="item.path" custom v-slot="{ navigate, isExactActive }">
        <div class="menu-leaf" :class="{ 'is-active': isExactActive, 'is-icon-only': collapsed, 'dwagger-menu-api-deprecated': item.deprecated }"
             :style="collapsed ? null : { paddingLeft: 16 + depth * 18 + 'px' }" @click="navigate">
          <template v-if="collapsed">
            <my-icon v-if="item.icon" :type="item.icon" class="menu-icon" />
            <span v-else class="menu-abbr">{{ abbr(item.name) }}</span>
          </template>
          <template v-else>
            <my-icon v-if="item.icon" :type="item.icon" class="menu-icon" />
            <span class="menu-method" :class="'menu-method-' + item.method.toLowerCase()" v-if="item.method">{{ item.method }}</span>
            <span class="menu-name">{{ item.name }}</span>
          </template>
        </div>
      </router-link>
    </template>
  </div>
</template>

<script>
import { defineComponent, reactive, provide, inject, watch } from 'vue'
import { useRoute } from 'vue-router'
import { IconDown } from '@arco-design/web-vue/es/icon'

const OPEN_KEYS = 'dwaggerMenuOpenKeys'

export default defineComponent({
  name: 'AccordionMenu',
  components: { IconDown },
  props: {
    menuData: { type: Array, default: () => [] },
    collapsed: { type: Boolean, default: false },
    depth: { type: Number, default: 0 }
  },
  setup(props) {
    const route = useRoute()
    // 折叠状态只在根组件建一份，递归子组件通过 inject 共享
    let openKeys
    if (props.depth === 0) {
      openKeys = reactive([])
      provide(OPEN_KEYS, openKeys)
    } else {
      openKeys = inject(OPEN_KEYS, reactive([]))
    }

    const isOpen = key => openKeys.includes(key)
    // 手风琴：同一层级只展开一个分组
    const toggle = key => {
      const i = openKeys.indexOf(key)
      if (i > -1) openKeys.splice(i, 1)
      else openKeys.splice(0, openKeys.length, key)
    }
    const abbr = name => (name || '').slice(0, 1).toUpperCase()

    // 找到目标路由所在的祖先分组链，用于自动展开
    const findAncestors = (list, path, chain) => {
      for (const item of list) {
        if (item.path === path) return chain
        if (item.children && item.children.length) {
          const hit = findAncestors(item.children, path, [...chain, item.key])
          if (hit) return hit
        }
      }
      return null
    }

    if (props.depth === 0) {
      watch(() => route.path, () => {
        const chain = findAncestors(props.menuData, route.path, [])
        if (chain && chain.length) {
          chain.forEach(k => { if (!openKeys.includes(k)) openKeys.push(k) })
        }
      }, { immediate: true })
    }

    return { isOpen, toggle, abbr, hasChildren: item => item.children && item.children.some(c => c.name) }
  }
})
</script>

<style lang="less" scoped>
/* 深色侧栏菜单 */
.accordion-menu {
  padding: 8px 0;
}

.menu-parent,
.menu-leaf {
  display: flex;
  align-items: center;
  height: 48px;
  padding: 0 16px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.65);
  cursor: pointer;
  user-select: none;
  transition: color 0.2s, background-color 0.2s;

  &:hover {
    color: #fff;
    background-color: rgba(255, 255, 255, 0.06);
  }
}

/* 折叠态只留图标，整行居中 */
.menu-parent.is-icon-only,
.menu-leaf.is-icon-only {
  justify-content: center;
  padding: 0;
}

.menu-icon {
  margin-right: 10px;
  font-size: 15px;
}

.is-icon-only .menu-icon {
  margin-right: 0;
  font-size: 18px;
}

.menu-parent.is-open {
  color: #fff;

  .menu-arrow {
    transform: rotate(180deg);
  }
}

.menu-arrow {
  margin-left: auto;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
  transition: transform 0.2s;
}

.menu-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 无图标的项在折叠态下显示名称首字母 */
.menu-abbr {
  font-weight: 600;
}

.menu-badge {
  margin-left: 8px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
  color: #fff;
  background: var(--dwagger-primary);
  border-radius: 24px;
}

/* 子层级用更深的底色拉开层次 */
.menu-children {
  background: var(--dwagger-sidebar-sub-bg);

  :deep(.menu-leaf) {
    height: 44px;
    /* 接口列表原本贴在一起，行间距留白后才不显挤 */
    margin: 4px 0;
    color: rgba(255, 255, 255, 0.55);
  }

  :deep(.menu-parent) {
    font-size: 13px;
  }
}

/* 选中项整条主色高亮 */
.menu-leaf.is-active,
.menu-children :deep(.menu-leaf.is-active) {
  color: #fff;
  background: var(--dwagger-primary);
}

/* 接口方法名占一列，用语义色区分请求类型 */
.menu-method {
  flex-shrink: 0;
  min-width: 40px;
  margin-right: 10px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.2px;
  color: rgba(255, 255, 255, 0.55);
}

.menu-method-post {
  color: #5fd4a1;
}

.menu-method-get {
  color: #8ab8ff;
}

.menu-method-put {
  color: #f8b86a;
}

.menu-method-patch {
  color: #5eead4;
}

.menu-method-delete {
  color: #ff8a8a;
}

.menu-method-head {
  color: #c99cff;
}

.menu-method-options {
  color: #9dbcff;
}

.menu-leaf.is-active .menu-method {
  color: #fff;
}
</style>
