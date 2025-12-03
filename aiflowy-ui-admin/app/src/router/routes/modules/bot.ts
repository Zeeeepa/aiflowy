import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    name: 'BotRun',
    path: '/ai/bots/run/:botId/:sessionId?',
    component: () => import('#/views/ai/bots/pages/Run.vue'),
    meta: {
      title: 'Bots',
      noBasicLayout: true,
      openInNewWindow: true,
      hideInMenu: true,
      hideInBreadcrumb: true,
      hideInTab: true,
    },
  },
  {
    name: 'BotSetting',
    path: '/ai/bots/setting/:id',
    component: () => import('#/views/ai/bots/pages/setting/index.vue'),
    meta: {
      title: 'Bots',
      openInNewWindow: true,
      hideInMenu: true,
      hideInBreadcrumb: true,
      hideInTab: true,
      activePath: '/ai/bots',
    },
  },
];

export default routes;
