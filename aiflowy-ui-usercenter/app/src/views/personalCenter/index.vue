<script setup lang="ts">
import type { FormInstance, UploadFile, UploadFiles } from 'element-plus';

import { reactive, ref } from 'vue';

import { User } from '@element-plus/icons-vue';
import {
  ElAvatar,
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElSpace,
  ElUpload,
} from 'element-plus';
import { tryit } from 'radash';

const formRef = ref<FormInstance>();
const formData = reactive({
  nickname: '',
  phone: '',
});
const editing = reactive({
  nickname: false,
  phone: false,
});
const rules = {
  nickname: [{ required: true, message: '请输入用户名' }],
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
  ],
};

const handleFieldChange = async (field: keyof typeof formData) => {
  if (!formRef.value) return;
  const [err] = await tryit(formRef.value.validateField)([field]);

  if (!err) {
    console.warn('success');
  }
};
const handleCancelEdit = async (field: keyof typeof formData) => {
  editing[field] = false;
  formRef.value?.resetFields([field]);
};

const handleUploadChange = (
  uploadFile: UploadFile,
  uploadFiles: UploadFiles,
) => {
  console.warn(uploadFile, uploadFiles);
};
</script>

<template>
  <div
    class="h-full w-full bg-[linear-gradient(153deg,#FFFFFF,#EFF8FF)] pt-[156px]"
  >
    <ElForm
      ref="formRef"
      class="mx-auto"
      style="max-width: 619px; width: 100%"
      label-position="top"
      :model="formData"
      :rules="rules"
      hide-required-asterisk
    >
      <ElFormItem label="用户名：" prop="nickname">
        <div class="flex w-full justify-between gap-5">
          <template v-if="editing.nickname">
            <ElInput v-model="formData.nickname" />
            <ElSpace>
              <ElButton type="primary" @click="handleFieldChange('nickname')">
                确定
              </ElButton>
              <ElButton @click="handleCancelEdit('nickname')"> 取消 </ElButton>
            </ElSpace>
          </template>
          <template v-else>
            <span>hgxT11WPbZ</span>
            <ElButton @click="editing.nickname = true">编辑用户名</ElButton>
          </template>
        </div>
      </ElFormItem>
      <ElFormItem label="头像：">
        <ElSpace>
          <ElUpload
            :show-file-list="false"
            :auto-upload="false"
            @change="handleUploadChange"
          >
            <ElAvatar :icon="User" :size="46" />
          </ElUpload>
          <span>支持 2M 以内的 JPG 或 PNG 图片</span>
        </ElSpace>
      </ElFormItem>
      <ElFormItem label="手机号：" prop="phone">
        <div class="flex w-full justify-between gap-5">
          <template v-if="editing.phone">
            <ElInput v-model="formData.phone" />
            <ElSpace>
              <ElButton type="primary" @click="handleFieldChange('phone')">
                确定
              </ElButton>
              <ElButton @click="handleCancelEdit('phone')">取消</ElButton>
            </ElSpace>
          </template>
          <template v-else>
            <span>XXXXXXXXX84</span>
            <ElButton @click="editing.phone = true">修改手机号</ElButton>
          </template>
        </div>
      </ElFormItem>
      <ElFormItem>
        <div class="mt-20 flex w-full justify-center">
          <ElButton type="primary" class="!h-11 w-[333px]">退出登录</ElButton>
        </div>
      </ElFormItem>
    </ElForm>
  </div>
</template>
