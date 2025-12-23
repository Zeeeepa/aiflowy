<script setup lang="ts">
import { provide, ref, watch } from 'vue';

import { cn } from '@aiflowy/utils';

import { Delete, Edit, MoreFilled } from '@element-plus/icons-vue';
import {
  ElAside,
  ElButton,
  ElContainer,
  ElDialog,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElHeader,
  ElInput,
  ElMain,
  ElMessage,
  ElMessageBox,
  ElTooltip,
} from 'element-plus';

import { api } from '#/api/request';
import AssistantAvatar from '#/components/avatar/Assistant.vue';
import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';
import ChatIcon from '#/components/icons/ChatIcon.vue';
import { $t } from '#/locales';

interface Props {
  bot: any;
  onMessageList?: (list: any[]) => void;
}
const props = defineProps<Props>();
const sessionList = ref<any>([]);
const currentSession = ref<any>({});
const hoverId = ref<string>();
const dialogVisible = ref(false);
watch(
  () => props.bot.id,
  () => {
    getSessionList(true);
  },
);
defineExpose({
  getSessionList,
});

function getSessionList(resetSession = false) {
  api
    .get('/userCenter/conversation/list', {
      params: {
        botId: props.bot.id,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        sessionList.value = res.data;
        if (resetSession) {
          currentSession.value = {};
        }
      }
    });
}
provide('getSessionList', getSessionList);
function addSession() {
  const data = {
    botId: props.bot.id,
    title: '新对话',
    sessionId: crypto.randomUUID(),
  };
  sessionList.value.push(data);
}
function clickSession(session: any) {
  currentSession.value = session;
  getMessageList();
}
function getMessageList() {
  api
    .get('/userCenter/aiBotMessage/getMessages', {
      params: {
        botId: props.bot.id,
        sessionId: currentSession.value.sessionId,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        props.onMessageList?.(res.data);
      }
    });
}
function formatCreatedTime(time: string) {
  if (time) {
    const createTime = Math.floor(new Date(time).getTime() / 1000);
    const today = Math.floor(Date.now() / 1000 / 86_400) * 86_400;
    return time.split(' ')[createTime < today ? 0 : 1];
  }
  return '';
}
const handleMouseEvent = (id?: string) => {
  if (id === undefined) {
    setTimeout(() => {
      hoverId.value = id;
    }, 200);
  } else {
    hoverId.value = id;
  }
};
const updateLoading = ref(false);
function updateTitle() {
  updateLoading.value = true;
  api
    .get('/userCenter/conversation/updateConversation', {
      params: {
        botId: props.bot.id,
        sessionId: currentSession.value.sessionId,
        title: currentSession.value.title,
      },
    })
    .then((res) => {
      updateLoading.value = false;
      if (res.errorCode === 0) {
        dialogVisible.value = false;
        ElMessage.success('成功');
        getSessionList();
      }
    });
}
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .get('/userCenter/conversation/deleteConversation', {
            params: {
              botId: props.bot.id,
              sessionId: row.sessionId,
            },
          })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              props.onMessageList?.([]);
              currentSession.value = {};
              ElMessage.success(res.message);
              done();
              getSessionList();
            }
          })
          .catch(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
}
</script>

<template>
  <ElContainer class="border-border bg-background h-full rounded-lg border">
    <ElAside width="287px" class="border-border border-r p-6">
      <Card class="max-w-max p-0">
        <AssistantAvatar :src="bot.icon" />
        <CardContent>
          <CardTitle>{{ bot.title }}</CardTitle>
          <CardDescription>{{ bot.description }}</CardDescription>
        </CardContent>
      </Card>
      <ElButton
        class="mt-6 !h-10 w-full !text-sm"
        type="primary"
        :icon="ChatIcon"
        plain
        @click="addSession"
      >
        新建会话
      </ElButton>
      <div class="mt-8">
        <div
          v-for="session in sessionList"
          :key="session.sessionId"
          :class="
            cn(
              'group flex h-10 cursor-pointer items-center justify-between gap-1 rounded-lg px-5 text-sm',
              currentSession.sessionId === session.sessionId
                ? 'bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                : 'hover:bg-[hsl(var(--accent))]',
            )
          "
          @click="clickSession(session)"
        >
          <ElTooltip :content="session.title || '未命名'">
            <span
              :class="
                cn(
                  'text-foreground overflow-hidden text-ellipsis text-nowrap',
                  currentSession.sessionId === session.sessionId &&
                    'text-primary',
                )
              "
            >
              {{ session.title || '未命名' }}
            </span>
          </ElTooltip>
          <span
            :class="
              cn(
                'text-foreground/50 text-nowrap text-xs group-hover:hidden',
                hoverId === session.sessionId && 'hidden',
              )
            "
          >
            {{ formatCreatedTime(session.created) }}
          </span>
          <ElDropdown
            :class="
              cn(
                'group-hover:!inline-flex',
                (!hoverId || session.sessionId !== hoverId) && '!hidden',
              )
            "
            @click.stop
            trigger="click"
          >
            <ElButton link :icon="MoreFilled" />

            <template #dropdown>
              <ElDropdownMenu
                @mouseenter="handleMouseEvent(session.sessionId)"
                @mouseleave="handleMouseEvent()"
              >
                <ElDropdownItem @click="dialogVisible = true">
                  <ElButton link :icon="Edit">编辑</ElButton>
                </ElDropdownItem>
                <ElDropdownItem>
                  <ElButton
                    @click="remove(session)"
                    link
                    type="danger"
                    :icon="Delete"
                  >
                    删除
                  </ElButton>
                </ElDropdownItem>
              </ElDropdownMenu>
            </template>
          </ElDropdown>
        </div>
      </div>
    </ElAside>
    <ElContainer>
      <ElHeader
        class="rounded-tr-lg border border-[#E6E9EE] bg-[#FAFAFA]"
        height="53px"
      >
        <span class="text-base/[53px] font-medium text-[#323233]">
          {{ currentSession.title || '未命名' }}
        </span>
      </ElHeader>
      <ElMain>
        <slot :session-id="currentSession.sessionId"></slot>
      </ElMain>
    </ElContainer>
    <ElDialog title="编辑" v-model="dialogVisible">
      <div class="p-5">
        <ElForm>
          <ElFormItem>
            <ElInput
              v-model="currentSession.title"
              placeholder="请输入会话名称"
            />
          </ElFormItem>
        </ElForm>
      </div>

      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="updateTitle" :loading="updateLoading">
          确认
        </ElButton>
      </template>
    </ElDialog>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-button :deep(.el-icon) {
  font-size: 20px;
}
</style>
