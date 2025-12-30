<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { reactive, ref } from 'vue';

import { IconifyIcon } from '@aiflowy/icons';

import { MoreFilled, View } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import DictSelect from '#/components/dict/DictSelect.vue';
import PageData from '#/components/page/PageData.vue';

const formRef = ref<FormInstance>();
const formData = reactive({
  type: '',
  status: '',
  name: '',
});
// TODO: 功能未实现
const pageDataRef = ref();

function search(formEl?: FormInstance) {
  formEl?.validate((valid) => {
    if (valid) {
      // pageDataRef.value.setQuery(formInline.value);
    }
  });
}
function reset(formEl?: FormInstance) {
  formEl?.resetFields();
  // pageDataRef.value.setQuery({});
}
</script>

<template>
  <div class="flex h-full flex-col gap-1.5 p-6">
    <ElForm ref="formRef" inline :model="formData">
      <ElFormItem prop="resourceType" class="!mr-3">
        <DictSelect
          v-model="formData.type"
          dict-code="resourceType"
          :placeholder="$t('sysFeedback.feedbackType')"
        />
      </ElFormItem>
      <ElFormItem prop="resourceType" class="!mr-3">
        <DictSelect
          v-model="formData.status"
          dict-code="resourceType"
          :placeholder="$t('sysFeedback.processingStatus')"
        />
      </ElFormItem>
      <ElFormItem prop="resourceName" class="!mr-3">
        <ElInput
          v-model="formData.name"
          :placeholder="$t('common.searchPlaceholder')"
        />
      </ElFormItem>
      <ElFormItem>
        <ElButton @click="search(formRef)" type="primary">
          {{ $t('button.query') }}
        </ElButton>
        <ElButton @click="reset(formRef)">
          {{ $t('button.reset') }}
        </ElButton>
      </ElFormItem>
    </ElForm>

    <div class="bg-background border-border flex-1 rounded-lg border p-5">
      <PageData ref="pageDataRef" page-url="" :page-size="10">
        <template #default="{ pageList }">
          <ElTable border show-overflow-tooltip :data="pageList">
            <ElTableColumn prop="id" label="ID">
              <template #default="{ row }">
                {{ row.id }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="category" :label="$t('sysFeedback.category')">
              <template #default="{ row }">
                {{ row.category }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="description"
              :label="$t('sysFeedback.description')"
            >
              <template #default="{ row }">
                {{ row.description }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="contactInformation"
              :label="$t('sysFeedback.contactInformation')"
            >
              <template #default="{ row }">
                {{ row.contactInformation }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="submittedAt"
              :label="$t('sysFeedback.submittedAt')"
            >
              <template #default="{ row }">
                {{ row.submittedAt }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="processingStatus"
              :label="$t('sysFeedback.processingStatus')"
            >
              <template #default="{ row }">
                <ElTag type="success">
                  {{ row.processingStatus }}
                </ElTag>
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="90"
              align="right"
            >
              <template #default="{ row }">
                <div class="flex items-center gap-3">
                  <ElButton link type="primary">
                    {{ $t('button.view') }}
                  </ElButton>

                  <ElDropdown>
                    <ElButton link :icon="MoreFilled" />

                    <template #dropdown>
                      <ElDropdownMenu>
                        <ElDropdownItem :icon="View">
                          {{ $t('button.markAsRead') }}
                        </ElDropdownItem>
                        <ElDropdownItem>
                          <ElIcon>
                            <IconifyIcon icon="svg:resolved" />
                          </ElIcon>
                          {{ $t('button.markAsResolved') }}
                        </ElDropdownItem>
                      </ElDropdownMenu>
                    </template>
                  </ElDropdown>
                </div>
              </template>
            </ElTableColumn>
          </ElTable>
        </template>
      </PageData>
    </div>
  </div>
</template>
