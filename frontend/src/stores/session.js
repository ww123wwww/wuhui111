import { readonly, ref } from 'vue'
import { getCurrentUser, login, logout } from '../api/auth'
import { tokenStorage } from '../utils/token'

// 全局只共享登录身份；活动列表和表单留在各自模块。
const user = ref(null)
export const currentUser = readonly(user)
export const homePath = role => role === 'ADMIN' ? '/admin/dashboard' : '/activities'

export function clearSession() {
  user.value = null
  tokenStorage.clear()
}
export async function signIn(form) {
  const result = await login(form)
  tokenStorage.set(result.token)
  user.value = result.user
  return result.user
}
export async function restoreSession() {
  if (!tokenStorage.get()) { user.value = null; return null }
  if (!user.value) user.value = await getCurrentUser()
  return user.value
}
export async function signOut() {
  await logout()
  clearSession()
}
