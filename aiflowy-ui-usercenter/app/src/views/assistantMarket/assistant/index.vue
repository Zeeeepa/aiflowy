<script setup lang="ts">
import { useRouter } from 'vue-router';

import { ArrowLeft, Avatar, Plus } from '@element-plus/icons-vue';
import {
  ElAside,
  ElAvatar,
  ElButton,
  ElContainer,
  ElIcon,
  ElMain,
  ElSpace,
} from 'element-plus';

import {
  Card,
  CardAvatar,
  CardDescription,
  CardTitle,
} from '#/components/card';
import { ChatSender } from '#/components/chat';

const router = useRouter();
</script>

<template>
  <ElContainer
    class="h-full bg-[linear-gradient(153deg,#FFFFFF,#EFF8FF)] p-6 pr-0"
  >
    <ElMain
      class="!flex flex-col rounded-xl border border-[#E6E9EE] bg-white !p-6"
    >
      <ElSpace :size="16" class="cursor-pointer" @click="router.back()">
        <ElIcon color="#969799" :size="24"><ArrowLeft /></ElIcon>
        <ElSpace :size="12">
          <ElAvatar :size="36" :icon="Avatar" />
          <h1 class="text-base font-semibold text-[#042A62]">AI陪伴</h1>
        </ElSpace>
      </ElSpace>
      <ElMain class="relative mx-auto w-full max-w-[884px] !p-0">
        <Card
          class="absolute left-1/2 top-1/2 max-w-[340px] -translate-x-1/2 -translate-y-1/2 flex-col items-center gap-0"
        >
          <CardAvatar :size="64" />
          <CardTitle class="mt-4 text-[#042A62]">AI陪伴</CardTitle>
          <CardDescription class="mt-2.5 text-center text-[#566882]">
            AI陪伴专为需要倾诉寻求陪伴和日常互动的人群打造，满足用户在孤独、压力或闲暇时的交流需求。
          </CardDescription>
        </Card>
        <ChatSender class="absolute bottom-11 left-0 w-full" />
      </ElMain>
    </ElMain>
    <ElAside width="407px" class="px-3 pt-10">
      <Card class="mx-auto max-w-[340px] flex-col items-center gap-0">
        <CardAvatar :size="64" />
        <CardTitle class="mt-4 text-[#042A62]">AI陪伴</CardTitle>
        <CardDescription class="mt-2.5 text-center text-[#566882]">
          AI陪伴专为需要倾诉寻求陪伴和日常互动的人群打造，满足用户在孤独、压力或闲暇时的交流需求。
        </CardDescription>
        <ElButton class="mt-8 !h-9 w-full" type="primary" :icon="Plus">
          添加到聊天助理
        </ElButton>
      </Card>
    </ElAside>
  </ElContainer>
</template>
