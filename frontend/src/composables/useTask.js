import { onBeforeUnmount, ref } from 'vue'
import { useRouter } from 'vue-router'
import { clearSession } from '../stores/session'

// 每次调用生成独立的加载和提示状态，不是全局 busy。
export function useTask({ redirectOnUnauthorized = true } = {}) {
  const busy = ref(false)
  const message = ref('')
  const messageType = ref('error')
  const router = useRouter()
  let active = true
  onBeforeUnmount(() => { active = false })

  function notify(text, type = 'success') {
    message.value = text
    messageType.value = type
  }
  async function run(work) {
    if (busy.value) return
    busy.value = true
    message.value = ''
    try {
      await work()
    } catch (error) {
      if (!active) return
      if (error.status === 401 && redirectOnUnauthorized) {
        clearSession()
        await router.replace({ name: 'login', query: { expired: '1' } })
      } else {
        notify(error.message || '操作失败，请重试。', 'error')
      }
    } finally {
      busy.value = false
    }
  }
  return { busy, message, messageType, notify, run }
}
