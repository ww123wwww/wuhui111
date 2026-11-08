import { tokenStorage } from './token'

// 公共 HTTP 工具。业务接口地址分别定义在 api 目录中。
export async function request(path, options = {}) {
  const headers = { ...options.headers }
  if (tokenStorage.get()) headers['campus-token'] = tokenStorage.get()
  if (options.body !== undefined) headers['Content-Type'] = 'application/json'
  let response
  try {
    response = await fetch('/api' + path, {
      ...options, headers,
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
