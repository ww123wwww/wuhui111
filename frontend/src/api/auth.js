import { request } from '../utils/request'
export const login = form => request('/auth/login', { method: 'POST', body: form })
export const logout = () => request('/auth/logout', { method: 'POST' })
export const getCurrentUser = () => request('/auth/me')
