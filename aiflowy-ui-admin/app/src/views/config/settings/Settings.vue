<script setup>
import { onMounted, ref } from 'vue';

import { $t } from '@aiflowy/locales';

import {
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
} from 'element-plus';

import { api } from '#/api/request.js';

const brands = ref([]);
const llmOptions = ref([]);

// 获取品牌接口数据
function getBrands() {
  api.get('/api/v1/aiLlmBrand/list').then((res) => {
    if (res.errorCode === 0) {
      brands.value = res.data;
      llmOptions.value = formatLlmList(res.data);
    }
  });
}
function getOptions() {
  api
    .get(
      '/api/v1/sysOption/list?keys=model_of_chat&keys=chatgpt_endpoint&keys=chatgpt_chatPath&keys=chatgpt_api_key&keys=chatgpt_model_name',
    )
    .then((res) => {
      if (res.errorCode === 0) {
        entity.value = res.data;
      }
    });
}
onMounted(() => {
  getOptions();
  getBrands();
});

const entity = ref({
  model_of_chat: '',
  chatgpt_api_key: '',
  chatgpt_chatPath: '',
  chatgpt_endpoint: '',
  chatgpt_model_name: '',
});

function formatLlmList(data) {
  return data.map((item) => {
    const extra = new Map([
      ['chatPath', item.options.chatPath],
      ['llmEndpoint', item.options.llmEndpoint],
    ]);
    return {
      label: item.title,
      value: item.key,
      extra,
    };
  });
}
function handleChangeModel(value) {
  const extra = llmOptions.value.find((item) => item.value === value)?.extra;
  entity.value.chatgpt_chatPath = extra.get('chatPath');
  entity.value.chatgpt_endpoint = extra.get('llmEndpoint');
}
function handleSave() {
  api.post('/api/v1/sysOption/save', entity.value).then((res) => {
    if (res.errorCode === 0) {
      ElMessage.success($t('message.saveOkMessage'));
    }
  });
}
</script>

<template>
  <div class="settings-container">
    <div class="settings-config-container">
      <div class="settings-notice">
        注意：有很多大模型（比如：暗月之面等）都是兼容 ChatGPT
        的，因此若需要配置系统未内置的平台的大模型，供应商可以选择OpenAi。
      </div>
      <ElForm :model="entity" class="demo-form-inline" label-width="150px">
        <ElFormItem :label="$t('settingsConfig.modelOfChat')">
          <ElSelect
            v-model="entity.model_of_chat"
            clearable
            @change="handleChangeModel"
          >
            <ElOption
              v-for="item in llmOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem :label="$t('settingsConfig.modelName')">
          <ElInput v-model="entity.chatgpt_model_name" clearable />
        </ElFormItem>
        <ElFormItem label="Endpoint">
          <ElInput v-model="entity.chatgpt_endpoint" clearable />
        </ElFormItem>
        <ElFormItem label="ChatPath">
          <ElInput v-model="entity.chatgpt_chatPath" clearable />
        </ElFormItem>
        <ElFormItem label="ApiKey">
          <ElInput v-model="entity.chatgpt_api_key" clearable />
        </ElFormItem>
      </ElForm>
      <div class="settings-button-container">
        <ElButton type="primary" @click="handleSave">
          {{ $t('button.save') }}
        </ElButton>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-container {
  display: flex;
  flex-direction: column;
  padding: 30px 143px;
  height: 100%;
}

.settings-config-container {
  background-color: var(--el-bg-color);
  width: 100%;
  padding: 20px;
  border-radius: 10px;
}
:deep(.el-form-item) {
  margin-bottom: 25px;
}
.settings-notice {
  margin-bottom: 20px;
  color: var(--el-color-danger);
}
.settings-button-container {
  display: flex;
  justify-content: flex-end;
}
</style>
