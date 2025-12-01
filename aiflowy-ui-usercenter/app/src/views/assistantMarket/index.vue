<script setup lang="ts">
import { useRouter } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';

import { Plus, Search } from '@element-plus/icons-vue';
import {
  ElButton,
  ElCol,
  ElContainer,
  ElHeader,
  ElInput,
  ElMain,
  ElRow,
  ElSpace,
} from 'element-plus';

import {
  Card,
  CardAvatar,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';

const router = useRouter();
const categories = ['推荐', '最新', '智能客服', '学习教育', '聊天陪伴'];
const assistantList = [
  {
    id: 0,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
  {
    id: 1,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
  {
    id: 2,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
  {
    id: 3,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
  {
    id: 4,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
  {
    id: 5,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
  {
    id: 6,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
  {
    id: 7,
    title: 'AI陪伴',
    description: 'AI陪伴专为需要倾诉寻求陪伴和日常互动的',
  },
];
</script>

<template>
  <ElContainer class="h-full bg-[linear-gradient(153deg,white,#EFF8FF)]">
    <ElHeader class="!h-auto !p-8 !pb-0">
      <ElSpace direction="vertical" :size="24" alignment="flex-start">
        <h1 class="text-2xl font-medium text-[#333333]">助理应用市场</h1>
        <ElSpace :size="20">
          <ElInput placeholder="搜索" :prefix-icon="Search" />
          <ElSpace :size="12">
            <button
              type="button"
              class="h-[35px] w-[94px] rounded-3xl border border-[#E6E9EE] text-sm text-[#566882] hover:border-[#0066FF] hover:bg-[rgba(0,102,255,0.08)] hover:text-[#0066FF]"
              v-for="category in categories"
              :key="category"
            >
              {{ category }}
            </button>
          </ElSpace>
        </ElSpace>
      </ElSpace>
    </ElHeader>
    <ElMain class="!px-8">
      <div class="flex flex-wrap items-center gap-5">
        <Card
          class="h-[168px] w-full max-w-[378px] flex-col justify-between rounded-xl border border-[#E6E9EE] p-6 pb-5 pr-4 transition hover:-translate-y-2 hover:shadow-xl"
          v-for="assistant in assistantList"
          :key="assistant.id"
        >
          <CardContent class="gap-3">
            <CardContent class="flex-row items-center gap-3">
              <CardAvatar />
              <CardTitle class="text-[#042A62]">
                {{ assistant.title }}
              </CardTitle>
            </CardContent>
            <CardDescription
              class="line-clamp-2 text-sm text-[#566882]"
              :title="assistant.description"
            >
              {{ assistant.description }}
            </CardDescription>
          </CardContent>
          <ElRow class="w-full" :gutter="16">
            <ElCol :span="12">
              <ElButton
                class="w-full"
                type="primary"
                style="--el-border: none"
                :icon="Plus"
                plain
                @click.stop
              >
                添加到聊天助理
              </ElButton>
            </ElCol>
            <ElCol :span="12">
              <ElButton
                class="w-full"
                type="primary"
                style="--el-border: none"
                plain
                @click="router.push(`/assistantMarket/${assistant.id}`)"
              >
                <template #icon>
                  <IconifyIcon icon="mdi:play-outline" />
                </template>
                立即体验
              </ElButton>
            </ElCol>
          </ElRow>
        </Card>
      </div>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-input :deep(.el-input__wrapper) {
  --el-input-border-radius: 18px;
  --el-input-border-color: #e6e9ee;
}
</style>
