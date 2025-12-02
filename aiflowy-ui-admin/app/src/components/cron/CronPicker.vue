<script setup lang="ts">
import { ref } from 'vue';

import { ElButton, ElDialog } from 'element-plus';

import CronGenerator from './CronGenerator.vue';

defineProps({
  modelValue: {
    type: String,
    default: '',
  },
});
// 定义 emit 事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

// 使用本地变量来管理对话框显示
const showCron = ref(false);
function useCron(value: string) {
  emit('update:modelValue', value);
  showCron.value = false;
}
</script>

<template>
  <div>
    <ElDialog draggable title="Cron" v-model="showCron" width="60%">
      <CronGenerator @use-cron="useCron" />
    </ElDialog>
    <ElButton class="mt-2" @click="showCron = true">点击生成</ElButton>
  </div>
</template>

<style scoped></style>
