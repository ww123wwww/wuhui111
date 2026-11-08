import { createRouter, createWebHashHistory } from 'vue-router'
import { clearSession, homePath, restoreSession } from '../stores/session'
import LoginView from '../views/LoginView.vue'
import AppLayout from '../layouts/AppLayout.vue'
import DashboardView from '../views/DashboardView.vue'
import ActivityManageView from '../views/ActivityManageView.vue'
import ActivityListView from '../views/ActivityListView.vue'
import MyRegistrationView from '../views/MyRegistrationView.vue'

const router = createRouter({
  // Hash 路由刷新时不要求 Java 后端增加页面回退规则。
  history: createWebHashHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    {
      path: '/', component: AppLayout, meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/activities' },
        { path: 'admin/dashboard', name: 'dashboard', component: DashboardView, meta: { role: 'ADMIN' } },
        { path: 'admin/activities', name: 'activity-manage', component: ActivityManageView, meta: { role: 'ADMIN' } },
        { path: 'activities', name: 'activity-list', component: ActivityListView, meta: { role: 'STUDENT' } },
        { path: 'my-registrations', name: 'my-registrations', component: MyRegistrationView, meta: { role: 'STUDENT' } }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ],
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach(async to => {
  let user
  try {
    user = await restoreSession()
  } catch (error) {
    clearSession()
    if (to.name === 'login') return true
    return { name: 'login', query: { reason: error.status === 401 ? 'expired' : 'connection' } }
  }
  if (to.meta.requiresAuth && !user) return { name: 'login' }
  if (to.name === 'login' && user) return homePath(user.role)
  if (user && to.meta.role && to.meta.role !== user.role) return homePath(user.role)
  return true
})
export default router
