<script setup>
import { computed, onMounted, ref } from 'vue'
import { getActivities } from '../api/activities'
import { getMyRegistrations, registerActivity } from '../api/registrations'
import { useTask } from '../composables/useTask'
import PageHeading from '../components/common/PageHeading.vue'
import NoticeBar from '../components/common/NoticeBar.vue'
import ActivityFilters from '../components/activity/ActivityFilters.vue'
import ActivityCard from '../components/activity/ActivityCard.vue'
import ActivityDetail from '../components/activity/ActivityDetail.vue'

const activities = ref([])
const registrations = ref([])
const keyword = ref('')
const status = ref('ALL')
const detail = ref(null)
const { busy, message, messageType, notify, run } = useTask()
const shown = computed(() => activities.value.filter(a => status.value === 'ALL' || a.status === status.value))
const registeredIds = computed(() => registrations.value.filter(r => r.status === 'REGISTERED').map(r => r.activityId))
async function load() {
  const [list, mine] = await Promise.all([getActivities(keyword.value), getMyRegistrations()])
  activities.value = list
  registrations.value = mine
}
function register(id) {
  run(async () => { const result = await registerActivity(id); await load(); notify(result.message) })
}
function afterRegistered(text) { run(async () => { await load(); notify(text) }) }
onMounted(() => run(load))
</script>

<template>
  <section>
    <PageHeading title="发现校园活动" label="CAMPUS ACTIVITIES" subtitle="选择感兴趣的活动，安排你的校园时光。" />
    <NoticeBar :text="message" :type="messageType" @close="message = ''" />
    <ActivityFilters v-model:keyword="keyword" v-model:status="status" :busy="busy" @search="run(load)" />
    <p v-if="busy" class="loading" role="status">正在处理，请稍候…</p>
    <div class="activity-grid">
      <ActivityCard v-for="activity in shown" :key="activity.id" :activity="activity" :registered="registeredIds.includes(activity.id)" :busy="busy" @detail="detail.open($event)" @register="register" />
      <div v-if="!busy && !shown.length" class="panel empty">暂无符合条件的活动，换个关键词试试。</div>
    </div>
    <ActivityDetail ref="detail" allow-registration :registered-ids="registeredIds" @registered="afterRegistered" />
  </section>
</template>
