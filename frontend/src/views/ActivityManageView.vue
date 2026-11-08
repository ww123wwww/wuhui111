<script setup>
import { computed, onMounted, ref } from 'vue'
import { getActivities, publishActivity, cancelActivity, deleteActivity } from '../api/activities'
import { dateText, statusText } from '../utils/activity'
import { useTask } from '../composables/useTask'
import PageHeading from '../components/common/PageHeading.vue'
import NoticeBar from '../components/common/NoticeBar.vue'
import ActivityFilters from '../components/activity/ActivityFilters.vue'
import ActivityForm from '../components/activity/ActivityForm.vue'
import ActivityDetail from '../components/activity/ActivityDetail.vue'
import RegistrationList from '../components/registration/RegistrationList.vue'

const activities = ref([])
const keyword = ref('')
const status = ref('ALL')
const editor = ref(null)
const detail = ref(null)
const roster = ref(null)
const { busy, message, messageType, notify, run } = useTask()
const shown = computed(() => activities.value.filter(a => status.value === 'ALL' || a.status === status.value))
async function load() { activities.value = await getActivities(keyword.value) }
function afterSaved() { run(async () => { await load(); notify('草稿已保存。') }) }
function changeActivity(activity, operation) {
  const actions = {
    publish: { label: '发布', call: publishActivity },
    cancel: { label: '取消', call: cancelActivity },
    delete: { label: '删除', call: deleteActivity }
  }
  const selected = actions[operation]
  if (!window.confirm(`确定${selected.label}“${activity.title}”吗？`)) return
  run(async () => { await selected.call(activity.id); await load(); notify(`活动已${selected.label}。`) })
}
onMounted(() => run(load))
</script>

<template>
  <section>
    <PageHeading title="活动管理" label="ACTIVITY MANAGEMENT" subtitle="维护活动草稿，发布安排并查看报名名单。">
      <button class="primary" :disabled="busy" @click="editor.open()">＋ 新建活动</button>
    </PageHeading>
    <NoticeBar :text="message" :type="messageType" @close="message = ''" />
    <ActivityFilters v-model:keyword="keyword" v-model:status="status" show-draft :busy="busy" @search="run(load)" />
    <p v-if="busy" class="loading" role="status">正在处理，请稍候…</p>
    <div class="panel table-wrap">
      <table>
        <thead><tr><th>活动名称</th><th>开始时间 / 截止时间</th><th>名额</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="activity in shown" :key="activity.id">
            <td><b>{{ activity.title }}</b><small>{{ activity.location }}</small></td>
            <td>{{ dateText(activity.startTime) }}<small>截止 {{ dateText(activity.deadline) }}</small></td>
            <td>{{ activity.registeredCount }} / {{ activity.capacity }}</td>
            <td><span class="badge" :class="activity.status">{{ statusText(activity.status) }}</span></td>
            <td><div class="row-actions">
              <button :disabled="busy" @click="detail.open(activity.id)">详情</button>
              <template v-if="activity.status === 'DRAFT'">
                <button :disabled="busy" @click="editor.open(activity)">编辑</button>
                <button :disabled="busy" @click="changeActivity(activity, 'publish')">发布</button>
                <button class="danger-text" :disabled="busy" @click="changeActivity(activity, 'delete')">删除</button>
              </template>
              <template v-else>
                <button :disabled="busy" @click="roster.open(activity)">名单</button>
                <button v-if="activity.status === 'PUBLISHED'" class="danger-text" :disabled="busy" @click="changeActivity(activity, 'cancel')">取消</button>
              </template>
            </div></td>
          </tr>
          <tr v-if="!busy && !shown.length"><td colspan="5" class="empty">暂无符合条件的活动</td></tr>
        </tbody>
      </table>
    </div>
    <ActivityForm ref="editor" @saved="afterSaved" />
    <ActivityDetail ref="detail" />
    <RegistrationList ref="roster" />
  </section>
</template>
