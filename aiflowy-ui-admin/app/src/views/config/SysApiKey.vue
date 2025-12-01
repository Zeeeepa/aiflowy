<script setup lang="ts">
import { ref } from 'vue';

import { ElButton } from 'element-plus';

import DataSelectModal from '#/components/dataSelectModal/DataSelectModal.vue';

const dataSelectRef = ref();
const handleOpen = () => {
  dataSelectRef.value.openDialog(['273931608955039744', '280111871524397056']);
};
const handleGetData = (ids: string[]) => {
  console.log('dataSelectRef.value.getData()');
  console.log(ids);
};
</script>
<template>
  <div class="sys-apikey-container">
    <div>
      <ElButton type="primary" @click="handleOpen">
        {{ $t('button.add') }}
      </ElButton>
    </div>
    <div>
      <ElButton type="primary" @click="handleGetData"> 确认 </ElButton>
    </div>

    <DataSelectModal
      ref="dataSelectRef"
      page-url="/api/v1/aiPlugin/page"
      @get-data="handleGetData"
      width="60%"
    />
  </div>
</template>

<style scoped>
.sys-apikey-container {
  height: 100%;
  display: flex;
}
</style>
