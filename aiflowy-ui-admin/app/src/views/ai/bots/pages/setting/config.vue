<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue';
import {
  ElCol,
  ElCollapse,
  ElCollapseItem,
  ElIcon,
  ElInputNumber,
  ElRow,
  ElSelect,
  ElSlider,
} from 'element-plus';
</script>

<template>
  <div class="flex flex-col gap-3">
    <!-- 大模型 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">大模型</h1>
      <div
        class="flex w-full flex-col justify-between gap-1 rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElSelect />
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">温度</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">TopK</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">TopP</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">最大回复长度</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">携带历史条数</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
      </div>
    </div>

    <!-- 技能 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">技能</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="工作流">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>工作流</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
          <ElCollapseItem title="知识库">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>知识库</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
          <ElCollapseItem title="插件">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>插件</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 对话设置 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">对话设置</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="问题预设">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>问题预设</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
          <ElCollapseItem title="欢迎语" />
        </ElCollapse>
      </div>
    </div>

    <!-- 发布 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">对话设置</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="嵌入" />
          <ElCollapseItem title="API" />
        </ElCollapse>
      </div>
    </div>
  </div>
</template>
