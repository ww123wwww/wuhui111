import { request } from '../utils/request'
export const getDashboardStats = () => request('/admin/stats')
