<script setup>
import { ref } from 'vue'
import { getActivityRegistrations } from '../../api/registrations'
import { dateText, registrationText } from '../../utils/activity'
import { useTask } from '../../composables/useTask'
import NoticeBar from '../common/NoticeBar.vue'

const dialog = ref(null)
const title = ref('')
const rows = ref([])
const { busy, message, messageType, run } = useTask()
function open(activity) {
  if (busy.value) return
  title.value = activity.title
  rows.value = []
  dialog.value.showModal()
  run(async () => { rows.value = await getActivityRegistrations(activity.id) })
}
defineExpose({ open })
</script>

<template>
  <dialog ref="dialog" class="modal wide-modal" @cancel="busy && $event.preventDefault()">
    <div class="modal-heading"><div><span class="small-label">REGISTRATION LIST</span><h2>{{ title }} · 报名名单</h2></div><button class="icon-button" :disabled="busy" @click="dialog.close()" aria-label="关闭">×</button></div>
    <NoticeBar :text="message" :type="messageType" @close="message = ''" />
    <p v-if="busy" class="loading" role="status">正在加载名单…</p>
    <div class="table-wrap">
      <table>
        <thead><tr><th>姓名</th><th>账号</th><th>报名时间</th><th>状态</th></tr></thead>
        <tbody>
          <tr v-for="row in rows" :key="row.id"><td>{{ row.displayName }}</td><td>{{ row.username }}</td><td>{{ dateText(row.registeredAt) }}</td><td>{{ registrationText(row) }}</td></tr>
          <tr v-if="!busy && !message && !rows.length"><td colspan="4" class="empty">暂无报名记录</td></tr>
        </tbody>
      </table>
    </div>
    <p class="footnote">名单保留取消记录，便于核对活动历史。</p>
  </dialog>
</template>
