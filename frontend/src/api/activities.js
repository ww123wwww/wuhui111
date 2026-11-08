import { request } from '../utils/request'
export const getActivities = (keyword = '') => request('/activities?keyword=' + encodeURIComponent(keyword.trim()))
export const getActivity = id => request(`/activities/${id}`)
export const createActivity = form => request('/admin/activities', { method: 'POST', body: form })
export const updateActivity = (id, form) => request(`/admin/activities/${id}`, { method: 'PUT', body: form })
export const deleteActivity = id => request(`/admin/activities/${id}`, { method: 'DELETE' })
export const publishActivity = id => request(`/admin/activities/${id}/publish`, { method: 'POST' })
export const cancelActivity = id => request(`/admin/activities/${id}/cancel`, { method: 'POST' })
