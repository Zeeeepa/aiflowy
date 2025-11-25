<script setup lang="ts">
import { h, ref } from 'vue'
import { Menu, type ItemType } from 'ant-design-vue'

import HomeIcon from '@/components/icons/IconHome.vue'
import AssistantIcon from '@/components/icons/IconAssistant.vue'
import AgentPlazaIcon from '@/components/icons/IconAgentPlaza.vue'
import CollectIcon from '@/components/icons/IconCollect.vue'
import HistoryIcon from '@/components/icons/IconHistory.vue'

const items: ItemType[] = [
  {
    key: 'home',
    icon: () => h(HomeIcon),
    label: '首页',
  },
  {
    key: 'assistant',
    icon: () => h(AssistantIcon),
    label: '通用助手',
  },
  {
    key: 'agentPlaza',
    icon: () => h(AgentPlazaIcon),
    label: '智能体广场',
  },
  {
    key: 'myCollect',
    icon: () => h(CollectIcon),
    label: '我的收藏',
  },
  {
    key: 'history',
    icon: () => h(HistoryIcon),
    label: '历史记录',
  },
]
const selectedKeys = ref(['home'])
</script>

<template>
  <Menu
    mode="vertical"
    :items="items"
    v-model:selected-keys="selectedKeys"
    :style="{
      background: 'transparent',
      paddingInline: '12px',
    }"
  />
</template>

<style lang="css" scoped>
.ant-menu :deep(.ant-menu-item) {
  height: 48px;
  line-height: 48px;
  padding-inline: 12px;
  display: flex;
  align-items: center;
  color: #333333;
}

.ant-menu :deep(.ant-menu-item-icon) {
  font-size: 20px;
}

.ant-menu :deep(.ant-menu-item-selected) {
  color: white;
  background-color: #0066ff;
}
</style>
