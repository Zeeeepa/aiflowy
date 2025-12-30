import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    name: 'SysFeedbackDetail',
    path: '/sys/sysFeedback/:id',
    component: () => import('#/views/system/sysFeedback/sysFeedbackDetail.vue'),
    meta: {
      title: $t('menus.system.sysFeedback'),
      hideInMenu: true,
      hideInBreadcrumb: true,
      hideInTab: true,
      activePath: '/sys/sysFeedback',
    },
  },
];

export default routes;
