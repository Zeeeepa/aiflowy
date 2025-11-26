<script setup lang="ts">
import { cn } from '@aiflowy/utils';

interface AssistantCardContentProps {
  class?: string;
}

defineOptions({ name: 'AssistantCardContent' });
const props = defineProps<AssistantCardContentProps>();
</script>

<template>
  <div
    :class="cn('flex flex-col gap-1 overflow-hidden text-nowrap', props.class)"
  >
    <slot></slot>
  </div>
</template>
