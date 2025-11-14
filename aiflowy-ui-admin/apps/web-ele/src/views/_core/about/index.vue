<script lang="ts" setup>
import { onMounted } from 'vue';

import { About } from '@vben/common-ui';

import { requestClient } from '#/api/request';

defineOptions({ name: 'About' });

onMounted(() => {
  test();
});
function test() {
  requestClient
    .get('/api/v1/sysAccount/myProfile')
    .then(() => {
      // console.log('res', res);
    })
    .catch((error) => {
      console.error('error', error);
    });
}
</script>

<template>
  <About />
</template>
