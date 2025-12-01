<script setup lang="ts">
import { markRaw, ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { Plus } from '@element-plus/icons-vue';
import { ElDialog } from 'element-plus';

import SysApiKeyList from '#/views/config/apikey/SysApiKeyList.vue';
import SysApiKeyResourcePermissionList from '#/views/config/apikey/SysApiKeyResourcePermissionList.vue';

const dataSelectRef = ref();
const handleOpen = () => {
  dialogVisible.value = true;
};
const handleGetData = (ids: string[]) => {
  console.log('dataSelectRef.value.getData()');
  console.log(ids);
};
const headerButtons = ref([
  {
    key: 'add',
    text: $t('sysApiKeyResourcePermission.addPermission'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'addPermission' },
  },
]);
const dialogVisible = ref(false);
const handleButtonClick = (button) => {
  if (button.data.action === 'addPermission') {
    handleOpen();
  }
};
</script>
<template>
  <div class="sys-apikey-container">
    <SysApiKeyList />
    <ElDialog
      v-model="dialogVisible"
      draggable
      :close-on-click-modal="false"
      align-center
    >
      <SysApiKeyResourcePermissionList />
    </ElDialog>
  </div>
</template>

<style scoped>
.sys-apikey-container {
  height: 100%;
  padding: 20px;
  width: 100%;
}
</style>
