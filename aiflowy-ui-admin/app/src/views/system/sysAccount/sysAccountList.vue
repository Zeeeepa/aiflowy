<script setup lang="ts">
import { ref } from 'vue';

import { ElButton, ElTable, ElTableColumn } from 'element-plus';

import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

const pageDataRef = ref();
const search = () => {
  pageDataRef.value.setQuery({ loginName: 'tes2t' });
};
const reset = () => {
  pageDataRef.value.setQuery({});
};
</script>

<template>
  <div class="page-container">
    <ElButton @click="search" type="primary">
      {{ $t('button.search.submit') }}
    </ElButton>
    <ElButton @click="reset">{{ $t('button.search.reset') }}</ElButton>
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/sysAccount/page"
      :page-size="10"
      :init-query-params="{ status: 1 }"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn prop="loginName" label="账号" width="180" />
          <ElTableColumn prop="nickname" label="昵称" width="180" />
          <ElTableColumn prop="avatar" label="头像" />
        </ElTable>
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
