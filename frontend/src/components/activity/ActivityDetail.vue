<script setup>
import { computed, ref } from 'vue'
import { getActivity } from '../../api/activities'
import { registerActivity } from '../../api/registrations'
import { dateText, statusText, registrationDisabled, registrationButtonText } from '../../utils/activity'
import { useTask } from '../../composables/useTask'
import NoticeBar from '../common/NoticeBar.vue'

const props = defineProps({ allowRegistration: Boolean, registeredIds: { type: Array, default: () => [] } })
const emit = defineEmits(['registered'])
const dialog = ref(null)
const activity = ref(null)
const { busy, message, messageType, run } = useTask()
const registered = computed(() => props.registeredIds.includes(activity.value?.id))

function open(id) {
  if (busy.value) return
  activity.value = null
  dialog.value.showModal()
  run(async () => { activity.value = await getActivity(id) })
}
function register() {
  run(async () => {
    const result = await registerActivity(activity.value.id)
    dialog.value.close()
    emit('registered', result.message)
  })
}
defineExpose({ open })
</script>

<template>
  <dialog ref="dialog" class="modal" @cancel="busy && $event.preventDefault()">
    <div class="modal-heading"><h2>{{ activity?.title || '活动详情' }}</h2><button class="icon-button" :disabled="busy" @click="dialog.close()" aria-label="关闭">×</button></div>
    <p v-if="busy" class="loading" role="status">正在处理，请稍候…</p>
    <NoticeBar :text="message" :type="messageType" @close="message = ''" />
    <template v-if="activity">
      <span class="badge" :class="activity.status">{{ statusText(activity.status) }}</span>
      <p class="detail-description">{{ activity.description }}</p>
      <dl class="detail-grid">
        <dt>活动地点</dt><dd>{{ activity.location }}</dd>
        <dt>开始时间</dt><dd>{{ dateText(activity.startTime) }}</dd>
        <dt>结束时间</dt><dd>{{ dateText(activity.endTime) }}</dd>
        <dt>报名截止</dt><dd>{{ dateText(activity.deadline) }}</dd>
        <dt>报名情况</dt><dd>{{ activity.registeredCount }} / {{ activity.capacity }}</dd>
      </dl>
    </template>
    <div class="modal-actions">
      <button :disabled="busy" @click="dialog.close()">关闭</button>
      <button v-if="activity && allowRegistration" class="primary" :disabled="busy || registrationDisabled(activity, registered)" @click="register">{{ registrationButtonText(activity, registered) }}</button>
    </div>
  </dialog>
</template>
