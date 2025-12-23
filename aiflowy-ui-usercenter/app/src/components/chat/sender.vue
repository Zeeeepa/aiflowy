<script setup lang="ts">
import { inject, ref } from 'vue';

import { Promotion } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';

import { sseClient } from '#/api/request';
import SendingIcon from '#/components/icons/SendingIcon.vue';
// import PaperclipIcon from '#/components/icons/PaperclipIcon.vue';

interface Props {
  sessionId: string | undefined;
  bot: any;
  addMessage?: (message: any) => void;
}
const props = defineProps<Props>();
const senderValue = ref('');
const btnLoading = ref(false);
const getSessionList = inject<any>('getSessionList');
function sendMessage() {
  if (getDisabled()) {
    return;
  }
  const data = {
    sessionId: props.sessionId,
    prompt: senderValue.value,
    botId: props.bot.id,
  };
  btnLoading.value = true;
  props.addMessage?.({
    key: crypto.randomUUID(),
    role: 'user',
    placement: 'end',
    content: senderValue.value,
    typing: true,
  });
  senderValue.value = '';
  const msgKey = crypto.randomUUID();
  let str = '';
  sseClient.post('/userCenter/aiBot/chat', data, {
    onMessage(res) {
      const msg = {
        key: msgKey,
        role: 'assistant',
        placement: 'start',
        content: (str += res.data),
        typing: true,
      };
      if (res.event === 'finish') {
        btnLoading.value = false;
        getSessionList();
      }
      if (str !== res.data && res.event !== 'finish') {
        props.addMessage?.(msg);
      }
    },
    onError(err) {
      console.error(err);
      btnLoading.value = false;
    },
    onFinished() {
      senderValue.value = '';
      btnLoading.value = false;
    },
  });
}
function getDisabled() {
  return !senderValue.value || !props.sessionId;
}
const stopSse = () => {
  sseClient.abort();
  btnLoading.value = false;
};
</script>

<template>
  <el-sender
    v-model="senderValue"
    variant="updown"
    :auto-size="{ minRows: 2, maxRows: 5 }"
    clearable
    allow-speech
    placeholder="发送消息"
    @keyup.enter="sendMessage"
  >
    <!-- 自定义 prefix 前缀 -->
    <!-- <template #prefix>
    </template> -->

    <template #action-list>
      <div class="flex items-center gap-2">
        <!-- <ElButton :icon="PaperclipIcon" link /> -->
        <ElButton v-if="btnLoading" circle @click="stopSse">
          <ElIcon size="30" color="#409eff"><SendingIcon /></ElIcon>
        </ElButton>
        <ElButton
          v-else
          type="primary"
          :icon="Promotion"
          :disabled="getDisabled()"
          @click="sendMessage"
          round
        />
      </div>
    </template>
  </el-sender>
</template>
