<template>
  <a-layout-content class="dwagger-body-content">
    <a-card class="swaggermodel-card" :bordered="false">
      <a-collapse v-if="modelNames.length" :bordered="false" @change="modelChange">
        <a-collapse-item v-for="model in modelNames" :key="model.id">
          <template #header>
            <a-space :size="8">
              <a-typography-text class="swaggermodel-name">{{ model.name }}</a-typography-text>
              <a-tag v-if="model.load" size="small" :bordered="false">{{ model.data.length }}</a-tag>
            </a-space>
          </template>
          <a-table v-if="model.load" :columns="columns" :data="model.data"
                   :rowKey="(r) => r.id + r.name" size="medium" :pagination="page">
          </a-table>
        </a-collapse-item>
      </a-collapse>
      <a-empty v-else :description="$t('swaggerModel.nodata')" />
    </a-card>
  </a-layout-content>
</template>
<script>
import KUtils from "@/core/utils";
import Constants from "@/store/constants";
import { computed, ref, watch } from 'vue'
import { useGlobalsStore } from '@/store/modules/global.js'
import { useI18n } from 'vue-i18n'
import { useDwaggerModels } from '@/store/dwaggerModels.js'

export default {
  props: {
    data: {
      type: Object
    }
  },
  setup(props) {
    const expanRows = ref(true)
    const page = ref(false)
    const modelNames = ref([])

    const { messages } = useI18n()

    const globalsStore = useGlobalsStore()
    const swagger = computed(() => {
      return globalsStore.swagger
    })

    const columns = computed(() => {
      return  messages.value[globalsStore.language].table.swaggerModelsColumns;
    })

    const DwaggerModels = useDwaggerModels()
    function initModelNames() {
      const key = Constants.globalTreeTableModelParams + props.data.instance.id
      // 根据instance的实例初始化model名称
      const treeTableModel = props.data.instance.swaggerTreeTableModels
      DwaggerModels.setValue(key, treeTableModel);
      if (KUtils.checkUndefined(treeTableModel)) {
        for (const name in treeTableModel) {
          modelNames.value.push({
            id: name,
            name: name,
            // 是否加载过
            load: false,
            data: []
          });
        }
      }
      // console.log(this.modelNames)
    }

    function modelChange(key) {
      // console("当前激活面板key:" + that.activeKey);

      const instanceKey =
          Constants.globalTreeTableModelParams + props.data.instance.id
      // console("chang事件-------");
      // console(key);

      if (KUtils.arrNotEmpty(key)) {
        // 默认要取最后一个
        const lastIndex = key.length - 1
        const id = key[lastIndex]
        // console("key------------");
        modelNames.value.forEach(function (model) {
          if (model.id == id) {
            // console("找到匹配的model了===");
            // 找到该model,判断是否已加载
            if (!model.load) {
              // 未加载的情况下,进行查找数据
              // //console("查找属性");
              // //console(model);
              const modelData = []
              // 得到当前model的原始对象
              // 所有丶属性全部深拷贝,pid设置为-1
              // var originalModel = treeTableModel[model.name];
              let originalModel = DwaggerModels.getByModelName(
                  instanceKey,
                  model.name
              )
              originalModel = swagger.value.analysisDefinitionRefTableModel(props.data.instance.id, originalModel);
              // console.log("初始化完成")
              console.log(originalModel.children);
              // console("查找原始model:" + model.name);
              if (KUtils.checkUndefined(originalModel)) {
                // 存在
                // 查找属性集合
                if (KUtils.arrNotEmpty(originalModel.params)) {
                  originalModel.params.forEach(function (nmd) {
                    // 第一层属性的pid=-1
                    const childrenParam = {
                      children: nmd.children,
                      childrenTypes: nmd.childrenTypes,
                      def: nmd.def,
                      description: nmd.description,
                      enum: nmd.enum,
                      example: nmd.example,
                      id: nmd.id,
                      ignoreFilterName: nmd.ignoreFilterName,
                      in: nmd.in,
                      level: nmd.level,
                      name: nmd.name,
                      parentTypes: nmd.parentTypes,
                      pid: "-1",
                      readOnly: nmd.readOnly,
                      require: nmd.require,
                      schema: nmd.schema,
                      schemaValue: nmd.schemaValue,
                      show: nmd.show,
                      txtValue: nmd.txtValue,
                      type: nmd.type,
                      validateInstance: nmd.validateInstance,
                      validateStatus: nmd.validateStatus,
                      value: nmd.value
                    }
                    modelData.push(childrenParam);
                    // 判断是否存在schema
                  });
                }
              }
              // //console(modelData);
              model.data = modelData;
              model.load = true;
            }
          }
        });

        console.log(modelNames.value)
      }
      // 第二次复制
      expanRows.value = true;
    }

    initModelNames()
    watch(() => modelNames.value, () => {
      for (let model of modelNames.value) {
        console.log(model.data)
      }
    })

    return {
      columns,
      expanRows,
      page,
      modelNames,
      swagger,
      modelChange
    }
  }
};
</script>
<style lang="less" scoped>
.swaggermodel-card {
  width: 98%;
  margin: 20px auto;
}

.swaggermodel-name {
  font-size: 15px;
  font-weight: 600;
}

.swaggermodel-card :deep(.arco-collapse-item) {
  margin-bottom: 8px;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  overflow: hidden;
}

.swaggermodel-card :deep(.arco-collapse-item-header) {
  border-bottom: none;
}
</style>
