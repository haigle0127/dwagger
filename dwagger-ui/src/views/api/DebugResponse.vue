<template>
  <a-row class="dwagger-debug-response">
    <a-tabs defaultActiveKey="debugResponse" class="dwagger-debug-response-tabs">
      <!-- Arco 的 tab 栏右侧扩展区叫 extra，旧版 slot 写法不会渲染出状态信息 -->
      <template #extra>
        <a-space v-if="responseStatus" class="dwagger-debug-status" :size="8">
          <a-checkbox :default-checked="responseFieldDescriptionChecked" @change="showFieldDesChange">
            <span>{{ $t('debug.response.showDes') }}</span>
          </a-checkbox>
          <a-divider direction="vertical" />
          <a-typography-text type="secondary">{{ $t('debug.response.code') }}</a-typography-text>
          <a-tag :color="responseCodeColor">{{ responseStatus.code }}</a-tag>
          <a-typography-text type="secondary">{{ $t('debug.response.cost') }}</a-typography-text>
          <a-tag color="gray">{{ responseStatus.cost }}</a-tag>
          <a-typography-text type="secondary">{{ $t('debug.response.size') }}</a-typography-text>
          <a-tag color="gray">{{ responseSizeText }}</a-tag>
        </a-space>
      </template>
      <a-tab-pane :title="i18n.debug.response.content" key="debugResponse">
        <a-row v-if="responseContent">
          <a-row v-if="responseContent.blobFlag">
            <a-image v-if="responseContent.imageFlag" :src="responseContent.blobUrl" />
            <a-button v-else type="text" :href="responseContent.blobUrl" :download="responseContent.blobFileName">
              {{ $t('debug.response.download') }}</a-button>
          </a-row>
          <div :id="'responseEditorContent' + api.id" v-else>
            <editor-debug-show @showDescription="showEditorFieldDescription" @debugEditorChange="debugEditorChange"
              :debugResponse="debugResponse" :value="responseContent.text" :mode="responseContent.mode">
            </editor-debug-show>
          </div>
        </a-row>
        <!-- 未发送也保留响应区骨架，发送后内容原位替换，不再整块上跳 -->
        <a-empty v-else class="dwagger-debug-response-empty"
                 :description="i18n.debug.response.emptyTitle" />
      </a-tab-pane>
      <a-tab-pane title="Raw" key="debugRaw" forceRender :disabled="!responseStatus">
        <a-row class="dwagger-debug-response-mt">
          <a-button :id="'btnDebugCopyRaw' + api.id" type="primary">
            <IconCopy /> <span>{{ $t('debug.response.copy') }}</span>
          </a-button>
        </a-row>
        <a-row class="dwagger-debug-response-mt">
          <a-textarea class="dwagger-debug-raw-textarea" :rows="10" :model-value="responseRawText" />
        </a-row>
      </a-tab-pane>
      <a-tab-pane title="Headers" key="debugHeaders" :disabled="!responseStatus">
        <a-row class="dwagger-debug-response-mt">
          <a-table bordered size="small" :columns="responseHeaderColumn" :pagination="pagination"
            :data="responseHeaders" rowKey="id">
          </a-table>
        </a-row>
      </a-tab-pane>
      <a-tab-pane title="Curl" key="debugCurl" :disabled="!responseStatus">
        <a-row class="dwagger-debug-response-mt">
          <a-button :id="'btnDebugCopyCurl' + api.id" type="primary">
            <IconCopy /> <span>{{ $t('debug.response.copy') }}</span>
          </a-button>
        </a-row>
        <a-row class="dwagger-debug-response-mt">
          <pre class="dwagger-debug-response-curl">{{
              responseCurlText
          }}</pre>
        </a-row>
      </a-tab-pane>
      <a-tab-pane v-if="responseContent != null && responseContent.base64 != null && responseContent.base64 != ''"
        title="Base64Img" key="debugBase64Img">
        <a-row class="dwagger-debug-response-mt">
          <a-image :src="responseContent.base64" />
        </a-row>
      </a-tab-pane>
    </a-tabs>
  </a-row>
</template>
<script>
import KUtils from "@/core/utils";
import { sanitizeHtml } from '@/utils/sanitizeHtml.js'
import ClipboardJS from "clipboard";
import { computed, defineAsyncComponent } from 'vue'
import { useGlobalsStore } from '@/store/modules/global.js'
import { useI18n } from 'vue-i18n'
import { IconCopy } from '@arco-design/web-vue/es/icon'
/* import EditorDebugShow from "./EditorDebugShow"; */

export default {
  props: {
    api: {
      type: Object,
      required: true
    },
    swaggerInstance: {
      type: Object,
      required: true
    },
    debugSend: {
      type: Boolean,
      default: false
    },
    responseHeaders: {
      type: Array
    },
    responseRawText: {
      type: String,
      default: ""
    },
    responseCurlText: {
      type: String,
      default: ""
    },
    responseStatus: {
      type: Object
    },
    responseContent: {
      type: Object
    },
    responseFieldDescriptionChecked: {
      type: Boolean,
      default: true
    }
  },
  components: { IconCopy, 'EditorDebugShow': defineAsyncComponent(() => import('./EditorDebugShow.vue')) },
  setup() {
    const store = useGlobalsStore()
    const language = computed(() => {
      return store.language
    })
    const { messages } = useI18n()
    return {
      language,
      messages
    }
  },
  data() {
    return {
      pagination: false,
      i18n: null,
      base64Image: false,
      debugResponse: true,
      responseHeaderColumn: [

      ]
    };
  },
  watch: {
    language: function (val, oldval) {
      this.initI18n();
    }
  },
  computed: {
    responseCodeColor() {
      // 2xx 绿、4xx/5xx 红，其余用中性色，避免 500 也显示成成功绿
      const code = Number(this.responseStatus && this.responseStatus.code);
      if (code >= 200 && code < 300) {
        return 'green';
      }
      if (code >= 400) {
        return 'red';
      }
      return 'gray';
    },
    responseSizeText() {
      var str = "0 B";
      var responseStatus = this.responseStatus;
      if (responseStatus != null && responseStatus != undefined) {
        var responseSize = responseStatus.size;
        var kbSize = (responseSize / 1024).toFixed(2);
        var mbSize = (responseSize / 1024 / 1024).toFixed(2);
        if (kbSize > 1) {
          str = kbSize + " KB";
        } else if (mbSize > 1) {
          str = mbSize + " MB";
        } else {
          str = responseSize + " B";
        }
      }
      return str;
    }
  },
  created() {
    //  this.resetResponseContent();
    //  this.base64Init();
    this.initI18n();
    this.copyRawText();
    this.copyCurlText();
    //  this.showEditorFieldDescription();
  },
  methods: {
    getCurrentI18nInstance() {
      return this.messages[this.language];
    },
    base64Init() {
      var bimg = KUtils.getValue(this.responseContent, "base64", "", true);
      // console.log(this.responseContent)
      if (KUtils.strNotBlank(bimg)) {
        this.base64Image = true;
      }
    },
    initI18n() {
      // 根据i18n初始化部分参数
      this.i18n = this.getCurrentI18nInstance();
      this.responseHeaderColumn = this.i18n.table.debugResponseHeaderColumns;
    },
    copyRawText() {
      // 复制raw的文本信息
      var that = this;
      var btnId = "btnDebugCopyRaw" + this.api.id;
      var clipboard = new ClipboardJS("#" + btnId, {
        text() {
          return that.responseRawText;
        }
      });
      // 复制Raw成功
      var successMessage = this.i18n.message.copy.raw.success;
      // 复制Raw失败
      var failMessage = this.i18n.message.copy.raw.fail;
      clipboard.on("success", function (e) {
        that.$message.info(successMessage);
      });
      clipboard.on("error", function (e) {
        that.$message.info(failMessage);
      });
    },
    copyCurlText() {
      // 复制curl
      var that = this;
      var btnId = "btnDebugCopyCurl" + this.api.id;
      var clipboard = new ClipboardJS("#" + btnId, {
        text() {
          return that.responseCurlText;
        }
      });
      // 复制Raw成功
      var successMessage = this.i18n.message.copy.curl.success;
      // 复制Raw失败
      var failMessage = this.i18n.message.copy.curl.fail;
      clipboard.on("success", function (e) {
        that.$message.info(successMessage);
      });
      clipboard.on("error", function (e) {
        that.$message.info(failMessage);
      });
    },
    resetResponseContent() {
      if (this.responseContent != null) {
        if (this.responseContent.mode == "json") {
          // json格式特别处理
          const tmpJson = this.responseContent.text;
          // console("特殊处理json");
          // console(tmpJson);
          this.responseContent.text = KUtils.json5stringify(
            KUtils.json5parse(tmpJson)
          );
        }
      }
    },
    showFieldDesChange(e) {
      var flag = e.target.checked;
      this.$emit("debugShowFieldDescriptionChange", flag);
      this.toggleFieldDescription(flag);
    },
    debugEditorChange(value) {
      this.$emit("debugEditorChange", value);
    },
    toggleFieldDescription(flag) {
      var cid = "responseEditorContent" + this.api.id;
      var editorContainer = document.getElementById(cid);
      var fields = editorContainer.getElementsByClassName(
        "dwagger-debug-editor-field-description"
      );
      if (KUtils.arrNotEmpty(fields)) {
        fields.forEach(function (item) {
          if (flag) {
            // 显示
            item.style.display = "block";
          } else {
            // 隐藏
            item.style.display = "none";
          }
        });
      } else {
        this.showEditorFieldAnyWay();
      }
    },
    showEditorFieldDescription(p) {
      // console.log("emit事件-"+p)
      var that = this;
      if (KUtils.checkUndefined(p)) {
        if (parseInt(p) <= 200) {
          // 如果超过200行,不显示属性的字段说明
          // 需要延时1s处理
          setTimeout(() => {
            that.showEditorFieldWait();
          }, 100);
        }
      }

    },
    showEditorFieldWait() {
      // 显示editor字段说明
      if (this.debugSend) {
        if (this.responseFieldDescriptionChecked) {
          if (this.responseContent.mode == "json") {
            // console.log("数据大小："+this.responseStatus.size)
            this.showEditorFieldAnyWay();
          }
        }
      }
    },
    showEditorFieldAnyWay() {
      var swaggerInstance = this.swaggerInstance;
      // console.log("我被调用了--------");
      var responseCode = this.api.getHttpSuccessCodeObject();
      var cid = "responseEditorContent" + this.api.id;
      var editorContainer = document.getElementById(cid);
      // console(cid);
      var paths = [];
      // var aceJsonText = $aceJsonContent.find(".ace_text-layer");
      var aceJsonText = editorContainer.getElementsByClassName(
        "ace_text-layer"
      );
      var acePrintMarginLeft = 0;
      var acePrintMarginObject = editorContainer.querySelector(".ace_print-margin")
      if (KUtils.checkUndefined(acePrintMarginObject) && KUtils.checkUndefined(acePrintMarginObject.style)) {
        acePrintMarginLeft = acePrintMarginObject.style.left;
      }
      // editorContainer.querySelector(".ace_print-margin").style.left;
      if (aceJsonText.length > 0) {
        var aceLineDoms = aceJsonText[0].getElementsByClassName("ace_line");
        for (var i = 0; i < aceLineDoms.length; i++) {
          var item = aceLineDoms[i];
          var $variable = item.getElementsByClassName("ace_variable");
          var key = null;
          if (KUtils.arrNotEmpty($variable)) {
            key = KUtils.toString($variable[0].innerHTML, "").replace(
              /^"(.*)"$/g,
              "$1"
            );
            // 判断是否存在
            var sfd = item.getElementsByClassName(
              "dwagger-debug-editor-field-description"
            );
            if (!KUtils.arrNotEmpty(sfd)) {
              var fieldSpan = document.createElement("span");
              fieldSpan.className = "dwagger-debug-editor-field-description";
              fieldSpan.innerHTML = sanitizeHtml(
                responseCode.responseDescriptionFind(
                  paths,
                  key,
                  swaggerInstance
                )
              );
              fieldSpan.style.left = acePrintMarginLeft;
              item.appendChild(fieldSpan);
            }
          }
          var itemParen = item.getElementsByClassName("ace_paren");
          if (KUtils.arrNotEmpty(itemParen)) {
            // 如果元素大于0,拼装多个
            var parentArrs = [];
            for (var e = 0; e < itemParen.length; e++) {
              parentArrs.push(itemParen[e].innerHTML);
            }
            // var parentText = itemParen[0].innerHTML;
            var parentText = parentArrs.join("");
            switch (parentText) {
              case "[":
              case "{":
                paths.push(key ? key : 0);
                break;
              case "}":
              case "]":
                paths.pop();
                break;
            }
          }
        }
      }
    }
  }
};
</script>
