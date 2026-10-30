<template>
  <div class="dwagger-data-type">
    <span v-if="!record.validateStatus">{{text==null||text==''?'string':text}}</span>
    <span v-else class="dwagger-request-validate-jsr">
      <a-tooltip placement="right">
        <template #content>
          <div v-for="pt in validators" :key="pt.key">{{pt.val}}</div>
        </template>
        {{text}}
      </a-tooltip>
    </span>
    <!-- 数组/集合的 Java 类型，OpenAPI 本身区分不了 List 和数组 -->
    <span v-if="record.javaType" class="dwagger-java-type" :title="arrayHint">{{record.javaType}}</span>
  </div>
</template>
<script>
export default {
  name: "DataType",
  props: {
    text: {
      type: String,
      default:"string",
      required: true
    },
    record: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      validators: []
    };
  },
  computed: {
    // 按类型给出传参方式提示：List 用下标，数组/其他集合用同名重复参数
    arrayHint() {
      if (!this.record || !this.record.javaType) {
        return "";
      }
      const key = this.record.arrayStyle === "indexed"
        ? "debug.arrayHintIndexed"
        : "debug.arrayHintRepeated";
      return this.$t(key, { name: this.record.name || "value" });
    }
  },
  created() {
    this.intiValidator();
  },
  methods: {
    intiValidator() {
      var that = this;
      const record = this.record;
      if (record.validateInstance != null) {
        var len = that.getJsonKeyLength(record.validateInstance);
        var _size = 0;
        for (var k in record.validateInstance) {
          var str = k + ":" + record.validateInstance[k];
          that.validators.push({ key: k, val: str });
        }
      }
    },
    getJsonKeyLength(json) {
      var size = 0;
      if (json != null) {
        for (var key in json) {
          if (json.hasOwnProperty(key)) size++;
        }
      }
      return size;
    }
  }
};
</script>
<style lang="less" scoped>
.dwagger-data-type {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
}

.dwagger-java-type {
  display: inline-flex;
  align-items: center;
  padding: 0 8px;
  border-radius: 10px;
  background: var(--dwagger-primary-light);
  color: var(--dwagger-primary);
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
  font-size: 12px;
  font-weight: 600;
  line-height: 20px;
  cursor: help;
}
</style>
