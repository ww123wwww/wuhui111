<script setup>
import { dateText, statusText, registrationDisabled, registrationButtonText } from '../../utils/activity'
defineProps({ activity: { type: Object, required: true }, registered: Boolean, busy: Boolean })
defineEmits(['detail', 'register'])
</script>

<template>
  <article class="activity-card">
    <div class="card-top"><span class="badge" :class="activity.status">{{ statusText(activity.status) }}</span><span class="activity-number">NO. {{ String(activity.id).padStart(3, '0') }}</span></div>
    <h2>{{ activity.title }}</h2>
    <p class="description">{{ activity.description }}</p>
    <div class="activity-meta">
      <span>时间</span><b>{{ dateText(activity.startTime) }}</b>
      <span>地点</span><b>{{ activity.location }}</b>
      <span>截止</span><b>{{ dateText(activity.deadline) }}</b>
    </div>
    <div class="capacity"><span>已报名 {{ activity.registeredCount }} 人</span><span>共 {{ activity.capacity }} 个名额</span></div>
    <progress :value="activity.registeredCount" :max="activity.capacity"></progress>
    <div class="card-actions">
      <button :disabled="busy" @click="$emit('detail', activity.id)">查看详情</button>
      <button class="primary" :disabled="busy || registrationDisabled(activity, registered)" @click="$emit('register', activity.id)">{{ registrationButtonText(activity, registered) }}</button>
    </div>
  </article>
</template>
