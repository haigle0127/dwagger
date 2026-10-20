/**
 * 根据url菜单查找组件名称,用于打开Tab选项卡
 * @param {*} path 路径
 * @param {*} menuData 菜单集合
 */
export function findComponentsByPath(path, menuData) {
  path = decodeURIComponent(path)
  var tmpComp = null;
  for (var i = 0; i < menuData.length; i++) {
    if (menuData[i].path == path) {
      tmpComp = menuData[i];
      break;
    }
    if (tmpComp == null) {
      var chds = menuData[i].children;
      if (chds != undefined && chds !== null) {
        tmpComp = findComponentsByPath(path, chds);
      }
    }
  }
  return tmpComp;
}

/**
 * 表格列配置兼容：把旧版 Ant Design 的 scopedSlots.customRender 转成 Arco 的 slotName
 * @param {*} columns 列配置数组
 * @param {*} slotMap 按 dataIndex 指定插槽名，优先级高于原配置
 */
export function toArcoColumns(columns, slotMap = {}) {
  return (columns || []).map(col => {
    const slotName = slotMap[col.dataIndex] || (col.scopedSlots && col.scopedSlots.customRender);
    if (!slotName) {
      return col;
    }
    const next = Object.assign({}, col, { slotName });
    delete next.scopedSlots;
    return next;
  });
}

/**
 * 根据菜单主键key查找菜单项
 * @param {*} key key值
 * @param {*} menuData 菜单集合
 */
export function findMenuByKey(key, menuData) {
  var tmpComp = null;
  for (var i = 0; i < menuData.length; i++) {
    if (menuData[i].key == key) {
      tmpComp = menuData[i];
      break;
    }
    if (tmpComp == null) {
      var chds = menuData[i].children;
      if (chds != undefined && chds !== null) {
        tmpComp = findMenuByKey(key, chds);
      }
    }
  }
  return tmpComp;
}
