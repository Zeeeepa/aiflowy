<template>
  <el-upload
    v-model:file-list="fileList"
    class="upload-demo"
    :headers="headers"
    :action="`${apiURL}${props.action}`"
    :multiple="props.multiple"
    :on-preview="handlePreview"
    :on-remove="handleRemove"
    :before-remove="beforeRemove"
    :limit="props.limit"
    :on-exceed="handleExceed"
    :on-success="handleSuccess"
  >
    <el-button type="primary">Click to upload</el-button>
    <template #tip>
      <div class="el-upload__tip">
        {{tips}}
      </div>
    </template>
  </el-upload>
</template>

<script lang="ts" setup>
import {defineEmits, ref} from 'vue'
import {ElMessage, ElMessageBox, ElButton, ElUpload} from 'element-plus'

const {apiURL} = useAppConfig(import.meta.env, import.meta.env.PROD);

import type {UploadProps, UploadUserFile} from 'element-plus'
import {useAccessStore} from "@aiflowy/stores";
import {useAppConfig} from "@aiflowy/hooks";

const accessStore = useAccessStore();
const props = defineProps({
  action: {
    type: String,
    default: '/api/v1/commons/upload'
  },
  tips: {
    type: String,
    default: ''
  },
  limit: {
    type: Number,
    default: 3
  },
  multiple: {
    type: Boolean,
    default: true
  }
})
const headers = ref({
  'aiflowy-token': accessStore.accessToken
})
const fileList = ref<UploadUserFile[]>([])

const emit = defineEmits([
  'success',       // 文件上传成功
])

const handleRemove: UploadProps['onRemove'] = (file, uploadFiles) => {
  console.log(file, uploadFiles)
}

const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
  console.log(uploadFile)
}

const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
  ElMessage.warning(
    `The limit is 3, you selected ${files.length} files this time, add up to ${
      files.length + uploadFiles.length
    } totally`
  )
}

const handleSuccess: UploadProps['onSuccess'] = (response) => {
  emit('success', response)
}

const beforeRemove: UploadProps['beforeRemove'] = (uploadFile) => {
  return ElMessageBox.confirm(
    `确定删除 ${uploadFile.name} ?`
  ).then(
    () => true,
    () => false
  )
}
</script>
