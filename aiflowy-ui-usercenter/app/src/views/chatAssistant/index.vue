<script setup lang="ts">
import { reactive } from 'vue';

import { cn } from '@aiflowy/utils';

import { ElAside, ElContainer, ElMain, ElSpace } from 'element-plus';

import {
  AssistantCard,
  AssistantCardAvatar,
  AssistantCardContent,
  AssistantCardDescription,
  AssistantCardTitle,
} from './components/assistantCard';
import { ChatBubbleList, ChatContainer, ChatSender } from './components/chat';

const recentUsedAssistant = reactive([
  {
    id: 0,
    title: '客服助手',
    description: '智能客服，回答用户问题',
    checked: false,
  },
  {
    id: 1,
    title: '知识助手',
    description: '基于知识库的问答助手dhish…',
    checked: false,
  },
  {
    id: 2,
    title: '业务助手',
    description: '业务咨询和指导',
    checked: false,
  },
]);

const handleSelectAssistant = (id: number) => {
  recentUsedAssistant.forEach((assistant) => {
    assistant.checked = assistant.id === id ? !assistant.checked : false;
  });
};
</script>

<template>
  <ElContainer class="h-full bg-white">
    <ElAside width="283px">
      <ElSpace
        class="p-5"
        direction="vertical"
        alignment="flex-start"
        :size="20"
      >
        <span class="pl-5 text-sm text-[#969799]">最近使用</span>
        <div class="flex h-full flex-col gap-5 overflow-auto">
          <AssistantCard
            v-for="assistant in recentUsedAssistant"
            :key="assistant.id"
            :class="cn(assistant.checked && 'bg-[#F0F4FF]')"
            @click="handleSelectAssistant(assistant.id)"
          >
            <AssistantCardAvatar />
            <AssistantCardContent>
              <AssistantCardTitle
                :class="cn(assistant.checked && 'text-[#0066FF]')"
              >
                {{ assistant.title }}
              </AssistantCardTitle>
              <AssistantCardDescription>
                {{ assistant.description }}
              </AssistantCardDescription>
            </AssistantCardContent>
          </AssistantCard>
        </div>
      </ElSpace>
    </ElAside>
    <ElMain class="p-6 pl-0">
      <ChatContainer>
        <div class="flex h-full flex-col justify-between">
          <ChatBubbleList />
          <ChatSender />
        </div>
      </ChatContainer>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-aside::-webkit-scrollbar {
  display: none;
}
</style>
