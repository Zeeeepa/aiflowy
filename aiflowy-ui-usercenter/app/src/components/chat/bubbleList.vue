<script setup lang="ts">
import { useUserStore } from '@aiflowy/stores';

import { ElAvatar } from 'element-plus';

import defaultUserAvatar from '#/assets/defaultUserAvatar.png';
import AssistantAvatar from '#/components/avatar/Assistant.vue';

// type listType = BubbleListItemProps & {
//   key: number;
//   role: 'assistant' | 'user';
// };
// const messageList: BubbleListProps<listType>['list'] = [
//   {
//     key: 0,
//     role: 'user',
//     placement: 'end',
//     content: '哈哈哈，让我试试',
//     typing: true,
//   },
//   {
//     key: 1,
//     role: 'assistant',
//     placement: 'start',
//     content: '💖 感谢使用 Element Plus X ! 你的支持，是我们开源的最强动力 ~',
//     typing: true,
//   },
//   {
//     key: 2,
//     role: 'user',
//     placement: 'end',
//     content: '哈哈哈，让我试试',
//     typing: true,
//   },
//   {
//     key: 3,
//     role: 'assistant',
//     placement: 'start',
//     content: '💖 感谢使用 Element Plus X ! 你的支持，是我们开源的最强动力 ~',
//     loading: true,
//   },
// ];
interface Props {
  bot: any;
  messages: any[];
}
const props = defineProps<Props>();
const store = useUserStore();

function getUserAvatar() {
  return store.userInfo?.avatar || defaultUserAvatar;
}
</script>

<template>
  <el-bubble-list :list="messages" max-height="calc(100vh - 345px)">
    <!-- 自定义头像 -->
    <template #avatar="{ item }">
      <AssistantAvatar
        v-if="item.role === 'assistant'"
        :size="props.bot.icon"
      />
      <ElAvatar v-else :src="getUserAvatar()" :size="40" />
    </template>

    <!-- 自定义头部 -->
    <template #header="{ item }">
      <span class="text-foreground/50 text-xs">
        {{ item.created }}
      </span>
    </template>

    <!-- 自定义气泡内容 -->
    <!-- <template #content="{ item }">
    </template> -->

    <!-- 自定义底部 -->
    <!--<template #footer="{ item }">
      <div class="flex items-center">
        <template v-if="item.role === 'assistant'">
          <ElButton :icon="RefreshRight" link />
          <ElButton :icon="CopyDocument" link />
        </template>
        <template v-else>
          <ElButton :icon="CopyDocument" link />
          <ElButton :icon="EditPen" link />
        </template>
      </div>
    </template>-->
  </el-bubble-list>
</template>
