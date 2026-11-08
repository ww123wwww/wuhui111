<script setup>
import { computed } from 'vue'
const props = defineProps({ keyword: String, status: String, showDraft: Boolean, busy: Boolean })
defineEmits(['update:keyword', 'update:status', 'search'])
const tabs = computed(() => [
  { value: 'ALL', label: '全部' },
  ...(props.showDraft ? [{ value: 'DRAFT', label: '草稿' }] : []),
  { value: 'PUBLISHED', label: '已发布' }, { value: 'CANCELLED', label: '已取消' }
])
</script>

<template>
  <div class="toolbar">
    <div class="tabs">
      <button v-for="tab in tabs" :key="tab.value" :class="{ selected: status === tab.value }" @click="$emit('update:status', tab.value)">{{ tab.label }}</button>
    </div>
    <form class="search" @submit.prevent="$emit('search')">
      <input :value="keyword" @input="$emit('update:keyword', $event.target.value)" placeholder="搜索活动名称" aria-label="搜索活动名称" maxlength="100">
      <button :disabled="busy">搜索</button>
    </form>
  </div>
</template>
