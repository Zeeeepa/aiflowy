<script setup lang="ts">
import type { CronItemState } from './CronTabPane.vue';

import { computed, reactive, ref } from 'vue';

import {
  ElButton,
  ElCard,
  ElDivider,
  ElInput,
  ElTable,
  ElTableColumn,
  ElTabPane,
  ElTabs,
} from 'element-plus';

import { api } from '#/api/request';

import CrontabPane from './CronTabPane.vue';

const emit = defineEmits(['useCron']);
const activeTab = ref('second');

// 默认状态工厂函数
const defaultState = (min: number, _: number): CronItemState => ({
  type: 'every',
  rangeStart: min,
  rangeEnd: min + 1,
  loopStart: min,
  loopStep: 1,
  specificList: [],
});

const state = reactive({
  second: defaultState(0, 59),
  minute: defaultState(0, 59),
  hour: defaultState(0, 23),
  day: { ...defaultState(1, 31), type: 'every' } as CronItemState,
  month: defaultState(1, 12),
  week: { ...defaultState(1, 7), type: 'none' } as CronItemState, // 默认为?
});

const weekAlias: Record<number, string> = {
  1: '周日',
  2: '周一',
  3: '周二',
  4: '周三',
  5: '周四',
  6: '周五',
  7: '周六',
};

// 核心：格式化单个字段
const formatItem = (item: CronItemState): string => {
  switch (item.type) {
    case 'every': {
      return '*';
    }
    case 'loop': {
      return `${item.loopStart}/${item.loopStep}`;
    }
    case 'none': {
      return '?';
    }
    case 'range': {
      return `${item.rangeStart}-${item.rangeEnd}`;
    }
    case 'specific': {
      if (item.specificList.length === 0) return '*';
      return item.specificList.sort((a, b) => a - b).join(',');
    }
    default: {
      return '*';
    }
  }
};

// 互斥逻辑
const handleDayChange = () => {
  if (state.day.type !== 'none') {
    state.week.type = 'none';
  }
};

const handleWeekChange = () => {
  if (state.week.type !== 'none') {
    state.day.type = 'none';
  }
};

// 计算最终 Cron 字符串
const cronResult = computed(() => {
  const s = formatItem(state.second);
  const m = formatItem(state.minute);
  const h = formatItem(state.hour);
  const d = formatItem(state.day);
  const M = formatItem(state.month);
  const w = formatItem(state.week);
  return `${s} ${m} ${h} ${d} ${M} ${w}`;
});

// 表格展示数据
const resultTableData = computed(() => [
  {
    second: formatItem(state.second),
    minute: formatItem(state.minute),
    hour: formatItem(state.hour),
    day: formatItem(state.day),
    month: formatItem(state.month),
    week: formatItem(state.week),
  },
]);

const copyCron = () => {
  // if (navigator.clipboard) {
  //   navigator.clipboard.writeText(cronResult.value).then(() => {
  //     ElMessage.success('Cron 表达式已复制');
  //   });
  // } else {
  //   ElMessage.warning('浏览器不支持剪贴板 API');
  // }
  emit('useCron', cronResult.value);
};
const nextTimes = ref<any[]>([]);
function getNextTimes() {
  api
    .get('/api/v1/sysJob/getNextTimes', {
      params: {
        cronExpression: cronResult.value,
      },
    })
    .then((res: any) => {
      nextTimes.value = res.errorCode === 0 ? res.data : [];
    });
}
</script>

<template>
  <div class="cron-generator">
    <ElCard class="box-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>Cron 表达式生成器</span>
        </div>
      </template>

      <ElTabs v-model="activeTab" type="border-card">
        <!-- 秒 -->
        <ElTabPane label="秒" name="second">
          <CrontabPane v-model="state.second" :min="0" :max="59" label="秒" />
        </ElTabPane>

        <!-- 分 -->
        <ElTabPane label="分" name="minute">
          <CrontabPane v-model="state.minute" :min="0" :max="59" label="分" />
        </ElTabPane>

        <!-- 时 -->
        <ElTabPane label="时" name="hour">
          <CrontabPane v-model="state.hour" :min="0" :max="23" label="时" />
        </ElTabPane>

        <!-- 日 -->
        <ElTabPane label="日" name="day">
          <CrontabPane
            v-model="state.day"
            :min="1"
            :max="31"
            label="日"
            week-mode-check
            @change="handleDayChange"
          />
        </ElTabPane>

        <!-- 月 -->
        <ElTabPane label="月" name="month">
          <CrontabPane v-model="state.month" :min="1" :max="12" label="月" />
        </ElTabPane>

        <!-- 周 -->
        <ElTabPane label="周" name="week">
          <CrontabPane
            v-model="state.week"
            :min="1"
            :max="7"
            label="周"
            :alias-map="weekAlias"
            day-mode-check
            @change="handleWeekChange"
          />
        </ElTabPane>
      </ElTabs>

      <!-- 结果展示区域 -->
      <div class="result-area">
        <ElDivider content-position="left">生成结果</ElDivider>
        <div class="result-row">
          <ElInput v-model="cronResult" readonly placeholder="Cron 表达式">
            <template #prepend>Cron 表达式</template>
          </ElInput>
          <ElButton type="primary" @click="copyCron" style="margin-left: 10px">
            使用该值
          </ElButton>
          <ElButton
            type="primary"
            @click="getNextTimes"
            style="margin-left: 10px"
          >
            查看最近5次执行时间
          </ElButton>
        </div>

        <div class="preview-table">
          <ElTable
            :data="resultTableData"
            border
            style="width: 100%"
            size="small"
          >
            <ElTableColumn prop="second" label="秒" />
            <ElTableColumn prop="minute" label="分" />
            <ElTableColumn prop="hour" label="时" />
            <ElTableColumn prop="day" label="日" />
            <ElTableColumn prop="month" label="月" />
            <ElTableColumn prop="week" label="周" />
          </ElTable>
        </div>
        <ElDivider content-position="left">最近5次执行时间</ElDivider>
        <div v-for="(item, idx) in nextTimes" :key="idx">
          {{ item }}
        </div>
      </div>
    </ElCard>
  </div>
</template>

<style scoped>
.cron-generator {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}
.result-area {
  margin-top: 20px;
}
.result-row {
  display: flex;
  margin-bottom: 15px;
}
</style>
