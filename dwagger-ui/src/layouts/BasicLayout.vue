<template>
  <div class="BasicLayout">
    <a-layout class="ant-layout-has-sider">
      <a-layout-sider :trigger="null" collapsible hide-trigger :collapsed="state.collapsed"
                      :collapsed-width="80"
                      @collapse="handleMenuCollapse"
                      :width="state.menuWidth" class="sider dwagger-sider">
        <div class="dwagger-logo-data" key="logo" v-if="!state.collapsed && settings.enableGroup">
          <a-select :model-value="defaultServiceOption" size="large" :options="serviceOptions"
                    class="dwagger-group-select" @change="serviceChange">
          </a-select>
        </div>
        <div class="dwagger-logo" key="logo" v-if="state.collapsed && settings.enableGroup">
          <a to="/">
            <img :src="state.logo" alt="logo"/>
          </a>
        </div>
        <div :class="settings.enableGroup ? 'dwagger-menu' : 'dwagger-menu-all'">
          <accordion-menu :menuData="state.localMenuData" :collapsed="state.collapsed"/>
        </div>
      </a-layout-sider>
      <!-- <SiderMenu :defaultOption="defaultServiceOption" :serviceOptions="serviceOptions" @menuClick='menuClick' :logo="logo" :menuData="MenuData" :collapsed="collapsed" :location="$route" :onCollapse="handleMenuCollapse" :menuWidth="menuWidth" /> -->
      <a-layout class="dwagger-main">
        <a-layout-header class="dwagger-header">
          <GlobalHeader @searchKey="searchKey" @searchClear="searchClear" :documentTitle="state.documentTitle"
                        :collapsed="state.collapsed" :currentUser="currentUser"
                        :onCollapse="handleMenuCollapse" :onMenuClick="item => handleMenuClick(item)"/>
        </a-layout-header>
        <context-menu :itemList="state.menuItemList" v-model:visible="state.menuVisible" @select="onMenuSelect"/>
        <!-- editable + line 才会渲染每个标签右侧的关闭按钮 -->
        <a-tabs v-model:activeKey="state.activeKey" type="line" editable :show-add-button="false"
                scroll-position="center"
                class="dwagger-tab" @change="tabChange" @delete="remove" @contextmenu="onContextmenu">
          <a-tab-pane v-for="pane in state.panels" :key="pane.key" :title="pane.title" :closable="pane.closable">
            <component :is="componentMap[pane.content]" :data="pane" @childrenMethods="childrenEmitMethod">
            </component>
          </a-tab-pane>
        </a-tabs>
        <a-layout-footer style="padding: 0">
          <GlobalFooter/>
        </a-layout-footer>
      </a-layout>
    </a-layout>
  </div>
</template>
<script setup>
import logo from "@/assets/logo.png";
import GlobalHeader from "@/components/GlobalHeader/index.vue";
import GlobalFooter from "@/components/GlobalFooter/index.vue";
import KUtils from "@/core/utils";
import SwaggerBootstrapUi from "@/core/DwaggerAsync.js";
import { findComponentsByPath, findMenuByKey } from "@/components/utils/DwaggerUtils";
import { urlToList } from "@/components/utils/pathTools";
import AccordionMenu from "@/components/SiderMenu/AccordionMenu.vue";
//右键菜单
import ContextMenu from "@/components/common/ContextMenu.vue";
import constant from "@/store/constants";
import { computed, markRaw, nextTick, onUpdated, reactive, shallowRef, watch, onMounted } from 'vue'
import { useGlobalsStore } from '@/store/modules/global.js'
import { useHeadersStore } from '@/store/modules/header.js'
import { useRoute, useRouter } from 'vue-router'
import localStore from '@/store/local.js'
import { useI18n } from 'vue-i18n'
import Main from '@/views/index/Main.vue'
import Othermarkdown from '@/views/othermarkdown/index.vue'
import Authorize from '@/views/settings/Authorize.vue'
import GlobalParameters from '@/views/settings/GlobalParameters.vue'
import Settings from '@/views/settings/Settings.vue'
import SwaggerModels from '@/views/settings/SwaggerModels.vue'
import OfficelineDocument from '@/views/settings/OfficelineDocument.vue'
import ApiInfo from '@/views/api/index.vue'

const constMenuWidth = 320;

const componentMap = {
  'Main': Main,
  'Othermarkdown': Othermarkdown,
  'Authorize': Authorize,
  'GlobalParameters': GlobalParameters,
  'Settings': Settings,
  'SwaggerModels': SwaggerModels,
  "OfficelineDocument": OfficelineDocument,
  "ApiInfo": ApiInfo,
}

const state = reactive({
  i18n: null,
  logo: logo,
  documentTitle: "",
  menuWidth: constMenuWidth,
  //swagger: null,
  localMenuData: [],
  collapsed: false,
  panels: [],
  panelIndex: 0,
  activeKey: "",
  newTabIndex: 0,
  status: false,
  menuVisible: false,
  /* 右键命中的标签 key，供「关闭左侧/右侧/其它」使用 */
  menuKey: '',
  nextUrl: '',
  nextKey: '',
  menuItemList: [],
  remove
})

const globalsStore = useGlobalsStore()
const headersStore = useHeadersStore()

const { t, messages, locale } = useI18n()

const route = useRoute()

function getI18nFromUrl() {
  const param = route.params;
  let include = false;
  let i18n = "zh-CN";
  if (KUtils.checkUndefined(param)) {
    const i18nFromUrl = param["i18n"];
    if (KUtils.checkUndefined(i18nFromUrl)) {
      const langs = ["zh-CN", "en-US"];
      if (langs.includes(i18nFromUrl)) {
        include = true;
        i18n = i18nFromUrl;
      }
    }
  }
  return {
    include: include,
    i18n: i18n
  }
}

function getPlusStatus() {
  //初始化swagger文档
  const url = route.path;
  let plusFlag = false;
  if (url.indexOf("/plus") != -1) {
    //开启增强
    plusFlag = true;
  }
  return plusFlag;
}

function initSwagger(options) {
  //console.log("初始化Swagger")
  //console.log(options)
  state.i18n = options.i18nInstance;
  const swagger = new SwaggerBootstrapUi(options)
  try {
    swagger.main();
    //this.MenuData=this.swagger.menuData;
    //this.swaggerCurrentInstance=this.swagger.currentInstance;
    //this.$store.dispatch("globals/setMenuData", this.MenuData);
    //缓存cache
    //localStore.setItem(constant.globalGitApiVersionCaches, this.swagger.cacheApis);
    //console.log("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa------------------------")
    //赋值
    // console.log(swagger)
    globalsStore.setSwagger(swagger)
  } catch (e) {
    console.error(e);
  }
  //初始化相关操作
  //初始化菜单数据
  //this.MenuData = getMenuData();
  //数据赋值
  return headersStore.getCurrentUser()
}

const language = computed(() => {
  return globalsStore.language;
})

const settings = computed(() => {
  return globalsStore.settings;
})

function getCurrentI18nInstance() {
  state.i18n = messages.value[language.value];
  return state.i18n;
}

function initSpringDocOpenApi() {
  //该版本是最终打包到dwagger-springdoc-ui的模块
  const i18nParams = getI18nFromUrl();
  let tmpI18n = i18nParams.i18n;
  //console.log(tmpI18n)
  //读取settings
  localStore.getItem(constant.globalSettingsKey).then(settingCache => {
    const settings = getCacheSettings(settingCache)
    //console.log("layout---")
    //console.log(settings)
    //重新赋值是否开启增强
    if (!settings.enableSwaggerBootstrapUi) {
      settings.enableSwaggerBootstrapUi = getPlusStatus();
    }
    settings.language = tmpI18n;
    localStore.setItem(constant.globalSettingsKey, settings);
    localStore.getItem(constant.globalGitApiVersionCaches).then(gitVal => {
      const cacheApis = getCacheGitVersion(gitVal);
      if (i18nParams.include) {
        //写入本地缓存
        globalsStore.setLang(tmpI18n)
        localStore.setItem(constant.globalI18nCache, tmpI18n);
        locale.value = tmpI18n;
        state.enableVersion = settings.enableVersion;
        initSwagger({
          springdoc: true,
          baseSpringFox: true,
          localStore: localStore,
          settings: settings,
          cacheApis: cacheApis,
          routeParams: route.params,
          plus: getPlusStatus(),
          i18n: tmpI18n,
          i18nFlag: i18nParams.include,
          configSupport: false,
          i18nInstance: getCurrentI18nInstance()
        })
      } else {
        //不包含
        //初始化读取i18n的配置，add by xiaoymin 2020-5-16 09:51:51
        localStore.getItem(constant.globalI18nCache).then(i18n => {
          if (KUtils.checkUndefined(i18n)) {
            globalsStore.setLang(i18n)
            tmpI18n = i18n;
          }
          locale.value = tmpI18n;
          state.enableVersion = settings.enableVersion;
          initSwagger({
            springdoc: true,
            baseSpringFox: true,
            localStore: localStore,
            settings: settings,
            cacheApis: cacheApis,
            routeParams: route.params,
            plus: getPlusStatus(),
            i18n: tmpI18n,
            i18nFlag: i18nParams.include,
            configSupport: false,
            i18nInstance: getCurrentI18nInstance()
          })
        })
      }
    })
  })
}

function initDwaggerSpringUi() {
  //该版本是最终打包到dwagger-spring-ui的模块,默认是调用该方法
  const i18nParams = getI18nFromUrl()
  let tmpI18n = i18nParams.i18n
  //console.log(tmpI18n)
  //读取settings
  localStore.getItem(constant.globalSettingsKey).then(settingCache => {
    const settings = getCacheSettings(settingCache)
    //console.log("layout---")
    //console.log(settings)
    //重新赋值是否开启增强
    if (!settings.enableSwaggerBootstrapUi) {
      settings.enableSwaggerBootstrapUi = this.getPlusStatus();
    }
    settings.language = tmpI18n;
    localStore.setItem(constant.globalSettingsKey, settings);
    localStore.getItem(constant.globalGitApiVersionCaches).then(gitVal => {
      const cacheApis = getCacheGitVersion(gitVal)
      if (i18nParams.include) {
        //写入本地缓存
        globalsStore.setLang(tmpI18n)
        localStore.setItem(constant.globalI18nCache, tmpI18n);
        locale.value = tmpI18n
        state.enableVersion = settings.enableVersion;
        initSwagger({
          baseSpringFox: true,
          settings: settings,
          cacheApis: cacheApis,
          routeParams: route.params,
          plus: getPlusStatus(),
          i18n: tmpI18n,
          i18nFlag: i18nParams.include,
          configSupport: false,
          desktop: true,
          i18nInstance: getCurrentI18nInstance()
        })
      } else {
        //不包含
        //console.log("不包含")
        //初始化读取i18n的配置，add by xiaoymin 2020-5-16 09:51:51
        localStore.getItem(constant.globalI18nCache).then(i18n => {
          if (KUtils.checkUndefined(i18n)) {
            globalsStore.setLang(i18n)
            tmpI18n = i18n;
          }
          locale.value = tmpI18n;
          state.enableVersion = settings.enableVersion;
          initSwagger({
            baseSpringFox: true,
            settings: settings,
            cacheApis: cacheApis,
            routeParams: route.params,
            plus: getPlusStatus(),
            i18n: tmpI18n,
            i18nFlag: i18nParams.include,
            configSupport: false,
            desktop: true,
            i18nInstance: getCurrentI18nInstance()
          })
        })
      }
    })
  })
}
function initDwaggerJFinal() {
  //该版本是最终打包到dwagger-jfinal-ui的模块,默认是调用该方法
  const i18nParams = getI18nFromUrl()
  let tmpI18n = i18nParams.i18n
  //console.log(tmpI18n)
  //读取settings
  localStore.getItem(constant.globalSettingsKey).then(settingCache => {
    const settings = getCacheSettings(settingCache)
    //console.log("layout---")
    //console.log(settings)
    //重新赋值是否开启增强
    if (!settings.enableSwaggerBootstrapUi) {
      settings.enableSwaggerBootstrapUi = getPlusStatus();
    }
    settings.language = tmpI18n;
    localStore.setItem(constant.globalSettingsKey, settings);
    localStore.getItem(constant.globalGitApiVersionCaches).then(gitVal => {
      const cacheApis = getCacheGitVersion(gitVal)
      if (i18nParams.include) {
        //写入本地缓存
        globalsStore.setLang(tmpI18n)
        localStore.setItem(constant.globalI18nCache, tmpI18n);
        locale.value = tmpI18n;
        state.enableVersion = settings.enableVersion;
        initSwagger({
          baseSpringFox: true,
          settings: settings,
          cacheApis: cacheApis,
          routeParams: route.params,
          plus: getPlusStatus(),
          i18n: tmpI18n,
          url: 'jf-swagger/swagger-resources',
          i18nFlag: i18nParams.include,
          configSupport: false,
          i18nInstance: getCurrentI18nInstance()
        })
      } else {
        //不包含
        //console.log("不包含")
        //初始化读取i18n的配置，add by xiaoymin 2020-5-16 09:51:51
        localStore.getItem(constant.globalI18nCache).then(i18n => {
          if (KUtils.checkUndefined(i18n)) {
            globalsStore.setLang(i18n)
            tmpI18n = i18n;
          }
          locale.value = tmpI18n;
          state.enableVersion = settings.enableVersion;
          initSwagger({
            baseSpringFox: true,
            settings: settings,
            cacheApis: cacheApis,
            routeParams: route.params,
            plus: getPlusStatus(),
            i18n: tmpI18n,
            url: 'jf-swagger/swagger-resources',
            i18nFlag: i18nParams.include,
            configSupport: false,
            i18nInstance: getCurrentI18nInstance()
          })
        })
      }
    })
  })
}
function initDwaggerFront() {
  //该版本区别于Spring-ui的版本,提供给其它语言来集成dwagger
  const i18nParams = getI18nFromUrl()
  const tmpI18n = i18nParams.i18n
  const swaggerOptions = {
    routeParams: route.params,
    plus: getPlusStatus(),
    i18n: tmpI18n,
    localStore: localStore,
    configSupport: false,
    i18nInstance: getCurrentI18nInstance(),
    //覆盖url地址,多个服务的组合
    url: "/services.json"
  }
  initSwagger(swaggerOptions);
}

onUpdated(() => {

})

function initI18n() {
  //根据i18n初始化部分参数
  getCurrentI18nInstance();
  state.menuItemList = state.i18n.menu.menuItemList;
}

onMounted(() => {
  const appReleaseType = import.meta.env.VITE_RELEASE_APP_TYPE
  console.log("appReleaseType:" + appReleaseType);
  switch (appReleaseType) {
    case 'SpringDocOpenApi':
      initSpringDocOpenApi()
      break;
    case 'DwaggerSpringUi':
      initDwaggerSpringUi()
      break;
    case 'DwaggerJFinal':
      initDwaggerJFinal()
      break;
    case 'DwaggerFront':
      initDwaggerFront()
      break;
    default:
      initSpringDocOpenApi()
      break;
  }

  initI18n()
})

function updateMenuI18n() {
  //根据i18n的切换,更新菜单的显示
  //console.log("根据i18n的切换,更新菜单的显示")
  if (KUtils.arrNotEmpty(state.MenuData)) {
    state.MenuData.forEach(m => {
      if (KUtils.checkUndefined(m.i18n)) {
        m.name = getCurrentI18nInstance().menu[m.i18n];
        if (KUtils.arrNotEmpty(m.children)) {
          m.children.forEach(cm => {
            if (KUtils.checkUndefined(cm.i18n)) {
              cm.name = getCurrentI18nInstance().menu[cm.i18n];
            }
          })
        }
      }
    })
  }
}

function getCacheSettings(val) {
  const that = state
  const defaultSettings = constant.defaultSettings
  const defaultPlusSettings = constant.defaultPlusSettings
  let settings = null
  if (val != undefined && val != null && val != '') {
    if (that.plus) {
      val.enableSwaggerBootstrapUi = defaultPlusSettings.enableSwaggerBootstrapUi
      val.enableRequestCache = defaultPlusSettings.enableRequestCache;
    } //如果本地存在,则使用本地的
    //这里有问题
    settings = Object.assign({}, defaultSettings, val);
  } else {
    if (that.plus) {
      settings = defaultPlusSettings;
    } else {
      //判断是否开启增强
      settings = defaultSettings;
    }
  }
  return settings;
}

function getCacheGitVersion(gitVal) {
  let cacheApis = []
  if (KUtils.strNotBlank(gitVal)) {
    //存在值
    cacheApis = gitVal;
  }
  return cacheApis;
}

const handleMenuClick = (item) => {
  console.log(item)
}
const currentUser = computed(() => {
  return headersStore.userCurrent;
})
const cacheMenuData = computed(() => {
  return globalsStore.currentMenuData;
})
const currentMenuData = computed(() => {
  return globalsStore.currentMenuData;
})
const MenuData = computed(() => {
  //console.log("menuData--------------------------------")
  return globalsStore.currentMenuData;
})
const swagger = computed(() => {
  return globalsStore.swagger;
})
const swaggerCurrentInstance = computed(() => {
  return globalsStore.swaggerCurrentInstance;
})
const serviceOptions = computed(() => {
  return globalsStore.serviceOptions;
})
const defaultServiceOption = computed(() => {
  return globalsStore.defaultServiceOption;
})
const searchClear = () => {
  //搜索输入框清空,菜单还原
  state.localMenuData = currentMenuData.value;
}

watch(() => language.value, () =>{
  initI18n();
  updateMenuI18n();
})
let hasInit = false
watch(() => MenuData.value, () => {
  state.localMenuData = globalsStore.currentMenuData;
  if(!hasInit) {
    openDefaultTabByPath()
    hasInit = true
  }
})

watch(() => swaggerCurrentInstance.value, () => {
  let title = swaggerCurrentInstance.value.title
  if (!title) {
    title = "Dwagger 接口文档";
  }
  state.documentTitle = title;
  window.document.title = title;
})

/* 侧栏菜单改成手风琴后不再触发 BasicLayout 重渲染，这里监听路由同步打开标签页 */
watch(() => route.path, () => {
  openDefaultTabByPath();
})

function searchKey(key) {
  //根据输入搜索
  if (KUtils.strNotBlank(key)) {
    const tmpMenu = []
    const regx = ".*?" + key + ".*"
    //console.log(this.cacheMenuData);
    cacheMenuData.value.forEach(function (menu) {
      if (KUtils.arrNotEmpty(menu.children)) {
        //遍历children
        const tmpChildrens = []
        menu.children.forEach(function (children) {
          const urlflag = KUtils.searchMatch(regx, children.url)
          const sumflag = KUtils.searchMatch(regx, children.name)
          const desflag = KUtils.searchMatch(regx, children.description)
          if (urlflag || sumflag || desflag) {
            tmpChildrens.push(children);
          }
        });
        if (tmpChildrens.length > 0) {
          const tmpObj = {
            groupName: menu.groupName,
            groupId: menu.groupId,
            key: menu.key,
            name: menu.name,
            icon: menu.icon,
            path: menu.path,
            hasNew: menu.hasNew,
            authority: menu.authority,
            children: tmpChildrens
          }
          if (tmpMenu.filter(t => t.key === tmpObj.key).length == 0) {
            tmpMenu.push(tmpObj);
          }
        }
      }
    });
    state.localMenuData = tmpMenu;
  }
}

function serviceChange(value, option) {
  //console("菜单下拉选择");
  //id
  let swaggerIns = swagger.value.selectInstanceByGroupId(value);
  swagger.value.analysisApi(swaggerIns);
  globalsStore.setDefaultService(value);
  //this.defaultServiceOption = value;
  //console(value);
  //console(option);
  setTimeout(() => {
    updateMainTabInstance();
  }, 500);
}
function onMenuSelect(key) {
  const pageKey = state.menuKey;
  if (!pageKey) return;
  switch (key) {
    case "1":
      closeLeft(pageKey);
      break;
    case "2":
      closeRight(pageKey);
      break;
    case "3":
      closeOthers(pageKey);
      break;
    default:
      break;
  }
  // 关闭后把地址栏同步到保留下来的标签
  tabChange(pageKey);
}

/* 右键落在哪个标签上，按 DOM 顺序反查 panels 的 key */
function onContextmenu(e) {
  const tabEl = e.target.closest ? e.target.closest('.arco-tabs-tab') : null;
  if (!tabEl || !tabEl.parentElement) return;
  const index = [...tabEl.parentElement.children].indexOf(tabEl);
  const panel = state.panels[index];
  if (!panel) return;
  state.menuKey = panel.key;
  e.preventDefault();
  state.menuVisible = true;
}

function closeOthers(pageKey) {
  // 主页不可关闭，其余只保留右键命中的那一个
  state.panels = state.panels.filter(panel => panel.key == "kmain" || panel.key == pageKey);
  state.activeKey = pageKey;
}

function closeLeft(pageKey) {
  const index = state.panels.findIndex(panel => panel.key == pageKey);
  // index<=1 表示左侧只剩不可关闭的主页，无需处理
  if (index > 1) {
    state.panels = state.panels.slice(0, 1).concat(state.panels.slice(index));
  }
  state.activeKey = pageKey;
}

function closeRight(pageKey) {
  const index = state.panels.findIndex(panel => panel.key == pageKey);
  if (index > -1) {
    state.panels = state.panels.slice(0, index + 1);
  }
  state.activeKey = pageKey;
}

function childrenEmitMethod(type, data) {
  state[type](data);
}

function addGlobalParameters(data) {
  swaggerCurrentInstance.value.globalParameters.push(data);
}

function getDefaultBrowserPath() {
  let url = route.path
  //console.log("打开默认url:"+url)
  //i18n的情况,忽略掉
  if (url.startsWith("/plus")) {
    url = "/plus";
  }
  if (url.startsWith("/home")) {
    url = "/home";
  }
  if (url == "/plus") {
    //如果是使用的增强地址,替换成主页地址进行查找
    url = "/home";
  }
  return url;
}

function  openDefaultTabByPath() {
  //根据地址栏打开Tab选项卡
  const panes = state.panels;
  /* var url = this.$route.path;
  console.log("打开默认url:"+url)
  if (url == "/plus") {
    //如果是使用的增强地址,替换成主页地址进行查找
    url = "/home";
  } */
  const url = getDefaultBrowserPath()
  //console.log("url 1")
  if (state.nextUrl === url) {
    //console.log("nextUrl eq--return..")
    return false;
  }
  //console.log("url 2")
  //var menu = findComponentsByPath(url, this.MenuData);
  const menu = findComponentsByPath(url, swagger.value.globalMenuDatas)
  if (menu != null) {
    //判断是否已经默认打开了主页面板
    const indexSize = state.panels.filter(tab => tab.key == "kmain");
    if (indexSize == 0) {
      panes.push({
        /* title: "主页", */
        title: getCurrentI18nInstance().menu.home,
        component: "Main",
        content: 'Main',
        key: "kmain",
        instance: swaggerCurrentInstance.value,
        closable: false
      });
    }
    const tabKeys = panes.map(tab => tab.key);

    //判断tab是否已加载
    if (tabKeys.indexOf(menu.key) == -1) {
      //console(menu);
      //console(this.swaggerCurrentInstance);
      //不存在,添加，否则直接选中tab即可
      panes.push({
        title: menu.tabName ? menu.tabName : menu.name,
        content: menu.component,
        key: menu.key,
        instance: swaggerCurrentInstance.value,
        closable: menu.key != "kmain"
      });
      state.panels = panes;
      //等新标签渲染出来再选中，Arco 才能量到它并自动横向滚动到可见位置
      nextTick(() => {
        state.activeKey = menu.key;
        freePanelMemory(menu.key);
      });
    } else {
      state.activeKey = menu.key;
      freePanelMemory(menu.key);
    }
    state.nextUrl = url;
    state.nextKey = menu.key;
  } else {
    //主页
    state.activeKey = "kmain";
    state.nextKey = "kmain";
    updateMainTabInstance();
    freePanelMemory(state.activeKey);
  }
  //this.watchPathMenuSelect();
}

function freePanelMemory(activeKey) {
  state.panels.forEach(panel => {
    if (panel.key == activeKey) {
      panel.instance = swaggerCurrentInstance.value;
    } else {
      panel.instance = null;
    }
  })
}

function updateMainTabInstance() {
  //修改kmain中的instance示例对象
  state.panels.forEach(function (panel) {
    if (panel.key == "kmain") {
      panel.instance = swaggerCurrentInstance.value;
    }
  });
}

function menuClick(key) {
  //console("菜单click");
  //console(key);
  const panes = state.panels;
  //console(panes);
  const tabKeys = state.panels.map(tab => tab.key);
  // var menu = findComponentsByPath(url, this.MenuData);
  const menu = findMenuByKey(key, MenuData.value)
  //console(menu);
  if (menu != null) {
    //判断tab是否已加载
    if (tabKeys.indexOf(menu.key) == -1) {
      //不存在,添加，否则直接选中tab即可
      panes.push({
        title: menu.name,
        content: menu.component,
        key: menu.key,
        closable: true
      });
      state.panels = panes;
    }
    state.activeKey = menu.key;
  } else {
    //主页
    state.activeKey = "kmain";
    updateMainTabInstance();
  }
}

const router = useRouter()
function tabChange(targetKey) {
  //console("tabchange------------");
  //console(targetKey);
  //var menu = findMenuByKey(targetKey, this.MenuData);
  const menu = findMenuByKey(targetKey, swagger.value.globalMenuDatas)
  //console(menu);
  if (menu != null) {
    const path = menu.path
    router.push({ path: path });
  } else {
    router.push({ path: "/" });
  }
}

function remove(targetKey) {
  let activeKey = state.activeKey;
  const flag = targetKey == activeKey;
  let lastIndex;
  state.panels.forEach((pane, i) => {
    if (pane.key === targetKey) {
      lastIndex = i - 1;
    }
  });
  const panes = state.panels.filter(pane => pane.key !== targetKey);
  if (panes.length && activeKey === targetKey) {
    if (lastIndex >= 0) {
      activeKey = panes[lastIndex].key;
    } else {
      activeKey = panes[0].key;
    }
  }
  state.panels = panes;
  state.activeKey = activeKey;
  //判断是否是当前页
  if (flag) {
    tabChange(activeKey);
  }
}

function handleMenuCollapse() {
  state.collapsed = !state.collapsed;
  // 折叠时侧栏收窄到只放图标的宽度
  state.menuWidth = state.collapsed ? 80 : constMenuWidth;
}

</script>

<style lang="less" scoped>
/* 侧栏整体：浅色主题 */
.dwagger-sider {
  background: var(--dwagger-sidebar-bg) !important;
}

/* 侧栏内部改成 flex 列：分组选择器固定高度，菜单占满剩余空间 */
.dwagger-sider :deep(.arco-layout-sider-children) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 深色底上的分组选择器用白底，避免灰底和侧栏糊在一起 */
.dwagger-group-select {
  width: 100%;

  :deep(.arco-select-view) {
    background: #fff;
    border-color: #fff;
  }
}

/* 右侧主区域：内容整体白底 */
.dwagger-main {
  background: #fff;
  /* flex 子项默认不能小于内容最小宽度，表格会把主区撑出横向滚动，这里放开收缩 */
  min-width: 0;
}

.dwagger-header {
  padding: 0;
  height: 64px;
  flex-shrink: 0;
  background: #fff;
}

/* 标签栏与内容区的样式统一放在 dwagger.less，这里只做布局级修正 */
:deep(.dwagger-tab) {
  flex: 1;
  min-height: 0;
}
</style>
