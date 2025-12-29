<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import { ref, watch } from 'vue';

import { useDebounceFn } from '@vueuse/core';
import { ElIcon, ElInput } from 'element-plus';

import { updateLlmOptions } from '#/api';
import MagicStaffIcon from '#/components/icons/MagicStaffIcon.vue';
import PromptChoreChatModal from '#/views/ai/bots/pages/setting/PromptChoreChatModal.vue';

const props = defineProps<{
  bot?: BotInfo;
  hasSavePermission?: boolean;
}>();
const systemPrompt = ref(
  '你是一个AI助手，请根据用户的问题给出清晰、准确的回答。',
);
const promptChoreChatModalRef = ref();
watch(
  () => props.bot?.modelOptions.systemPrompt,
  (newPrompt) => {
    if (newPrompt) {
      systemPrompt.value = newPrompt;
    }
  },
  { immediate: true },
);

const handleInput = useDebounceFn((value: string) => {
  updateLlmOptions({
    id: props.bot?.id || '',
    llmOptions: {
      systemPrompt: value,
    },
  });
}, 1000);

const handelReplacePrompt = (value: string) => {
  systemPrompt.value = value;
  handleInput(value);
};
</script>

<template>
  <div class="flex h-full flex-col gap-2 rounded-lg bg-white p-3">
    <div class="flex justify-between">
      <h1 class="text-base font-medium text-[#1A1A1A]">
        系统提示词（System Prompt）
      </h1>
      <button
        @click="promptChoreChatModalRef.open(props.bot?.id, systemPrompt)"
        type="button"
        class="flex items-center gap-0.5 rounded-lg bg-[#F7F7F7] px-3 py-1"
      >
        <ElIcon size="16"><MagicStaffIcon /></ElIcon>
        <span
          class="bg-[linear-gradient(106.75666073298856deg,#F17E47,#D85ABF,#717AFF)] bg-clip-text text-sm text-transparent"
        >
          AI优化
        </span>
      </button>
    </div>
    <ElInput
      class="flex-1"
      type="textarea"
      resize="none"
      v-model="systemPrompt"
      :title="!hasSavePermission ? '你没有配置bot的权限！' : ''"
      :disabled="!hasSavePermission"
      @input="handleInput"
    />

    <!--系统提示词优化模态框-->
    <PromptChoreChatModal
      ref="promptChoreChatModalRef"
      @success="handelReplacePrompt"
    />
  </div>
</template>

<style lang="css" scoped>
.el-textarea :deep(.el-textarea__inner) {
  padding: 12px;
  box-shadow: none;
  height: 100%;
  font-size: 12px;
  line-height: 1.25;
  border-radius: 8px;
  --el-input-text-color: #1a1a1a;
  --el-input-bg-color: #f7f7f7;
}
</style>
