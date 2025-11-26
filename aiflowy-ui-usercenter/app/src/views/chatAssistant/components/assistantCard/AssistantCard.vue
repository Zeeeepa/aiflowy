<script setup lang="ts">
import { cn } from '@aiflowy/utils';

interface AssistantCardProps {
  class?: string;
}

defineOptions({ name: 'AssistantCard' });
const props = defineProps<AssistantCardProps>();
</script>

<template>
  <div
    :class="
      cn(
        'flex w-full max-w-60 cursor-pointer items-start gap-2.5 rounded-lg pb-2.5 pl-2.5 pr-5 pt-3.5',
        props.class,
      )
    "
  >
    <slot></slot>
  </div>
</template>
