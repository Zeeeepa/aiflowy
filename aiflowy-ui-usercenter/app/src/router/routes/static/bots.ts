import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    name: 'Bots',
    path: '/bots',
    component: () => import('#/views/bots/index.vue'),
    meta: {
      icon: 'mdi:robot-outline',
      order: 1,
      title: '智能体',
    },
  },
];

export default routes;
