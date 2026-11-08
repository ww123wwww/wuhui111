const TOKEN_KEY = 'campus-demo-token'
export const token = {
  get: () => sessionStorage.getItem(TOKEN_KEY),
  set: value => sessionStorage.setItem(TOKEN_KEY, value),
  clear: () => sessionStorage.removeItem(TOKEN_KEY)
}

// 所有请求经过这里，统一附加令牌、解析 JSON、转换错误。
export async function request(path, options = {}) {
  const headers = { ...options.headers }
  if (token.get()) headers['campus-token'] = token.get()
  if (options.body !== undefined) headers['Content-Type'] = 'application/json'
  let response
  try {
    response = await fetch('/api' + path, {
      ...options,
      headers,
      body: options.body === undefined ? undefined : JSON.stringify(options.body)
    })
  } catch {
    throw new Error('无法连接后端，请确认 Spring Boot 已在 8080 端口启动。')
  }
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    const error = new Error(body.message || `请求失败（${response.status}）`)
    error.status = response.status
    throw error
  }
  return body
}
