<script setup>
import { onMounted, ref } from 'vue'
import { getMyRegistrations, cancelRegistration } from '../api/registrations'
import { dateText, registrationText } from '../utils/activity'
import { useTask } from '../composables/useTask'
import PageHeading from '../components/common/PageHeading.vue'
import NoticeBar from '../components/common/NoticeBar.vue'

const rows = ref([])
const { busy, message, messageType, notify, run } = useTask()
async function load() { rows.value = await getMyRegistrations() }
function cancel(row) {
  if (!window.confirm(`确定取消“${row.title}”的报名吗？`)) return
  run(async () => {
    const result = await cancelRegistration(row.activityId)
    await load()
    notify(result.message)
  })
}
onMounted(() => run(load))
</script>

<template>
  <section>
    <PageHeading title="我的报名" label="MY REGISTRATIONS" subtitle="查看你的参与记录，管理已报名的活动。" />
    <NoticeBar :text="message" :type="messageType" @close="message = ''" />
    <p v-if="busy" class="loading" role="status">正在处理，请稍候…</p>
    <div class="panel table-wrap">
      <table>
        <thead><tr><th>活动名称</th><th>开始时间</th><th>报名时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="row in rows" :key="row.id">
            <td><b>{{ row.title }}</b><small>{{ row.location }}</small></td>
            <td>{{ dateText(row.startTime) }}</td><td>{{ dateText(row.registeredAt) }}</td>
            <td><span class="badge" :class="row.activityStatus === 'CANCELLED' ? 'CANCELLED' : row.status">{{ registrationText(row) }}</span></td>
            <td><button v-if="row.status === 'REGISTERED' && row.activityStatus === 'PUBLISHED'" class="text-button danger-text" :disabled="busy" @click="cancel(row)">取消报名</button><span v-else class="muted">—</span></td>
          </tr>
          <tr v-if="!busy && !rows.length"><td colspan="5" class="empty">还没有报名记录，去活动广场看看吧。</td></tr>
        </tbody>
      </table>
    </div>
    <p class="footnote">报名截止后不能取消；活动整体取消时，历史报名记录会保留。</p>
  </section>
</template>
