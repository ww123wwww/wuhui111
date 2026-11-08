<script setup>
import { reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { homePath, signIn } from '../stores/session'
import { useTask } from '../composables/useTask'
import NoticeBar from '../components/common/NoticeBar.vue'

const router = useRouter()
const route = useRoute()
const form = reactive({ username: '', password: '' })
const { busy, message, messageType, run } = useTask({ redirectOnUnauthorized: false })
const sessionMessage = computed(() => route.query.reason === 'connection'
  ? '无法恢复登录，请确认后端运行后重新登录。'
  : route.query.expired || route.query.reason === 'expired' ? '登录已失效，请重新登录。' : '')
function fillDemo(role) {
  form.username = role === 'ADMIN' ? 'admin' : 'student'
  form.password = role === 'ADMIN' ? 'Admin123!' : 'Student123!'
}
function submit() {
  run(async () => {
    const user = await signIn({ ...form })
    form.password = ''
    await router.replace(homePath(user.role))
  })
}
</script>

<template>
  <main class="login-layout">
    <section class="login-story">
      <div class="brand"><span class="brand-icon">校</span><span>校园活动<span class="brand-sub">CAMPUS LIFE</span></span></div>
      <div class="story-main"><p class="eyebrow">让每一次参与，都有收获</p><h1>在校园里，<br>遇见更多可能。</h1><p>发现感兴趣的活动，留住值得期待的时刻。<br>一个简单、清晰的校园活动报名平台。</p></div>
      <div class="story-grid"><div><b>01</b><span>发现活动</span></div><div><b>02</b><span>一键报名</span></div><div><b>03</b><span>管理参与</span></div></div>
      <small>软件工程课程项目 · 演示版</small>
    </section>
    <section class="login-panel">
      <div class="login-box">
        <span class="small-label">WELCOME BACK</span><h2>欢迎登录</h2><p class="muted">使用演示账号，开始体验校园活动管理。</p>
        <p v-if="sessionMessage && !message" class="muted" role="status">{{ sessionMessage }}</p>
        <NoticeBar :text="message" :type="messageType" @close="message = ''" />
        <form @submit.prevent="submit">
          <label>账号<input v-model="form.username" placeholder="请输入账号" autocomplete="username" required maxlength="30"></label>
          <label>密码<input v-model="form.password" type="password" placeholder="请输入密码" autocomplete="current-password" required maxlength="64"></label>
          <button class="primary wide" :disabled="busy">{{ busy ? '正在连接…' : '登录系统 →' }}</button>
        </form>
        <div class="demo-accounts"><p>快速填入演示账号</p><div><button type="button" :disabled="busy" @click="fillDemo('ADMIN')">管理员</button><button type="button" :disabled="busy" @click="fillDemo('STUDENT')">学生</button></div><small>仅供本地课程演示，登录后可切换账号体验。</small></div>
      </div>
    </section>
  </main>
</template>
