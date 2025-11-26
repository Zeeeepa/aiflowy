<script setup lang="ts">
import { cn } from '@aiflowy/utils';

import { ElAvatar } from 'element-plus';

interface AssistantCardAvatarProps {
  class?: string;
  size?: number;
  src?: string;
}

defineOptions({ name: 'AssistantCardAvatar' });
const props = defineProps<AssistantCardAvatarProps>();
</script>

<template>
  <ElAvatar
    :class="cn('shrink-0', props.class)"
    :size="props.size ?? 36"
    :src="props.src"
  />
</template>
