<script setup lang="ts">
import { computed } from 'vue';

import {
  ElInputNumber,
  ElOption,
  ElRadio,
  ElRadioGroup,
  ElSelect,
} from 'element-plus';
// 定义接口，方便父组件引用（如果使用 TS）
export interface CronItemState {
  type: 'every' | 'loop' | 'none' | 'range' | 'specific';
  rangeStart: number;
  rangeEnd: number;
  loopStart: number;
  loopStep: number;
  specificList: number[];
}

const props = defineProps({
  modelValue: {
    type: Object as () => CronItemState,
    required: true,
  },
  label: { type: String, default: '' },
  min: { type: Number, default: 0 },
  max: { type: Number, default: 59 },
  aliasMap: {
    type: Object as () => Record<number, string>,
    default: () => ({}),
  }, // 用于周的转换
  weekModeCheck: { type: Boolean, default: false }, // 是否是日Tab
  dayModeCheck: { type: Boolean, default: false }, // 是否是周Tab
});

const emit = defineEmits(['update:modelValue', 'change']);

// 计算属性处理单选框的 v-model
const radioType = computed({
  get: () => props.modelValue.type,
  set: (val) => {
    // 更新 type 时，触发 update
    emit('update:modelValue', { ...props.modelValue, type: val });
    emit('change', val);
  },
});

// 通用更新函数
const updateVal = (key: keyof CronItemState, val: any) => {
  emit('update:modelValue', { ...props.modelValue, [key]: val });
  // 这里是否触发 change 取决于你的需求，通常数值改变不需要触发 tab 互斥检查，所以这里不一定非要 emit('change')
  // 但为了保险起见，可以保留
};

// 生成下拉选项
const specificOptions = computed(() => {
  const options = [];
  for (let i = props.min; i <= props.max; i++) {
    const label = props.aliasMap[i] ? `${props.aliasMap[i]} (${i})` : `${i}`;
    options.push({ value: i, label });
  }
  return options;
});
</script>

<template>
  <div class="crontab-pane">
    <ElRadioGroup class="cron-radio-group" v-model="radioType">
      <!-- 1. 每 xxx -->
      <div class="radio-line">
        <ElRadio value="every">每{{ label }} (*)</ElRadio>
      </div>

      <!-- 2. 不指定 (?) - 仅用于日和周 -->
      <div class="radio-line" v-if="weekModeCheck || dayModeCheck">
        <ElRadio value="none">不指定 (?)</ElRadio>
      </div>

      <!-- 3. 周期 (Loop) -->
      <div class="radio-line">
        <ElRadio value="loop">周期</ElRadio>
        <span class="text">从</span>
        <ElInputNumber
          :model-value="modelValue.loopStart"
          @update:model-value="(v) => updateVal('loopStart', v)"
          :min="min"
          :max="max"
          size="small"
          controls-position="right"
        />
        <span class="text">{{ label }}开始，每</span>
        <ElInputNumber
          :model-value="modelValue.loopStep"
          @update:model-value="(v) => updateVal('loopStep', v)"
          :min="1"
          :max="max"
          size="small"
          controls-position="right"
        />
        <span class="text">{{ label }}执行一次</span>
      </div>

      <!-- 4. 区间 (Range) -->
      <div class="radio-line">
        <ElRadio value="range">区间</ElRadio>
        <span class="text">从</span>
        <ElInputNumber
          :model-value="modelValue.rangeStart"
          @update:model-value="(v) => updateVal('rangeStart', v)"
          :min="min"
          :max="max"
          size="small"
          controls-position="right"
        />
        <span class="text">至</span>
        <ElInputNumber
          :model-value="modelValue.rangeEnd"
          @update:model-value="(v) => updateVal('rangeEnd', v)"
          :min="min"
          :max="max"
          size="small"
          controls-position="right"
        />
        <span class="text">{{ label }}</span>
      </div>

      <!-- 5. 指定 (Specific) -->
      <div class="radio-line">
        <ElRadio value="specific">指定</ElRadio>
        <ElSelect
          :model-value="modelValue.specificList"
          @update:model-value="(v) => updateVal('specificList', v)"
          multiple
          placeholder="请选择"
          style="width: 100%; min-width: 200px; margin-left: 10px"
          size="small"
        >
          <ElOption
            v-for="item in specificOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </ElSelect>
      </div>
    </ElRadioGroup>
  </div>
</template>

<style scoped>
.cron-radio-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}
.radio-line {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
.text {
  margin: 0 5px;
  font-size: 14px;
}
:deep(.ElInputNumber) {
  width: 100px;
}
</style>
