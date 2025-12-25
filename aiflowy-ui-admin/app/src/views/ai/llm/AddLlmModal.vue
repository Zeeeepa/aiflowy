<script setup lang="ts">
import type { ModelAbilityItem } from './llmUtils/model-ability';

import { reactive, ref, watch } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElTag,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

import {
  getDefaultModelAbility,
  handleTagClick as handleTagClickUtil,
  syncTagSelectedStatus as syncTagSelectedStatusUtil,
} from './llmUtils/model-ability';
import {
  generateFeaturesFromModelAbility,
  resetModelAbility,
} from './llmUtils/model-ability-utils';

interface FormData {
  modelType: string;
  title: string;
  modelName: string;
  groupName: string;
  providerId: string;
  provider: string;
  apiKey: string;
  endpoint: string;
  requestPath: string;
  supportThinking: boolean;
  supportTool: boolean;
  supportImage: boolean;
  supportAudio: boolean;
  supportFree: boolean;
  supportVideo: boolean;
  supportImageB64Only: boolean;
  options: {
    chatPath: string;
    embedPath: string;
    llmEndpoint: string;
    rerankPath: string;
  };
}

const props = defineProps({
  providerId: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['reload']);
const selectedProviderId = ref<string>(props.providerId ?? '');

// 监听 providerId 的变化
watch(
  () => props.providerId,
  (newVal) => {
    if (newVal) {
      selectedProviderId.value = newVal;
    }
  },
  { immediate: true },
);

const formDataRef = ref();
const isAdd = ref(true);
const dialogVisible = ref(false);

// 表单数据
const formData = reactive<FormData>({
  modelType: '',
  title: '',
  modelName: '',
  groupName: '',
  providerId: '',
  provider: '',
  apiKey: '',
  endpoint: '',
  requestPath: '',
  supportThinking: false,
  supportTool: false,
  supportImage: false,
  supportAudio: false,
  supportFree: false,
  supportVideo: false,
  supportImageB64Only: false,
  options: {
    llmEndpoint: '',
    chatPath: '',
    embedPath: '',
    rerankPath: '',
  },
});

// 使用抽取的函数获取模型能力配置
const modelAbility = ref<ModelAbilityItem[]>(getDefaultModelAbility());

/**
 * 同步标签选中状态与formData中的布尔字段
 */
const syncTagSelectedStatus = () => {
  syncTagSelectedStatusUtil(modelAbility.value, formData);
};

/**
 * 处理标签点击事件
 */
const handleTagClick = (item: ModelAbilityItem) => {
  // handleTagClickUtil(modelAbility.value, item, formData);
  handleTagClickUtil(item, formData);
};

// 打开新增弹窗
defineExpose({
  openAddDialog(modelType: string) {
    isAdd.value = true;
    if (formDataRef.value) {
      formDataRef.value.resetFields();
    }

    // 重置表单数据
    Object.assign(formData, {
      id: '',
      modelType,
      title: '',
      modelName: '',
      groupName: '',
      provider: '',
      endPoint: '',
      providerId: '',
      supportThinking: false,
      supportTool: false,
      supportAudio: false,
      supportVideo: false,
      supportImage: false,
      supportImageB64Only: false,
      supportFree: false,
      options: {
        llmEndpoint: '',
        chatPath: '',
        embedPath: '',
        rerankPath: '',
      },
    });

    // 重置标签状态
    resetModelAbility(modelAbility.value);
    dialogVisible.value = true;
  },

  openEditDialog(item: any) {
    dialogVisible.value = true;
    isAdd.value = false;

    // 填充表单数据
    Object.assign(formData, {
      id: item.id,
      modelType: item.modelType || '',
      title: item.title || '',
      modelName: item.modelName || '',
      groupName: item.groupName || '',
      provider: item.provider || '',
      endpoint: item.endpoint || '',
      requestPath: item.requestPath || '',
      supportThinking: item.supportThinking || false,
      supportAudio: item.supportAudio || false,
      supportImage: item.supportImage || false,
      supportImageB64Only: item.supportImageB64Only || false,
      supportVideo: item.supportVideo || false,
      supportTool: item.supportTool || false,
      supportFree: item.supportFree || false,
      options: {
        llmEndpoint: item.options?.llmEndpoint || '',
        chatPath: item.options?.chatPath || '',
        embedPath: item.options?.embedPath || '',
        rerankPath: item.options?.rerankPath || '',
      },
    });

    // 同步标签状态
    syncTagSelectedStatus();
  },
});

const closeDialog = () => {
  dialogVisible.value = false;
};

const rules = {
  title: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  modelName: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  groupName: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  provider: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
};

const btnLoading = ref(false);

const save = async () => {
  btnLoading.value = true;

  // 使用工具函数从模型能力生成features
  const features = generateFeaturesFromModelAbility(modelAbility.value);

  try {
    await formDataRef.value.validate();
    const submitData = { ...formData, ...features };

    if (isAdd.value) {
      submitData.providerId = selectedProviderId.value;
      const res = await api.post('/api/v1/model/save', submitData);
      if (res.errorCode === 0) {
        ElMessage.success(res.message);
        emit('reload');
        closeDialog();
      } else {
        ElMessage.error(res.message || $t('message.operationFailed'));
      }
    } else {
      const res = await api.post('/api/v1/model/update', submitData);
      if (res.errorCode === 0) {
        ElMessage.success(res.message);
        emit('reload');
        closeDialog();
      } else {
        ElMessage.error(res.message || $t('message.operationFailed'));
      }
    }
  } catch (error) {
    console.error('Save model error:', error);
    ElMessage.error($t('message.operationFailed'));
  } finally {
    btnLoading.value = false;
  }
};
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    align-center
    width="482"
  >
    <ElForm
      label-width="100px"
      ref="formDataRef"
      :model="formData"
      status-icon
      :rules="rules"
    >
      <ElFormItem prop="title" :label="$t('llm.title')">
        <ElInput v-model.trim="formData.title" />
      </ElFormItem>
      <ElFormItem prop="modelName" :label="$t('llm.llmModel')">
        <ElInput v-model.trim="formData.modelName" />
      </ElFormItem>
      <ElFormItem prop="apiKey" :label="$t('llmProvider.apiKey')">
        <ElInput v-model.trim="formData.apiKey" />
      </ElFormItem>
      <ElFormItem prop="endpoint" :label="$t('llmProvider.endpoint')">
        <ElInput v-model.trim="formData.endpoint" />
      </ElFormItem>
      <ElFormItem prop="requestPath" :label="$t('llm.requestPath')">
        <ElInput v-model.trim="formData.requestPath" />
      </ElFormItem>
      <ElFormItem prop="groupName" :label="$t('llm.groupName')">
        <ElInput v-model.trim="formData.groupName" />
      </ElFormItem>
      <ElFormItem prop="ability" :label="$t('llm.ability')">
        <div class="model-ability">
          <ElTag
            class="model-ability-tag"
            v-for="item in modelAbility"
            :key="item.value"
            :type="item.selected ? item.activeType : item.defaultType"
            @click="handleTagClick(item)"
            :class="{ 'tag-selected': item.selected }"
          >
            {{ item.label }}
          </ElTag>
        </div>
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
.model-ability {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.model-ability-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.tag-selected {
  font-weight: bold;
  transform: scale(1.05);
}
</style>
