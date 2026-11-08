// 展示规则只用于提示，最终权限与报名判断由 Java 后端执行。
export const statusText = status => ({ DRAFT: '草稿', PUBLISHED: '已发布', CANCELLED: '已取消', REGISTERED: '已报名' }[status] || status)
export const dateText = value => value ? value.replace('T', ' ').slice(0, 16) : '—'
export const isExpired = value => Date.parse(value + '+08:00') <= Date.now()
export const registrationText = row => row.activityStatus === 'CANCELLED' ? '活动已取消' : statusText(row.status)
export function registrationDisabled(activity, registered) {
  return activity.status !== 'PUBLISHED' || registered || isExpired(activity.deadline) || activity.registeredCount >= activity.capacity
}
export function registrationButtonText(activity, registered) {
  if (activity.status === 'CANCELLED') return '活动已取消'
  if (registered) return '已报名'
  if (isExpired(activity.deadline)) return '报名已截止'
  if (activity.registeredCount >= activity.capacity) return '名额已满'
  return '立即报名'
}
export function shanghaiInput(days, hours = 14) {
  const date = new Date(Date.now() + (8 + days * 24) * 3600000)
  return date.toISOString().slice(0, 10) + 'T' + String(hours).padStart(2, '0') + ':00'
}
