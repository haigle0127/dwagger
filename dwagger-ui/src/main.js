import { createApp, h } from 'vue'
import App from './App.vue'
import { setupStore } from './store/index.js'
import router from '@/router/index.js'
import { setupI18n } from '@/lang/index.js'
import { Message } from '@arco-design/web-vue'
// 先加载 Arco 基础样式，再加载项目覆盖样式，保证 dwagger.less 的覆盖生效
import '@arco-design/web-vue/dist/arco.css'
import './style/dwagger.less'

String.prototype.gblen = function () {
  let len = 0
  for (let i = 0; i < this.length; i++) {
    if (this.charCodeAt(i) > 127 || this.charCodeAt(i) == 94) {
      len += 2;
    } else {
      len++;
    }
  }
  return len;
}

String.prototype.startWith = function (str) {
  const reg = new RegExp("^" + str)
  return reg.test(this);
}

/***
 * 自定义图标
 */
import './assets/iconfonts/iconfont.js'
const MyIcon = {
  name: 'MyIcon',
  props: {
    type: {
      type: String,
      default: ''
    }
  },
  render() {
    return h('svg', { class: 'svgfont', 'aria-hidden': 'true' }, [
      h('use', { 'xlink:href': `#${this.type}` })
    ])
  }
}

const app = createApp(App)
app.use(router)
app.component('my-icon', MyIcon)
app.config.globalProperties.$message = Message
setupStore(app)
setupI18n(app)
app.mount('#app')
