<script setup lang="ts">
import { Search } from '@element-plus/icons-vue';
import {
  ElButton,
  ElContainer,
  ElHeader,
  ElInput,
  ElMain,
  ElSpace,
} from 'element-plus';

import CardPage from '#/components/cardPage/CardPage.vue';
import PageData from '#/components/page/PageData.vue';

const tags = ['推荐', '最新', '营销场景', '办公提效', '行业专属', '安全合规'];
</script>

<template>
  <ElContainer class="h-full bg-[linear-gradient(153deg,white,#EFF8FF)]">
    <ElHeader class="!h-auto !p-8 !pb-5">
      <ElSpace direction="vertical" :size="24" alignment="flex-start">
        <h1 class="text-2xl font-medium text-[#333333]">智能体</h1>
        <ElSpace :size="20">
          <ElInput
            class="search-input"
            placeholder="搜索"
            :prefix-icon="Search"
          />
          <ElSpace :size="12">
            <ElButton v-for="item in tags" :key="item" plain round>
              {{ item }}
            </ElButton>
          </ElSpace>
        </ElSpace>
      </ElSpace>
    </ElHeader>
    <ElMain>
      <PageData>
        <template #default="{ pageList }">
          <CardPage
            title-key="id"
            description-key="loginName"
            :data="pageList"
          />
        </template>
      </PageData>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.search-input :deep(.el-input__wrapper) {
  --el-input-border-radius: 18px;
  --el-input-border-color: #e6e9ee;
}
</style>
