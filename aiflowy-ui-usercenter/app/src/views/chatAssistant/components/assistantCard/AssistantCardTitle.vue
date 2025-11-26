<script setup lang="ts">
import { cn } from '@aiflowy/utils';

interface AssistantCardTitleProps {
  class?: string;
}

defineOptions({ name: 'AssistantCardTitle' });
const props = defineProps<AssistantCardTitleProps>();
</script>

<template>
  <span
    :class="
      cn(
        'overflow-hidden text-ellipsis text-base font-semibold text-[#323233]',
        props.class,
      )
    "
  >
    <slot></slot>
  </span>
</template>
