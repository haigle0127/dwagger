<template>
  <div class="dwagger-code-block">
    <editor :value="value" @init="editorInit" :lang="lang" theme="eclipse" width="100%"
      :options="editorOptions" :style="{height: editorHeight + 'px'}" @input="change"></editor>
  </div>
</template>

<script>
import { VAceEditor } from 'vue3-ace-editor'
import ace from "ace-builds";
import { ACE_JSON_MODE } from "@/core/aceJsonMode";
import modeJson from "ace-builds/src-noconflict/mode-json?url";
import modeXml from "ace-builds/src-noconflict/mode-xml?url";
import themeEclipse from "ace-builds/src-noconflict/theme-eclipse?url";
import extLanguageTools from "ace-builds/src-noconflict/ext-language_tools?url";

ace.config.setModuleUrl('ace/mode/json', modeJson)
ace.config.setModuleUrl('ace/mode/xml', modeXml)
ace.config.setModuleUrl('ace/theme/eclipse', themeEclipse)
ace.config.setModuleUrl('ace/ext-language/tools', extLanguageTools)

export default {
  name: "EditorShow",
  components: { editor: VAceEditor },
  props: {
    value: {
      type: [String, Object],
      required: true,
      default: ""
    },
    xmlMode: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  emits: ['showDescription', 'change'],
  data() {
    return {
      lang: ACE_JSON_MODE,
      editor: null,
      editorHeight: 200,
      editorOptions: {
        // 示例区保留行号，固定 gutter 宽度避免初始化时与文本重叠
        showGutter: true,
        fixedWidthGutter: true,
        showPrintMargin: false,
        highlightActiveLine: false,
        displayIndentGuides: false
      }
    };
  },
  methods: {
    change(value) {
      this.$emit("change", value);
    },
    resetEditorHeight() {
      const that = this
      // 重设高度
      setTimeout(() => {
        let length_editor = that.editor.session.getLength()
        if (length_editor == 1) {
          length_editor = 10;
        }
        const lineHeight = that.editor.renderer.lineHeight || 22;
        that.editorHeight = length_editor * lineHeight;
      }, 300);
    },
    editorInit(editor) {
      const that = this
      this.editor = editor;
      // require("brace/ext/language_tools"); //language extension prerequsite...
      // require("brace/mode/json");
      // require("brace/mode/xml");
      if (this.xmlMode) {
        this.lang = "xml";
      }
      // require("brace/theme/eclipse");
      // 保留 4px 左内边距，让行号与文本之间留一点呼吸空间，不再贴死/重叠
      this.editor.renderer.setPadding(4);
      // 若初始化时编辑器藏在隐藏容器里，第一次有尺寸后重算布局，避免 gutter 宽度为 0 导致重叠
      var ro = new ResizeObserver(function (entries) {
        entries.forEach(function (entry) {
          var cr = entry.contentRect;
          if (cr.width > 0 && cr.height > 0) {
            editor.resize(true);
            ro.disconnect();
          }
        });
      });
      ro.observe(editor.container);
      setTimeout(function () { editor.resize(true); }, 300);
      setTimeout(function () { editor.resize(true); }, 800);
      // 重设高度
      this.resetEditorHeight();
      this.editor.renderer.on("afterRender", function () {
        that.$emit("showDescription", "123")
      });
    }
  }
};
</script>
