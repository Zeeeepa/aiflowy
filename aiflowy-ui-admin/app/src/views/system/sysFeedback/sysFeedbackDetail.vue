<script setup lang="ts">
import { useRouter } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';

import { ArrowLeft, Check } from '@element-plus/icons-vue';
import { ElButton } from 'element-plus';

const router = useRouter();
</script>

<template>
  <div class="relative p-5">
    <ElButton
      class="absolute left-5 top-5"
      :icon="ArrowLeft"
      @click="router.back()"
    >
      返回
    </ElButton>

    <div class="mx-auto flex w-full max-w-[900px] flex-col gap-4">
      <div
        class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
      >
        <h1 class="text-base font-medium">基础信息</h1>
        <div class="flex items-center gap-16 text-sm">
          <span>问题类型：反馈</span>
          <span>处理状态：已查看</span>
          <span>联系方式：15121387563</span>
          <span>提交时间：2025-09-09 12:00:00</span>
        </div>
      </div>

      <div
        class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
      >
        <h1 class="text-base font-medium">反馈内容</h1>
        <div class="flex items-start text-sm">
          <span class="w-20 shrink-0">问题描述：</span>
          <div class="border-border w-full rounded-lg border p-4"></div>
        </div>
        <div class="flex items-start text-sm">
          <span class="w-20 shrink-0">附件：</span>
          <div class="border-border w-full rounded-lg border p-4"></div>
        </div>
      </div>

      <div class="mx-auto flex items-center">
        <ElButton type="primary" :icon="Check">标记已查看</ElButton>
        <ElButton type="primary">
          <template #icon>
            <IconifyIcon icon="svg:resolved" />
          </template>
          标记已处理
        </ElButton>
      </div>
    </div>
  </div>
</template>
