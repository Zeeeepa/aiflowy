<script setup lang="ts">
import { provide, ref, watch } from 'vue';

import { cn } from '@aiflowy/utils';

import {
  ElAside,
  ElButton,
  ElContainer,
  ElHeader,
  ElIcon,
  ElMain,
} from 'element-plus';

import { api } from '#/api/request';
import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';
import AssistantIcon from '#/components/icons/AssistantIcon.vue';
import ChatIcon from '#/components/icons/ChatIcon.vue';

interface Props {
  bot: any;
  onMessageList?: (list: any[]) => void;
}
const props = defineProps<Props>();
const sessionList = ref<any>([]);
const currentSession = ref<any>({});
watch(
  () => props.bot.id,
  () => {
    getSessionList(true);
  },
);
defineExpose({
  getSessionList,
});

function getSessionList(resetSession = false) {
  api
    .get('/userCenter/conversation/list', {
      params: {
        botId: props.bot.id,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        sessionList.value = res.data;
        if (resetSession) {
          currentSession.value = {};
        }
      }
    });
}
provide('getSessionList', getSessionList);
function addSession() {
  const data = {
    botId: props.bot.id,
    title: '新对话',
    sessionId: crypto.randomUUID(),
  };
  sessionList.value.push(data);
}
function clickSession(session: any) {
  currentSession.value = session;
  getMessageList();
}
function getMessageList() {
  api
    .get('/userCenter/aiBotMessage/getMessages', {
      params: {
        botId: props.bot.id,
        sessionId: currentSession.value.sessionId,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        props.onMessageList?.(res.data);
      }
    });
}
</script>

<template>
  <ElContainer class="border-border h-full rounded-lg border">
    <ElAside width="287px" class="border-border border-r p-6">
      <Card class="max-w-max p-0">
        <!-- <CardAvatar /> -->
        <ElIcon class="!text-primary" :size="36">
          <AssistantIcon />
        </ElIcon>
        <CardContent>
          <CardTitle>{{ bot.title }}</CardTitle>
          <CardDescription>{{ bot.description }}</CardDescription>
        </CardContent>
      </Card>
      <ElButton
        class="mt-6 !h-10 w-full !text-sm"
        type="primary"
        :icon="ChatIcon"
        plain
        @click="addSession"
      >
        新建会话
      </ElButton>
      <div class="mt-8">
        <div
          v-for="session in sessionList"
          :key="session.sessionId"
          :class="
            cn(
              'flex cursor-pointer items-center justify-between rounded-lg px-5 py-2.5 text-sm',
              currentSession.sessionId === session.sessionId
                ? 'bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                : 'hover:bg-[hsl(var(--accent))]',
            )
          "
          @click="clickSession(session)"
        >
          <span
            :class="
              cn(
                'text-foreground',
                currentSession.sessionId === session.sessionId &&
                  'text-primary',
              )
            "
          >
            {{ session.title || '未命名' }}
          </span>
          <span class="text-foreground">
            {{ session.created }}
          </span>
        </div>
      </div>
    </ElAside>
    <ElContainer>
      <ElHeader class="border border-[#E6E9EE] bg-[#FAFAFA]" height="53px">
        <span class="text-base/[53px] font-medium text-[#323233]">
          {{ currentSession.title || '未命名' }}
        </span>
      </ElHeader>
      <ElMain>
        <slot :session-id="currentSession.sessionId"></slot>
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-button :deep(.el-icon) {
  font-size: 20px;
}
</style>
