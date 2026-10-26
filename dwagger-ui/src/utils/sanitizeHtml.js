import DOMPurify from 'dompurify'

// 新窗口打开的外链补上 rel，避免被打开的页面通过 window.opener 反向控制当前页
DOMPurify.addHook('afterSanitizeAttributes', (node) => {
  if (node.tagName === 'A' && node.getAttribute('target')) {
    node.setAttribute('rel', 'noopener noreferrer')
  }
})

/**
 * 净化接口文档中的富文本，防止 OpenAPI 描述里携带的脚本被执行
 */
export function sanitizeHtml(value) {
  return DOMPurify.sanitize(value == null ? '' : String(value))
}
