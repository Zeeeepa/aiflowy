<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { reactive, ref } from 'vue';
import { useRoute } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';

import { Upload } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElUpload,
} from 'element-plus';

const route = useRoute();
const showDialog = ref(false);
const formRef = ref<FormInstance>();
const formData = reactive({
  type: '',
  description: '',
  contact: '',
  file: [],
});

// TODO: 功能未实现
const handleSubmit = async () => {
  const isValid = await formRef.value?.validate();

  if (isValid) {
    console.log(formData);
  }
};
</script>

<template>
  <Teleport v-if="!route.path.includes('auth')" to="#app">
    <div
      class="fixed bottom-3 right-5 cursor-pointer text-6xl active:opacity-70"
      @click="showDialog = !showDialog"
    >
      <IconifyIcon icon="svg:issue" />
    </div>
  </Teleport>
  <ElDialog
    draggable
    v-model="showDialog"
    title="问题反馈"
    style="max-width: 560px"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      label-width="80px"
      label-position="left"
      require-asterisk-position="right"
    >
      <ElFormItem
        prop="type"
        label="问题类型"
        :rules="[{ required: true, message: '此为必填项' }]"
      >
        <ElSelect v-model="formData.type" placeholder="请选择问题类型" />
      </ElFormItem>
      <ElFormItem
        prop="description"
        label="问题描述"
        :rules="[{ required: true, message: '此为必填项' }]"
      >
        <ElInput
          type="textarea"
          :rows="5"
          v-model="formData.description"
          placeholder="请简要描述下您所遇到的问题"
        />
      </ElFormItem>
      <ElFormItem prop="contact" label="联系方式">
        <ElInput
          v-model="formData.contact"
          placeholder="请留下手机号/邮箱/微信号"
        />
      </ElFormItem>
      <ElFormItem prop="file" label="上传附件">
        <ElUpload
          v-model:file-list="formData.file"
          list-type="picture"
          :auto-upload="false"
          :limit="1"
        >
          <ElButton :icon="Upload">上传文件</ElButton>
        </ElUpload>
      </ElFormItem>
    </ElForm>

    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="showDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">立即反馈</ElButton>
      </div>
    </template>
  </ElDialog>
</template>
