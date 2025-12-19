<script setup lang="ts" generic="T extends { icon?: any; [key: string]: any }">
import type { Component } from 'vue';

import { ref, watch } from 'vue';

import { preferences } from '@aiflowy/preferences';
import { cn } from '@aiflowy/utils';

import { MoreFilled } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElEmpty,
  ElIcon,
} from 'element-plus';

interface Props {
  title?: string;
  menus: T[];
  labelKey: string;
  valueKey: string;
  iconSize?: number;
  controlBtns?: {
    icon?: any;
    label: string;
    onClick: (_: T) => void;
    type?: any;
  }[];
  footerButton?: {
    icon?: any;
    label: string;
    onClick: () => void;
  };
  defaultSelected?: string;
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  iconSize: 16,
  controlBtns: () => [],
  footerButton: undefined,
  defaultSelected: '',
});
const emits = defineEmits<{
  (e: 'change', item: T): void;
}>();
const panelWidth = ref(225);
const selected = ref<string>(props.defaultSelected ?? '');
const hoverId = ref<string>();

const handleChange = (item: T) => {
  selected.value = item[props.valueKey];
  emits('change', item);
};
// 监听 defaultSelected 的变化
watch(
  () => props.defaultSelected,
  (newVal) => {
    if (newVal) {
      selected.value = newVal;
      const item = props.menus.find((menu) => menu[props.valueKey] === newVal);
      if (item) {
        emits('change', item);
      }
    }
  },
  { immediate: true },
);
const handleMouseEvent = (id?: string) => {
  if (id === undefined) {
    setTimeout(() => {
      hoverId.value = id;
    }, 200);
  } else {
    hoverId.value = id;
  }
};
const isComponent = (icon: any) => {
  return typeof icon !== 'string';
};
const isSvgString = (icon: any) => {
  if (typeof icon !== 'string') return false;
  // 简单判断：是否包含 SVG 根标签
  return icon.trim().startsWith('<svg') && icon.trim().endsWith('</svg>');
};
</script>

<template>
  <div
    class="flex h-full max-h-[calc(100vh-192px)] w-[225px] flex-col rounded-lg border border-[var(--el-border-color)] bg-[var(--el-bg-color)] p-2"
    :style="{ width: `${panelWidth}px` }"
  >
    <div class="flex flex-1 flex-col gap-5 overflow-hidden">
      <h3 v-if="title && title.length > 0" class="text-base font-medium">
        {{ title }}
      </h3>

      <div class="flex-1 overflow-auto">
        <div
          v-for="item in menus"
          :key="item[valueKey]"
          class="group list-item"
          :class="{
            selected: selected === item[valueKey],
          }"
          @click="handleChange(item)"
        >
          <div class="flex items-center gap-1">
            <div
              v-if="item.icon"
              class="ml-[-3px] flex items-center justify-center"
            >
              <div
                v-if="isSvgString(item.icon)"
                v-html="item.icon"
                :style="{
                  width: `${iconSize}px`,
                  height: `${iconSize}px`,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  overflow: 'hidden',
                }"
                class="svg-container"
              ></div>
              <img
                v-else-if="
                  typeof item.icon === 'string' && !isComponent(item.icon)
                "
                :src="item.icon"
                :style="{
                  width: `${iconSize}px`,
                  height: `${iconSize}px`,
                  objectFit: 'contain',
                }"
              />
              <ElIcon v-else>
                <component :is="item.icon as Component" v-bind="$attrs" />
              </ElIcon>
            </div>
            <div>
              {{ item[labelKey] }}
            </div>
          </div>
          <ElDropdown
            v-if="controlBtns.length > 0 && !['', '0'].includes(item[valueKey])"
            @click.stop
          >
            <div
              :class="
                cn(
                  'group-hover:!inline-flex',
                  (!hoverId || item.id !== hoverId) && '!hidden',
                )
              "
            >
              <ElIcon>
                <MoreFilled />
              </ElIcon>
            </div>
            <template #dropdown>
              <div
                @mouseenter="handleMouseEvent(item.id)"
                @mouseleave="handleMouseEvent()"
              >
                <ElDropdownMenu>
                  <ElDropdownItem
                    v-for="btn in controlBtns"
                    :key="btn.label"
                    @click="btn.onClick(item)"
                  >
                    <ElButton :type="btn.type" :icon="btn.icon" link>
                      {{ btn.label }}
                    </ElButton>
                  </ElDropdownItem>
                </ElDropdownMenu>
              </div>
            </template>
          </ElDropdown>
        </div>
        <ElEmpty
          v-if="menus.length <= 0"
          :image="`/empty${preferences.theme.mode === 'dark' ? '-dark' : ''}.png`"
        />
      </div>
    </div>

    <ElButton
      v-if="footerButton"
      @click="footerButton.onClick"
      :icon="footerButton.icon"
      plain
    >
      {{ footerButton.label }}
    </ElButton>
  </div>
</template>

<style scoped>
.list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: space-between;
  padding: 10px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.list-item:hover {
  background-color: hsl(var(--accent));
}

.list-item.selected {
  background-color: hsl(var(--primary) / 15%);
  color: hsl(var(--primary));
}

.list-item.selected:where(.dark, .dark *) {
  background-color: hsl(var(--accent));
  color: hsl(var(--accent-foreground));
}
.svg-container :deep(svg) {
  width: 100%;
  height: 100%;
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
</style>
