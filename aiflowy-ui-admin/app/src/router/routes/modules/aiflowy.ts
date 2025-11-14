import type { RouteRecordRaw } from 'vue-router';

import {
  VBEN_DOC_URL,
  VBEN_GITHUB_URL,
  VBEN_LOGO_URL,
} from '@aiflowy/constants';

import { IFrameView } from '#/layouts';
import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      badgeType: 'dot',
      icon: VBEN_LOGO_URL,
      order: 9998,
      title: $t('demos.aiflowy.title'),
    },
    name: 'AIFlowyProject',
    path: '/aiflowy-admin',
    children: [
      {
        name: 'AIFlowyDocument',
        path: '/aiflowy-admin/document',
        component: IFrameView,
        meta: {
          icon: 'lucide:book-open-text',
          link: VBEN_DOC_URL,
          title: $t('demos.aiflowy.document'),
        },
      },
      {
        name: 'AIFlowyGithub',
        path: '/aiflowy-admin/github',
        component: IFrameView,
        meta: {
          icon: 'mdi:github',
          link: VBEN_GITHUB_URL,
          title: 'Github',
        },
      },
    ],
  },
  {
    name: 'AIFlowyAbout',
    path: '/aiflowy-admin/about',
    component: () => import('#/views/_core/about/index.vue'),
    meta: {
      icon: 'lucide:copyright',
      title: $t('demos.aiflowy.about'),
      order: 9999,
    },
  },
  {
    name: 'Profile',
    path: '/profile',
    component: () => import('#/views/_core/profile/index.vue'),
    meta: {
      icon: 'lucide:user',
      hideInMenu: true,
      title: $t('page.auth.profile'),
    },
  },
];

export default routes;
