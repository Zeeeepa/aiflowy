<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { Plus, Remove } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElOption,
  ElRadio,
  ElRadioGroup,
  ElSelect,
} from 'element-plus';

import { api } from '#/api/request';
import UploadAvatar from '#/components/upload/UploadAvatar.vue';
import { $t } from '#/locales';

const emit = defineEmits(['reload']);
const embeddingLlmList = ref<any>([]);
const rerankerLlmList = ref<any>([]);
interface headersType {
  label: string;
  value: string;
}
const authTypeList = ref<headersType[]>([
  {
    label: 'None',
    value: 'none',
  },
  {
    label: 'Service token / ApiKey',
    value: 'apiKey',
  },
]);
onMounted(() => {
  api.get('/api/v1/aiLlm/list?supportEmbed=true').then((res) => {
    embeddingLlmList.value = res.data;
  });
  api.get('/api/v1/aiLlm/list?supportRerankerLlmList=true').then((res) => {
    rerankerLlmList.value = res.data;
  });
});
defineExpose({
  openDialog,
});
const saveForm = ref<FormInstance>();
// variables
const dialogVisible = ref(false);
const isAdd = ref(true);
const tempAddHeaders = ref<headersType[]>([]);
const entity = ref<any>({
  alias: '',
  deptId: '',
  icon: '',
  title: '',
  authType: 'none',
  description: '',
  englishName: '',
  headers: '',
  position: '',
});
const btnLoading = ref(false);
const rules = ref({
  name: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
  description: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  baseUrl: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  authType: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  tokenKey: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  tokenValue: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  position: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
});

// functions
function openDialog(row: any) {
  if (row.id) {
    isAdd.value = false;
    if (row.headers) {
      tempAddHeaders.value = JSON.parse(row.headers);
    }
  }
  entity.value = {
    ...row,
    authType: row.authType || 'none',
  };
  dialogVisible.value = true;
}
function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      const plainEntity = { ...entity.value };
      const plainHeaders = [...tempAddHeaders.value];
      if (isAdd.value) {
        api
          .post('/api/v1/aiPlugin/plugin/save', {
            ...plainEntity,
            headers: plainHeaders,
          })
          .then((res) => {
            if (res.errorCode === 0) {
              dialogVisible.value = false;
              ElMessage.success($t('message.saveOkMessage'));
              emit('reload');
            }
          });
      } else {
        api
          .post('/api/v1/aiPlugin/plugin/update', {
            ...plainEntity,
            headers: plainHeaders,
          })
          .then((res) => {
            if (res.errorCode === 0) {
              dialogVisible.value = false;
              ElMessage.success($t('message.updateOkMessage'));
              emit('reload');
            }
          });
      }
    }
  });
}
function closeDialog() {
  saveForm.value?.resetFields();
  isAdd.value = true;
  entity.value = {};
  dialogVisible.value = false;
}
function addHeader() {
  tempAddHeaders.value.push({
    label: '',
    value: '',
  });
}
function removeHeader(index: number) {
  tempAddHeaders.value.splice(index, 1);
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    align-center
  >
    <ElForm
      label-width="150px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem
        prop="icon"
        :label="$t('plugin.icon')"
        style="display: flex; align-items: center"
      >
        <UploadAvatar v-model="entity.icon" />
      </ElFormItem>
      <ElFormItem prop="name" :label="$t('plugin.name')">
        <ElInput
          v-model.trim="entity.name"
          :placeholder="$t('plugin.placeholder.name')"
        />
      </ElFormItem>
      <ElFormItem prop="baseUrl" :label="$t('plugin.baseUrl')">
        <ElInput v-model.trim="entity.baseUrl" />
      </ElFormItem>
      <ElFormItem prop="description" :label="$t('plugin.description')">
        <ElInput
          v-model.trim="entity.description"
          :rows="4"
          type="textarea"
          :placeholder="$t('plugin.placeholder.description')"
        />
      </ElFormItem>
      <ElFormItem prop="Headers" label="Headers">
        <div
          class="headers-container-reduce flex flex-row gap-4"
          v-for="(item, index) in tempAddHeaders"
          :key="index"
        >
          <div class="head-con-content flex flex-row gap-4">
            <ElInput v-model.trim="item.label" placeholder="header name" />
            <ElInput v-model.trim="item.value" placeholder="header value" />
            <ElIcon size="20" @click="removeHeader" style="cursor: pointer">
              <Remove />
            </ElIcon>
          </div>
        </div>
        <ElButton @click="addHeader" class="addHeadersBtn">
          <ElIcon size="18" style="margin-right: 4px">
            <Plus />
          </ElIcon>
          {{ $t('button.add') }}headers
        </ElButton>
      </ElFormItem>
      <ElFormItem prop="authType" :label="$t('plugin.authType')">
        <ElSelect v-model="entity.authType">
          <ElOption
            v-for="item in authTypeList"
            :key="item.value"
            :label="item.label"
            :value="item.value || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        prop="position"
        :label="$t('plugin.position')"
        v-if="entity.authType === 'apiKey'"
      >
        <ElRadioGroup v-model="entity.position">
          <ElRadio value="headers">headers</ElRadio>
          <ElRadio value="query">query</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem
        prop="tokenKey"
        :label="$t('plugin.tokenKey')"
        v-if="entity.authType === 'apiKey'"
      >
        <ElInput v-model.trim="entity.tokenKey" />
      </ElFormItem>
      <ElFormItem
        prop="tokenValue"
        :label="$t('plugin.tokenValue')"
        v-if="entity.authType === 'apiKey'"
      >
        <ElInput v-model.trim="entity.tokenValue" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="closeDialog">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        @click="save"
        :loading="btnLoading"
        :disabled="btnLoading"
      >
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.headers-container-reduce {
  align-items: center;
}
.addHeadersBtn {
  width: 100%;
  border-style: dashed;
  border-color: var(--el-color-primary);
  border-radius: 8px;
  margin-top: 8px;
}
.head-con-content {
  margin-bottom: 8px;
  align-items: center;
}
</style>
