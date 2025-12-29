<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { ElButton, ElDialog, ElForm, ElFormItem, ElInput } from 'element-plus';

import { sseClient } from '#/api/request';

const emit = defineEmits(['success']);

const dialogVisible = ref(false);
const formRef = ref<InstanceType<typeof ElForm>>();
const formData = ref();
const rules = {
  title: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
};
const loading = ref(false);

const handleSubmit = async () => {
  loading.value = true;
  const data = {
    botId: formData.value.botId,
    prompt: formData.value.prompt,
  };
  formData.value.prompt = '';
  sseClient.post('/api/v1/bot/prompt/chore/chat', data, {
    onMessage(message) {
      const event = message.event;
      //  done
      if (event === 'done') {
        loading.value = false;
        return;
      }
      if (!message.data) {
        return;
      }
      const sseData = JSON.parse(message.data);
      const delta = sseData.payload?.delta;
      formData.value.prompt += delta;
    },
  });
};
const handleReplace = () => {
  emit('success', formData.value.prompt);
  dialogVisible.value = false;
};
defineExpose({
  open(botId: string, systemPrompt: string) {
    formData.value = {
      botId,
      prompt: systemPrompt,
    };
    dialogVisible.value = true;
    handleSubmit();
  },
});
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    title="AI优化提示词"
    draggable
    align-center
    width="550px"
  >
    <ElForm ref="formRef" :model="formData" :rules="rules">
      <ElFormItem prop="prompt">
        <ElInput type="textarea" :rows="20" v-model="formData.prompt" />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="dialogVisible = false">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton type="primary" @click="handleReplace" v-if="!loading">
        {{ $t('button.replace') }}
      </ElButton>
      <ElButton
        type="primary"
        :loading="loading"
        :disabled="loading"
        @click="handleSubmit"
      >
        {{ loading ? $t('button.optimizing') : $t('button.regenerate') }}
      </ElButton>
    </template>
  </ElDialog>
</template>
