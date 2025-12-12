<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { tryit } from '@aiflowy/utils';

import { getBotDetails } from '#/api';
import { hasPermission } from '#/api/common/hasPermission';

import Config from './config.vue';
import Preview from './preview.vue';
import Prompt from './prompt.vue';

const route = useRoute();
const hasSavePermission = computed(() =>
  hasPermission(['/api/v1/aiBot/save', '/api/v1/aiBot/updateLlmId']),
);
const bot = ref<BotInfo>();

onMounted(() => {
  if (route.params.id) {
    fetchBotDetail(route.params.id as string);
  }
});

const fetchBotDetail = async (id: string) => {
  const [, res] = await tryit(getBotDetails(id));

  if (res?.errorCode === 0) {
    bot.value = res.data;
  }
};
</script>

<template>
  <div class="settings-container">
    <div class="row-container">
      <div class="row-item">
        <Prompt :bot="bot" :has-save-permission="hasSavePermission" />
      </div>
      <div class="row-item">
        <Config :bot="bot" :has-save-permission="hasSavePermission" />
      </div>
      <div class="row-item">
        <Preview :bot="bot" />
      </div>
    </div>
  </div>
</template>
<style scoped>
.settings-container {
  height: calc(100vh - 90px);
  padding: 20px;
}
.row-container {
  height: 100%;
  display: flex;
  gap: 20px;
}
.row-item {
  height: 100%;
  flex: 1;
}
</style>
