<script setup lang="ts">
import { Avatar, Flex, Layout, LayoutContent, LayoutHeader, LayoutSider } from 'ant-design-vue'

import CustomMenu from '@/components/CustomMenu.vue'
import AvatarIcon from '@/components/icons/IconAvatar.vue'
</script>

<template>
  <RouterView v-if="$route.meta.hideMenu" />
  <Layout v-else class="h-screen">
    <LayoutSider width="280">
      <Flex vertical justify="space-between" class="h-full pb-8.75">
        <Flex vertical>
          <img src="@/assets/logo.webp" alt="logo" class="mx-auto my-8.25 w-57.25 object-fill" />
          <CustomMenu />
        </Flex>
        <Flex align="center" justify="center">
          <Flex vertical align="start" class="text-xs text-black/25">
            <Flex>
              <span class="cursor-pointer hover:underline">帮助中心</span>｜
              <span class="cursor-pointer hover:underline">联系我们</span>
            </Flex>
            <span>ICP备案：黔ICP备XXXXXXXX号</span>
          </Flex>
        </Flex>
      </Flex>
    </LayoutSider>
    <Layout class="layout-right">
      <LayoutHeader>
        <Flex justify="space-between">
          <i />
          <Avatar :size="46">
            <template #icon>
              <AvatarIcon class="text-[46px]" />
            </template>
          </Avatar>
        </Flex>
      </LayoutHeader>
      <LayoutContent>
        <RouterView />
      </LayoutContent>
    </Layout>
  </Layout>
</template>

<style lang="css" scoped>
.ant-layout-sider {
  background: linear-gradient(180deg, rgba(0, 102, 255, 0.06), rgba(0, 159, 255, 0.04));
}

.layout-right {
  background: linear-gradient(135deg, white, #eff8ff);
}

.ant-layout-header {
  height: fit-content;
  padding: 30px 32px;
  background: transparent;
}
</style>
