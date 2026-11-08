<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { currentUser, signOut } from '../stores/session'
import { useTask } from '../composables/useTask'
import NoticeBar from '../components/common/NoticeBar.vue'

const router = useRouter()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')
const { busy, message, messageType, run } = useTask()
const menus = computed(() => isAdmin.value
  ? [{ to: '/admin/dashboard', icon: '◫', title: '数据概览' }, { to: '/admin/activities', icon: '▦', title: '活动管理' }]
  : [{ to: '/activities', icon: '▦', title: '活动广场' }, { to: '/my-registrations', icon: '☑', title: '我的报名' }])
function logout() {
  run(async () => { await signOut(); await router.replace('/login') })
}
</script>

<template>
  <div v-if="currentUser" class="app-layout">
    <aside class="sidebar">
      <div class="brand"><span class="brand-icon">校</span><span>校园活动<span class="brand-sub">CAMPUS LIFE</span></span></div>
      <p class="nav-caption">工作空间</p>
      <nav aria-label="主导航">
        <RouterLink v-for="menu in menus" :key="menu.to" :to="menu.to" active-class="active">
          <span>{{ menu.icon }}</span>{{ menu.title }}
        </RouterLink>
      </nav>
      <div class="sidebar-note"><span class="status-dot"></span>{{ isAdmin ? '管理员工作台' : '学生工作台' }}<p>让校园生活井然有序</p></div>
      <div class="profile">
        <span class="avatar">{{ currentUser.displayName.slice(0, 1) }}</span>
        <div><b>{{ currentUser.displayName }}</b><small>{{ currentUser.username }}</small></div>
        <button class="logout" :disabled="busy" @click="logout" title="退出登录" aria-label="退出登录">↪</button>
      </div>
    </aside>
    <section class="workspace">
      <header class="topbar"><span>校园活动 / {{ isAdmin ? '管理工作台' : '学生服务' }}</span><span class="topbar-tag">{{ isAdmin ? '管理员' : '学生' }}</span></header>
      <div class="page-content">
        <NoticeBar :text="message" :type="messageType" @close="message = ''" />
        <RouterView />
        <footer class="page-footer">校园活动报名管理系统 <span>课程演示版 · 所有时间为北京时间</span></footer>
      </div>
    </section>
  </div>
</template>
