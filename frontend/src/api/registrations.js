import { request } from '../utils/request'
export const registerActivity = id => request(`/activities/${id}/registrations`, { method: 'POST' })
export const cancelRegistration = id => request(`/activities/${id}/registrations/me`, { method: 'DELETE' })
export const getMyRegistrations = () => request('/me/registrations')
export const getActivityRegistrations = id => request(`/admin/activities/${id}/registrations`)
