<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { ArrowLeft, Minus, Plus } from '@element-plus/icons-vue';
import {
  ElAside,
  ElAvatar,
  ElButton,
  ElContainer,
  ElIcon,
  ElMain,
  ElMessage,
  ElSpace,
} from 'element-plus';

import { api } from '#/api/request';
import defaultBotAvatar from '#/assets/defaultBotAvatar.png';
import { Card, CardDescription, CardTitle } from '#/components/card';
import { ChatBubbleList, ChatSender } from '#/components/chat';
import { $t } from '#/locales';

onMounted(async () => {
  getUserUsed();
  getBotDetail();
});
const router = useRouter();
const route = useRoute();
const usedList = ref<any[]>([]);
const botInfo = ref<any>({});
const btnLoading = ref(false);
const sessionId = ref('');
function getUserUsed() {
  api.get('/userCenter/aiBotRecentlyUsed/list').then((res) => {
    usedList.value = res.data.map((item: any) => item.botId);
  });
}
function getBotDetail() {
  api
    .get('/userCenter/aiBot/getDetail', {
      params: {
        id: route.params.id,
      },
    })
    .then((res) => {
      botInfo.value = res.data;
      sessionId.value = crypto.randomUUID();
    });
}
function addBotToRecentlyUsed(botId: any) {
  btnLoading.value = true;
  api
    .post('/userCenter/aiBotRecentlyUsed/save', {
      botId,
    })
    .then((res) => {
      btnLoading.value = false;
      if (res.errorCode === 0) {
        ElMessage.success($t('message.success'));
        getUserUsed();
      }
    });
}
function removeBotFromRecentlyUsed(botId: any) {
  btnLoading.value = true;
  api
    .get('/userCenter/aiBotRecentlyUsed/removeByBotId', {
      params: {
        botId,
      },
    })
    .then((res) => {
      btnLoading.value = false;
      if (res.errorCode === 0) {
        ElMessage.success($t('message.success'));
        getUserUsed();
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
</script>

<template>
  <ElContainer class="bg-background-deep h-full p-6 pr-0">
    <ElMain
      class="border-border bg-background !flex flex-col rounded-xl border !p-6"
    >
      <ElSpace :size="16" class="cursor-pointer" @click="router.back()">
        <ElIcon :size="24"><ArrowLeft /></ElIcon>
        <ElSpace :size="12">
          <ElAvatar :size="36" :src="botInfo.icon || defaultBotAvatar" />
          <h1 class="text-base font-semibold">
            {{ botInfo.title }}
          </h1>
        </ElSpace>
      </ElSpace>
      <div class="relative mx-auto w-full max-w-[884px] flex-1">
        <Card
          v-if="messageList.length === 0"
          class="absolute left-1/2 top-1/2 max-w-[340px] -translate-x-1/2 -translate-y-1/2 flex-col items-center gap-0"
        >
          <ElAvatar :size="64" :src="botInfo.icon || defaultBotAvatar" />
          <CardTitle class="mt-4">{{ botInfo.title }}</CardTitle>
          <CardDescription class="mt-2.5 text-center text-[#566882]">
            {{ botInfo.description }}
          </CardDescription>
        </Card>
        <ChatBubbleList v-else :bot="botInfo" :messages="messageList" />
        <ChatSender
          class="absolute bottom-11 left-0 w-full"
          :add-message="addMessage"
          :bot="botInfo"
          :session-id="sessionId"
        />
      </div>
    </ElMain>
    <ElAside width="407px" class="px-3 pt-10">
      <Card class="mx-auto max-w-[340px] flex-col items-center gap-0">
        <ElAvatar :size="64" :src="botInfo.icon || defaultBotAvatar" />
        <CardTitle class="mt-4">{{ botInfo.title }}</CardTitle>
        <CardDescription class="mt-2.5 text-center text-[#566882]">
          {{ botInfo.description }}
        </CardDescription>
        <ElButton
          v-if="!usedList.includes(botInfo.id)"
          :loading="btnLoading"
          class="mt-8 !h-9 w-full"
          type="primary"
          :icon="Plus"
          @click="addBotToRecentlyUsed(botInfo.id)"
        >
          添加到聊天助理
        </ElButton>
        <ElButton
          v-else
          :loading="btnLoading"
          class="mt-8 !h-9 w-full"
          type="primary"
          :icon="Minus"
          @click="removeBotFromRecentlyUsed(botInfo.id)"
        >
          从聊天助理中移除
        </ElButton>
      </Card>
    </ElAside>
  </ElContainer>
</template>
