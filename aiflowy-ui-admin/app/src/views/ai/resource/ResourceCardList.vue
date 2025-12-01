<script setup lang="ts">
import { ref, watch } from 'vue';

import { $t } from '#/locales';

import { ElCard, ElCheckbox, ElCol, ElRadio, ElRow } from 'element-plus';

export interface ResourceCardProps {
  data: any[];
  multiple?: boolean;
  valueProp?: string;
}
const props = withDefaults(defineProps<ResourceCardProps>(), {
  multiple: false,
  valueProp: 'id',
});

const emit = defineEmits(['update:modelValue']);

const radioValue = ref('');
const checkAll = ref(false);
function choose() {
  const arr = [];
  if (props.multiple) {
    for (const data of props.data) {
      if (data.checkboxValue) {
        arr.push(data);
      }
    }
  } else {
    if (radioValue.value) {
      for (const data of props.data) {
        if (data[props.valueProp] === radioValue.value) {
          arr.push(data);
        }
      }
    }
  }
  emit('update:modelValue', arr);
}
function handleCheckAllChange(val: any) {
  if (val) {
    for (const data of props.data) {
      data.checkboxValue = data[props.valueProp];
    }
  } else {
    for (const data of props.data) {
      data.checkboxValue = '';
    }
  }
}
watch(
  [() => radioValue.value, () => props.data],
  () => {
    choose();
  },
  { deep: true },
);
</script>

<template>
  <div>
    <ElCheckbox
      v-if="multiple"
      :label="$t('button.selectAll')"
      v-model="checkAll"
      @change="handleCheckAllChange"
    />
    <ElRow :gutter="20">
      <ElCol :span="6" v-for="item in data" :key="item.id" class="mb-5">
        <ElCard
          :body-style="{ padding: '12px', height: '220px' }"
          shadow="hover"
        >
          <template #header>
            <ElCheckbox
              v-if="multiple"
              v-model="item.checkboxValue"
              :true-value="item[valueProp]"
              false-value=""
            />
            <ElRadio v-else v-model="radioValue" :value="item[valueProp]" />
          </template>
          {{ item.resourceName }}
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<style scoped></style>
