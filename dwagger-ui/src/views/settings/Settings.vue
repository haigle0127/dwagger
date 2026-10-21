<template>
  <a-layout-content class="dwagger-body-content">
    <div class="settingConfig">
      <a-alert type="info" class="setting-tip">
        <span v-html="$t('message.settingTip')"></span>
      </a-alert>
      <a-card class="setting-card" :bordered="false">
        <a-list :bordered="false">
          <a-list-item>
            <a-checkbox @change="checkboxChange('enableRequestCache')" :model-value="settings.enableRequestCache">
              <span v-html="$t('settings.openCache')"></span>
            </a-checkbox>
          </a-list-item>
          <a-list-item>
            <a-checkbox @change="checkboxChange('enableDynamicParameter')" :model-value="settings.enableDynamicParameter">
              <span v-html="$t('settings.dynamicParameter')"></span>
            </a-checkbox>
          </a-list-item>
          <a-list-item>
            <a-space wrap :size="16">
              <a-checkbox @change="checkboxChange('enableFilterMultipartApis')"
                          :model-value="settings.enableFilterMultipartApis">
                <span v-html="$t('settings.apiFilter')"></span>
              </a-checkbox>
              <a-select class="setting-select" @change="filterOptionsChange"
                        :model-value="settings.enableFilterMultipartApiMethodType">
                <a-option value="GET">GET</a-option>
                <a-option value="POST">POST</a-option>
                <a-option value="PUT">PUT</a-option>
                <a-option value="DELETE">DELETE</a-option>
                <a-option value="PATCH">PATCH</a-option>
                <a-option value="OPTIONS">OPTIONS</a-option>
                <a-option value="HEAD">HEAD</a-option>
              </a-select>
            </a-space>
          </a-list-item>
          <a-list-item>
            <a-space wrap :size="16">
              <a-checkbox @change="checkboxChange('enableHost')" :model-value="settings.enableHost">Host</a-checkbox>
              <a-input class="setting-input" @change="hostChange" :model-value="settings.enableHostText" />
            </a-space>
          </a-list-item>
        </a-list>
      </a-card>
    </div>
  </a-layout-content>
</template>
<script>
import Constants from "@/store/constants";
import { computed } from 'vue'
import { useGlobalsStore } from '@/store/modules/global.js'
import { Message as message } from '@arco-design/web-vue';import localStore from '@/store/local.js'
import axios from 'axios'

let instance = null;

export default {
  props: {
    data: {
      type: Object
    }
  },
  setup() {
    const globalsStore = useGlobalsStore()
    const swaggerCurrentInstance = computed(() => {
      return globalsStore.swaggerCurrentInstance
    })
    const language = computed(() => {
      return globalsStore.language
    })
    return {
      swaggerCurrentInstance,
      language
    }
  },
  data() {
    return {
      settings: Constants.defaultSettings,
      hostValue: '',
      labelCol: {
        xs: { span: 21 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 12 }
      }
    };
  },
  beforeCreate() {
    instance = this;
  },
  created() {
    // 读取本地缓存信息,判断是否存在,如果存在即初始化赋值
    const sysHost = this.swaggerCurrentInstance.host
    localStore.getItem(Constants.globalSettingsKey).then(function (val) {
      if (val != null) {
        // 向下兼容,判断是否包含动态参数的配置
        if (
            val["enableDynamicParameter"] == undefined ||
            val["enableDynamicParameter"] == null
        ) {
          val["enableDynamicParameter"] = false;
        }
        instance.settings = val;
        // 判断释放包含host
        if (!val.hasOwnProperty("enableHost")) {
          // 包含
          instance.settings.enableHost = false;
          instance.settings.enableHostText = sysHost;
        } else {
          if (instance.settings.enableHostText == null || instance.settings.enableHostText == '' || instance.settings.enableHostText == undefined) {
            instance.settings.enableHostText = sysHost;
          }
        }
      } else {
        // 赋予系统默认值
        instance.settings.enableHostText = sysHost;

        localStore.setItem(Constants.globalSettingsKey, instance.settings);
      }
    });
  },
  methods: {
    getCurrentI18nInstance() {
      return this.$i18n.messages[this.language];
    },
    hostChange(value) {
      this.settings.enableHostText = value;
      this.saveSettingForLocal();
    },
    checkboxChange(field) {
      const ckStatus = this.settings[field];
      // 需要判断是否存在,向下兼容
      if (ckStatus != undefined && ckStatus != null) {
        this.settings[field] = !ckStatus;
      } else {
        this.settings[field] = true;
      }
      // 判断是否开启增强
      if (field == "enableSwaggerBootstrapUi") {
        if (this.settings.enableSwaggerBootstrapUi) {
          this.validateDwagger();
        } else {
          this.saveSettingForLocal();
        }
      } else if (field == "enableHost") {
        if (this.settings.enableHost) {
          this.validateHost();
        } else {
          this.saveSettingForLocal();
        }
      } else {
        this.saveSettingForLocal();
      }
    },
    filterOptionsChange(value) {
      this.settings.enableFilterMultipartApiMethodType = value;
      this.saveSettingForLocal();
    },
    saveSettingForLocal() {
      localStore.setItem(Constants.globalSettingsKey, instance.settings);
    },
    validateHost() {
      // 校验Host,校验规则如下：
      // 1.Host不能为空
      // 1.如果是http开头,不能为空
      const hostValue = this.settings.enableHostText
      if (hostValue == null || hostValue == '' || hostValue == undefined) {
        this.settings.enableHost = false;
        message.error('Please Check Host Value');
        return false;
      }
      const i18n = this.getCurrentI18nInstance()
      message.success(i18n.message.settingHost, 2);
      this.saveSettingForLocal();
    },
    validateDwagger() {
      // 如果开启增强,请求验证
      let api = this.data.instance.extUrl
      const idx = api.indexOf("/")
      if (idx == 0) {
        api = api.substr(1);
      }
      // api = "/tx/fff/www/xx";
      // console("验证api地址：" + api);
      let flag = true
      axios({
        url: api,
        dataType: "json",
        type: "get"
      })
          .then(function (data) {
            if (data != null) {
              if (data.hasOwnProperty("swaggerBootstrapUi")) {
                var sbu = data["swaggerBootstrapUi"];
                if (sbu != null && sbu != undefined) {
                  if (sbu.hasOwnProperty("errorMsg")) {
                    // 升级后1.8.9的属性
                    var em = sbu["errorMsg"];
                    if (em != null && em != undefined && em != "") {
                      var errMsg =
                          Constants.i18n.zh.message.settings.plusError + em;
                      message.info(errMsg);
                      flag = false;
                    }
                  }
                }
              }
            }
            instance.settings.enableSwaggerBootstrapUi = flag;
            instance.saveSettingForLocal();
          })
          .catch(function (error) {
            message.info(Constants.i18n.zh.message.settings.plusFail);
            flag = false;
            instance.settings.enableSwaggerBootstrapUi = flag;
            instance.saveSettingForLocal();
          });
    },
    saveSettings() {
      // 判断是否过滤
      if (!this.settings.enableFilterMultipartApis) {
        // 未开启过滤的情况下还是POST
        this.settings.enableFilterMultipartApiMethodType = "POST";
      }
      // 是否增强
      if (this.settings.enableSwaggerBootstrapUi) {
        // 如果开启增强,请求验证
        let api = this.data.instance.extUrl
        const idx = api.indexOf("/")
        if (idx == 0) {
          api = api.substr(1);
        }
        api = "/tx/fff/www/xx";
        // console("验证api地址：" + api);
        let flag = true
        axios({
          url: api,
          dataType: "json",
          type: "get"
        })
            .then(function (data) {
              if (data != null) {
                if (data.hasOwnProperty("swaggerBootstrapUi")) {
                  var sbu = data["swaggerBootstrapUi"];
                  if (sbu != null && sbu != undefined) {
                    if (sbu.hasOwnProperty("errorMsg")) {
                      // 升级后1.8.9的属性
                      var em = sbu["errorMsg"];
                      if (em != null && em != undefined && em != "") {
                        var errMsg =
                            Constants.i18n.zh.message.settings.plusError + em;
                        message.info(errMsg);
                        flag = false;
                      }
                    }
                  }
                }
              }
              instance.settings.enableSwaggerBootstrapUi = flag;
              instance.saveSettingForLocal(flag);
            })
            .catch(function (error) {
              message.info(Constants.i18n.zh.message.settings.plusFail);
              flag = false;
              instance.settings.enableSwaggerBootstrapUi = flag;
              instance.saveSettingForLocal(flag);
            });
      } else {
        instance.saveSettingForLocal(true);
      }
    }
  }
};
</script>

<style scoped>
.settingConfig {
  width: 80%;
  max-width: 960px;
  margin: 20px auto;
}

.setting-tip {
  margin-bottom: 16px;
}

.setting-card :deep(.arco-list-item) {
  padding: 16px 4px;
}

.setting-select {
  width: 160px;
}

.setting-input {
  width: 320px;
}
</style>
