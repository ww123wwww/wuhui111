<script setup>
import { reactive, ref } from 'vue'
import { createActivity, updateActivity } from '../../api/activities'
import { shanghaiInput } from '../../utils/activity'
import { useTask } from '../../composables/useTask'
import NoticeBar from '../common/NoticeBar.vue'

const emit = defineEmits(['saved'])
const dialog = ref(null)
const editingId = ref(null)
const form = reactive({ title: '', description: '', location: '', capacity: 30, startTime: '', endTime: '', deadline: '' })
const { busy, message, messageType, run } = useTask()

// 父页面只负责打开表单，不保存本模块的字段或提交逻辑。
function open(activity = null) {
  if (busy.value) return
  message.value = ''
  editingId.value = activity?.id ?? null
  Object.assign(form, activity ? {
    title: activity.title, description: activity.description, location: activity.location,
    capacity: activity.capacity, startTime: activity.startTime.slice(0, 16),
    endTime: activity.endTime.slice(0, 16), deadline: activity.deadline.slice(0, 16)
  } : { title: '', description: '', location: '', capacity: 30,
    startTime: shanghaiInput(7), endTime: shanghaiInput(7, 16), deadline: shanghaiInput(6, 18) })
  dialog.value.showModal()
}
function save() {
  run(async () => {
    const payload = { ...form, capacity: Number(form.capacity) }
    if (editingId.value) await updateActivity(editingId.value, payload)
    else await createActivity(payload)
    dialog.value.close()
    emit('saved')
  })
}
defineExpose({ open })
</script>

<template>
  <dialog ref="dialog" class="modal" @cancel="busy && $event.preventDefault()">
    <form @submit.prevent="save">
      <div class="modal-heading">
        <div><span class="small-label">ACTIVITY EDITOR</span><h2>{{ editingId ? '编辑活动草稿' : '新建活动' }}</h2></div>
        <button type="button" class="icon-button" :disabled="busy" @click="dialog.close()" aria-label="关闭">×</button>
      </div>
      <p class="muted">保存为草稿后，确认信息即可发布。</p>
      <label>活动名称<input v-model="form.title" required maxlength="100" placeholder="例如：Java 后端开发分享会"></label>
      <div class="form-grid">
        <label>活动地点<input v-model="form.location" required maxlength="100"></label>
        <label>活动名额<input v-model="form.capacity" type="number" min="1" max="500" required></label>
        <label>开始时间<input v-model="form.startTime" type="datetime-local" required></label>
        <label>结束时间<input v-model="form.endTime" type="datetime-local" required></label>
      </div>
      <label>报名截止时间<input v-model="form.deadline" type="datetime-local" required></label>
      <label>活动说明<textarea v-model="form.description" rows="4" required maxlength="2000" placeholder="介绍活动内容、参与要求和注意事项"></textarea></label>
      <NoticeBar :text="message" :type="messageType" @close="message = ''" />
      <div class="modal-actions">
        <button type="button" :disabled="busy" @click="dialog.close()">返回</button>
        <button class="primary" :disabled="busy">{{ busy ? '正在保存…' : '保存草稿' }}</button>
      </div>
    </form>
  </dialog>
</template>
