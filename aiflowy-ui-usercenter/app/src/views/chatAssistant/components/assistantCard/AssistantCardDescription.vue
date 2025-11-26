<script setup lang="ts">
import { cn } from '@aiflowy/utils';

interface AssistantCardDescriptionProps {
  class?: string;
}

defineOptions({ name: 'AssistantCardDescription' });
const props = defineProps<AssistantCardDescriptionProps>();
</script>

<template>
  <span
    :class="
      cn('overflow-hidden text-ellipsis text-xs text-[#ABB0B9]', props.class)
    "
  >
    <slot></slot>
  </span>
</template>
