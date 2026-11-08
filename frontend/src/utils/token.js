const TOKEN_KEY = 'campus-demo-token'

// 只管理令牌存取，不发送请求或控制页面跳转。
export const tokenStorage = {
  get: () => sessionStorage.getItem(TOKEN_KEY),
  set: value => sessionStorage.setItem(TOKEN_KEY, value),
  clear: () => sessionStorage.removeItem(TOKEN_KEY)
}
