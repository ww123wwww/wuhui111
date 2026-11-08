<script setup>
defineProps({ text: { type: String, default: '' }, type: { type: String, default: 'error' } })
defineEmits(['close'])
</script>

<template>
  <div v-if="text" class="notice" :class="type" role="alert">
    <span>{{ text }}</span>
    <button type="button" aria-label="关闭提示" @click="$emit('close')">×</button>
  </div>
</template>
