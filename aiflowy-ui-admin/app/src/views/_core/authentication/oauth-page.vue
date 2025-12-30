<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';

import { useAccessStore, useUserStore } from '@aiflowy/stores';

import { getAccessCodesApi, getUserInfoApi } from '#/api';

const accessStore = useAccessStore();
const userStore = useUserStore();
const route = useRoute();
const token: any = route.query.token;
onMounted(() => {
  redirect();
});
async function redirect() {
  accessStore.setAccessToken(token);
  const [fetchUserInfoResult, accessCodes] = await Promise.all([
    getUserInfoApi(),
    getAccessCodesApi(),
  ]);
  userStore.setUserInfo(fetchUserInfoResult);
  accessStore.setAccessCodes(accessCodes);
  window.location.href = '/';
}
</script>

<template>
  <div>redirecting...</div>
</template>

<style scoped></style>
