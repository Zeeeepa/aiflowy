import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    name: 'AssetLibrary',
    path: '/assetLibrary',
    component: () => import('#/views/assetLibrary/index.vue'),
    meta: {
      icon: 'svg:asset-library',
      order: 77,
      title: '我的素材',
    },
  },
];

export default routes;
