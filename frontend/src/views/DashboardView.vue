<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { getDashboardStats } from '../api/dashboard'
import { getActivities } from '../api/activities'
import { dateText, statusText } from '../utils/activity'
import { useTask } from '../composables/useTask'
import PageHeading from '../components/common/PageHeading.vue'
import NoticeBar from '../components/common/NoticeBar.vue'
import ActivityForm from '../components/activity/ActivityForm.vue'
import RegistrationList from '../components/registration/RegistrationList.vue'

const stats = ref({ activities: 0, published: 0, students: 0, registrations: 0 })
const activities = ref([])
const editor = ref(null)
const roster = ref(null)
const { busy, message, messageType, notify, run } = useTask()
async function load() {
  const [summary, list] = await Promise.all([getDashboardStats(), getActivities()])
  stats.value = summary
  activities.value = list.slice(0, 5)
}
function afterSaved() { run(async () => { await load(); notify('草稿已保存，可在活动管理中发布。') }) }
onMounted(() => run(load))
</script>

<template>
  <section>
    <PageHeading title="校园活动概览" label="OVERVIEW" subtitle="管理活动安排，查看报名情况。">
      <button class="primary" :disabled="busy" @click="editor.open()">＋ 新建活动</button>
    </PageHeading>
    <NoticeBar :text="message" :type="messageType" @close="message = ''" />
    <p v-if="busy" class="loading" role="status">正在加载概览…</p>
    <div class="stat-grid">
      <article><span>活动总数</span><b>{{ stats.activities }}</b><small>包含草稿与已取消活动</small></article>
      <article><span>已发布活动</span><b>{{ stats.published }}</b><small>已发布且未取消</small></article>
      <article><span>有效报名</span><b>{{ stats.registrations }}</b><small>已发布活动的报名记录</small></article>
      <article><span>学生账号</span><b>{{ stats.students }}</b><small>已初始化的演示学生</small></article>
    </div>
    <section class="welcome-banner"><div><p class="eyebrow">从一个好活动开始</p><h2>把想法变成下一次校园相聚</h2><p>创建草稿，完善安排，发布后即可接受学生报名。</p></div><button class="light-button" :disabled="busy" @click="editor.open()">创建活动 →</button></section>
    <div class="section-heading"><h2>近期创建</h2><RouterLink class="text-button" to="/admin/activities">查看全部 →</RouterLink></div>
    <div class="panel table-wrap">
      <table>
        <thead><tr><th>活动名称</th><th>活动时间</th><th>报名情况</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="activity in activities" :key="activity.id">
            <td><b>{{ activity.title }}</b><small>{{ activity.location }}</small></td>
            <td>{{ dateText(activity.startTime) }}</td>
            <td>{{ activity.registeredCount }} / {{ activity.capacity }}<progress :value="activity.registeredCount" :max="activity.capacity"></progress></td>
            <td><span class="badge" :class="activity.status">{{ statusText(activity.status) }}</span></td>
            <td><button class="text-button" :disabled="busy" @click="roster.open(activity)">报名名单</button></td>
          </tr>
          <tr v-if="!busy && !activities.length"><td colspan="5" class="empty">暂无活动，点击右上方新建活动。</td></tr>
        </tbody>
      </table>
    </div>
    <ActivityForm ref="editor" @saved="afterSaved" />
    <RegistrationList ref="roster" />
  </section>
</template>
