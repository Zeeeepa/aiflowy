<script setup lang="ts">
import { ref } from 'vue';
import { Sender } from 'vue-element-plus-x';

import { ElementPlus, Paperclip, Promotion } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';

const senderValue = ref('');
const isSelect = ref(false);
</script>

<template>
  <Sender
    v-model="senderValue"
    variant="updown"
    :auto-size="{ minRows: 2, maxRows: 5 }"
    clearable
    allow-speech
    placeholder="💌 在这里你可以自定义变体后的 prefix 和 action-list"
  >
    <template #prefix>
      <div
        style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap"
      >
        <ElButton round plain color="#626aef">
          <ElIcon><Paperclip /></ElIcon>
        </ElButton>

        <div
          :class="{ isSelect }"
          style="
            display: flex;
            align-items: center;
            gap: 4px;
            padding: 2px 12px;
            border: 1px solid silver;
            border-radius: 15px;
            cursor: pointer;
            font-size: 12px;
          "
          @click="isSelect = !isSelect"
        >
          <ElIcon><ElementPlus /></ElIcon>
          <span>深度思考</span>
        </div>

        左边是自定义 prefix 前缀 右边是自定义 操作列表
      </div>
    </template>

    <template #action-list>
      <div style="display: flex; align-items: center; gap: 8px">
        <ElButton round color="#626aef">
          <ElIcon><Promotion /></ElIcon>
        </ElButton>
      </div>
    </template>
  </Sender>
</template>
