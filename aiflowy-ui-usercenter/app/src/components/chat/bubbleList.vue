<script setup lang="ts">
import type {
  BubbleListItemProps,
  BubbleListProps,
} from 'vue-element-plus-x/types/BubbleList';

import { ref } from 'vue';
import { BubbleList } from 'vue-element-plus-x';

import { DocumentCopy, Refresh, Search, Star } from '@element-plus/icons-vue';
import { ElButton } from 'element-plus';

type listType = BubbleListItemProps & {
  key: number;
  role: 'ai' | 'user';
};

// 示例调用
const bubbleItems = ref<BubbleListProps<listType>['list']>(
  generateFakeItems(10),
);
const avatar = ref('https://avatars.githubusercontent.com/u/76239030?v=4');
const avartAi = ref(
  'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
);
const switchValue = ref(false);
// const loading = ref(false);

function generateFakeItems(count: number): listType[] {
  const messages: listType[] = [];
  for (let i = 0; i < count; i++) {
    const role = i % 2 === 0 ? 'ai' : 'user';
    const placement = role === 'ai' ? 'start' : 'end';
    const key = i + 1;
    messages.push({
      key,
      role,
      placement,
      noStyle: true, // 如果你不想用默认的气泡样式
    });
  }
  return messages;
}

// 设置某个 item 的 loading
// function setLoading(loading: boolean) {
//   const list = bubbleItems.value;
//   if (!list || list.length === 0) return;

//   const last = list[list.length - 1];
//   if (last) {
//     last.loading = loading;
//   }

//   const prev = list[list.length - 2];
//   if (prev) {
//     prev.loading = loading;
//   }
// }
</script>

<template>
  <!-- <div class="flex gap-3">
      <span>动态设置内容 <ElSwitch v-model="switchValue" /></span>
      <span>
        自定义 loading
        <ElSwitch
          v-model="loading"
          @change="(value: any) => setLoading(value as boolean)"
      /></span>
    </div> -->
  <BubbleList :list="bubbleItems" max-height="calc(100vh - 345px)">
    <!-- 自定义头像 -->
    <template #avatar="{ item }">
      <div class="avatar-wrapper">
        <img :src="item.role === 'ai' ? avartAi : avatar" alt="avatar" />
      </div>
    </template>

    <!-- 自定义头部 -->
    <template #header="{ item }">
      <div class="header-wrapper">
        <div class="header-name">
          {{ item.role === 'ai' ? 'Element Plus X 🍧' : '🧁 用户' }}
        </div>
      </div>
    </template>

    <!-- 自定义气泡内容 -->
    <template #content="{ item }">
      <div class="content-wrapper">
        <div class="content-text">
          {{
            item.role === 'ai'
              ? `${switchValue ? `#ai-${item.key}：` : ''} 💖 感谢使用 Element Plus X ! 你的支持，是我们开源的最强动力 ~`
              : `${switchValue ? `#user-${item.key}：` : ''}哈哈哈，让我试试`
          }}
        </div>
      </div>
    </template>

    <!-- 自定义底部 -->
    <template #footer="{ item }">
      <div class="footer-wrapper">
        <div class="footer-container">
          <ElButton type="info" :icon="Refresh" size="small" circle />
          <ElButton type="success" :icon="Search" size="small" circle />
          <ElButton type="warning" :icon="Star" size="small" circle />
          <ElButton color="#626aef" :icon="DocumentCopy" size="small" circle />
        </div>
        <div class="footer-time">
          {{ item.role === 'ai' ? '下午 2:32' : '下午 2:33' }}
        </div>
      </div>
    </template>

    <!-- 自定义 loading -->
    <template #loading="{ item }">
      <div class="loading-container">
        <span>#{{ item.role }}-{{ item.key }}：</span>
        <span>我</span>
        <span>是</span>
        <span>自</span>
        <span>定</span>
        <span>义</span>
        <span>加</span>
        <span>载</span>
        <span>内</span>
        <span>容</span>
        <span>哦</span>
        <span>~</span>
      </div>
    </template>
  </BubbleList>
</template>

<style scoped lang="less">
.avatar-wrapper {
  width: 40px;
  height: 40px;
  img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
  }
}

.header-wrapper {
  .header-name {
    font-size: 14px;
    color: #979797;
  }
}

.content-wrapper {
  .content-text {
    font-size: 14px;
    color: #333;
    padding: 12px;
    background: linear-gradient(to right, #fdfcfb 0%, #ffd1ab 100%);
    border-radius: 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}

.footer-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  .footer-time {
    font-size: 12px;
    margin-top: 3px;
  }
}

.footer-container {
  :deep(.el-button + .el-button) {
    margin-left: 8px;
  }
}

.loading-container {
  font-size: 14px;
  color: #333;
  padding: 12px;
  background: linear-gradient(to right, #fdfcfb 0%, #ffd1ab 100%);
  border-radius: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.loading-container span {
  display: inline-block;
  margin-left: 8px;
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(5px);
  }
  50% {
    transform: translateY(-5px);
  }
}

.loading-container span:nth-child(4n) {
  animation: bounce 1.2s ease infinite;
}
.loading-container span:nth-child(4n + 1) {
  animation: bounce 1.2s ease infinite;
  animation-delay: 0.3s;
}
.loading-container span:nth-child(4n + 2) {
  animation: bounce 1.2s ease infinite;
  animation-delay: 0.6s;
}
.loading-container span:nth-child(4n + 3) {
  animation: bounce 1.2s ease infinite;
  animation-delay: 0.9s;
}
</style>
