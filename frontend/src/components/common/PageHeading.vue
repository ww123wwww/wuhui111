<script setup>
defineProps({ title: String, subtitle: String, label: String })
</script>

<template>
  <div class="page-heading">
    <div><p class="small-label">{{ label }}</p><h1>{{ title }}</h1><p class="muted">{{ subtitle }}</p></div>
    <slot />
  </div>
</template>
