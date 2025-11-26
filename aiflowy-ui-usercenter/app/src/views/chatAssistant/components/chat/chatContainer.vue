<script setup lang="ts">
import { createIconifyIcon } from '@aiflowy/icons';

import { ElAside, ElButton, ElContainer, ElHeader, ElMain } from 'element-plus';

import {
  AssistantCard,
  AssistantCardAvatar,
  AssistantCardContent,
  AssistantCardDescription,
  AssistantCardTitle,
} from '../assistantCard';
</script>

<template>
  <ElContainer class="h-full rounded-lg border-2 border-[#E6E9EE]">
    <ElAside width="287px" class="border-r border-[#E6E9EE] p-6">
      <AssistantCard class="max-w-max p-0">
        <AssistantCardAvatar />
        <AssistantCardContent>
          <AssistantCardTitle>客服助手</AssistantCardTitle>
          <AssistantCardDescription>
            智能客服，回答用户问题
          </AssistantCardDescription>
        </AssistantCardContent>
      </AssistantCard>
      <ElButton
        class="mt-6 !h-10 w-full !text-sm"
        color="#0066FF"
        :icon="createIconifyIcon('mdi:chat-plus-outline')"
        plain
      >
        新建会话
      </ElButton>
      <div class="mt-8">
        <div
          class="flex cursor-pointer items-center justify-between rounded-lg bg-[#F0F4FF] px-5 py-2.5 text-sm"
        >
          <span class="text-[#0066FF]">新对话</span>
          <span class="text-[#797B7F]">12:00</span>
        </div>
      </div>
    </ElAside>
    <ElContainer>
      <ElHeader class="border border-[#E6E9EE] bg-[#FAFAFA]" height="53px">
        <span class="text-base/[53px] font-medium text-[#323233]">新对话</span>
      </ElHeader>
      <ElMain>
        <slot></slot>
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-button :deep(.el-icon) {
  font-size: 20px;
}
</style>
