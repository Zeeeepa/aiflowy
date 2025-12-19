<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { cn } from '@aiflowy/utils';

import { ElAside, ElContainer, ElMain } from 'element-plus';

import { api } from '#/api/request';
import AssistantAvatar from '#/components/avatar/Assistant.vue';
import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';
import { ChatBubbleList, ChatContainer, ChatSender } from '#/components/chat';

onMounted(() => {
  getAssistantList();
});
const recentUsedAssistant = ref<any[]>([]);
const currentBot = ref<any>({});
const handleSelectAssistant = (bot: any) => {
  currentBot.value = bot;
  messageList.value = [];
};
function getAssistantList() {
  api.get('/userCenter/aiBotRecentlyUsed/getRecentlyBot').then((res) => {
    recentUsedAssistant.value = res.data;
    if (recentUsedAssistant.value.length > 0) {
      currentBot.value = recentUsedAssistant.value[0];
    }
  });
}
const messageList = ref<any>([]);
function addMessage(message: any) {
  const index = messageList.value.findIndex(
    (item: any) => item.key === message.key,
  );
  if (index === -1) {
    messageList.value.push(message);
  } else {
    messageList.value[index] = message;
  }
}
function setMessageList(messages: any) {
  messageList.value = messages;
}
</script>

<template>
  <ElContainer class="bg-background-deep h-full">
    <ElAside width="283px" class="flex flex-col gap-5 p-5">
      <span class="text-foreground pl-2.5 text-sm">最近使用</span>
      <div class="flex h-full flex-col gap-5 overflow-auto">
        <Card
          v-for="assistant in recentUsedAssistant"
          :key="assistant.id"
          :class="
            cn(
              currentBot.id === assistant.id
                ? 'bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                : 'hover:bg-[hsl(var(--accent))]',
            )
          "
          @click="handleSelectAssistant(assistant)"
        >
          <AssistantAvatar :src="assistant.icon" />
          <CardContent>
            <CardTitle :class="cn(assistant.checked && 'text-primary')">
              {{ assistant.title }}
            </CardTitle>
            <CardDescription>
              {{ assistant.description }}
            </CardDescription>
          </CardContent>
        </Card>
      </div>
    </ElAside>
    <ElMain class="p-6 pl-0">
      <ChatContainer :bot="currentBot" :on-message-list="setMessageList">
        <template #default="{ sessionId }">
          <div class="flex h-full flex-col justify-between">
            <ChatBubbleList :bot="currentBot" :messages="messageList" />
            <ChatSender
              :add-message="addMessage"
              :bot="currentBot"
              :session-id="sessionId"
            />
          </div>
        </template>
      </ChatContainer>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-aside::-webkit-scrollbar {
  display: none;
}
</style>
