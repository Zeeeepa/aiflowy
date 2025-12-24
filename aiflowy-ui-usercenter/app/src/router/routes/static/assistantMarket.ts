import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    name: 'AssistantMarket',
    path: '/assistantMarket',
    component: () => import('#/views/assistantMarket/index.vue'),
    meta: {
      icon: 'svg:app-store',
      order: 11,
      title: '助理市场',
    },
  },
  {
    name: 'Assistant',
    path: '/assistantMarket/:id',
    component: () => import('#/views/assistantMarket/assistant/index.vue'),
    meta: {
      title: '助理市场',
      hideInMenu: true,
      hideInTab: true,
      hideInBreadcrumb: true,
      noBasicLayout: true,
      activePath: '/assistantMarket',
    },
  },
];

export default routes;
