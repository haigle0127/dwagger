<template>
  <div class="generate">
    <!--js脚本示例-->
    <div>
      <a-typography-title :heading="6" class="api-title">
        <span v-html="$t('script.JSExample')"></span>
      </a-typography-title>
      <editor-script :value="jsCode"></editor-script>
    </div>
    <!--ts脚本示例-->
    <div>
      <a-typography-title :heading="6" class="api-title">
        <span v-html="$t('script.TSExample')"></span>
      </a-typography-title>
      <editor-script :value="tsCode" :tsMode="true"></editor-script>
    </div>
  </div>
</template>
<script>
import {
  formatApi,
  getComment,
  upperFirstCase,
  getJsFunctionDeclaration,
  getTsInterfaceDeclaration,
  getTsFunctionDeclaration
} from '@/core/ScriptGenerator.js';
import { defineAsyncComponent} from 'vue'

export default {
  name: "ScriptView",
  components: {
    "EditorScript": defineAsyncComponent(() => import('./EditorScript.vue'))
  },
  props: {
    api: {
      type: Object,
      required: true
    },
    swaggerInstance: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      jsCode: '',
      tsCode: ''
    };
  },
  created() {
    // 格式化api数据
    const config = formatApi(this.api);

    let jsComment = '';
    let jsFun = '';
    let interfaceName = '';
    let tsParamsInterface = '';
    let tsResInterface = '';
    let tsComment = '';
    let tsFun = '';
    try {
      jsComment = getComment(this.api);
      jsFun =  getJsFunctionDeclaration(config);

      interfaceName = upperFirstCase(`${config.name}`);
      tsParamsInterface = config.bodyParams[0]
        ? '// 参数接口\n' + getTsInterfaceDeclaration(config.bodyParams[0], `${interfaceName}Params`, true)
        : ''; // 参数interface
      tsResInterface = config.resParam
        ? '// 响应接口\n' + getTsInterfaceDeclaration(config.resParam, `${interfaceName}Res`, false)
        : ''; // 响应interface
      tsComment = getComment(this.api); // 函数注释
      tsFun = getTsFunctionDeclaration(config, interfaceName); // 函数本体
    } catch(err) {
      console.error(err);
    }
    this.jsCode = jsComment + jsFun;
    this.tsCode = tsParamsInterface + tsResInterface + tsComment + tsFun; // 函数本体
  },
  methods: {}
};
</script>
<style lang="less" scoped>
.api-title {
  margin: 20px 0 8px;
  padding-left: 10px;
  border-left: 4px solid var(--dwagger-primary);
  line-height: 1.4;
}
</style>
