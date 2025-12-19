<script setup lang="ts">
import { ElAvatar, ElIcon } from 'element-plus';

import AssistantIcon from '#/components/icons/AssistantIcon.vue';

defineProps<{
  size?: number;
  src?: string;
}>();
</script>

<template>
  <ElIcon v-if="!src" class="!text-primary" :size="size ?? 36">
    <AssistantIcon />
  </ElIcon>
  <ElAvatar v-else :src="src" :size="size" />
</template>
