/**
 * 极简事件总线：Vue 3 移除了组件实例上的 $on/$off/$emit，
 * 原来通过 $root 广播的全局参数更新事件改用这里。
 */
const listeners = new Map()

export const eventBus = {
  on(name, fn) {
    if (!listeners.has(name)) {
      listeners.set(name, new Set())
    }
    listeners.get(name).add(fn)
  },
  off(name, fn) {
    listeners.get(name)?.delete(fn)
  },
  emit(name, payload) {
    listeners.get(name)?.forEach(fn => fn(payload))
  }
}

export default eventBus
