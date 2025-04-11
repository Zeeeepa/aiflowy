# 目录结构

## 项目结构

```
├── aiflowy-commons                 # 公共模块
├── aiflowy-modules                 # 业务模块
└── aiflowy-starter                 # 启动项目
├── aiflowy-ui-react                # react 端
├── aiflowy-ui-vue                  # vue 端
├── docs                            # 文档
├── sql                             # sql 脚本
├── pom.xml
└── README.md                       # 项目介绍
```

## 后端目录结构

```
├── aiflowy-commons                 # 公共模块
│   ├── aiflowy-common-ai           # ai 相关的基础配置
│   ├── aiflowy-common-all          
│   ├── aiflowy-common-base         # 一些公用资源
│   ├── aiflowy-common-cache        # 缓存相关
│   ├── aiflowy-common-file-storage # 文件存储相关
│   ├── aiflowy-common-options      # 系统配置相关
│   ├── aiflowy-common-satoken      # sa-token 配置
│   ├── aiflowy-common-sms          # 短信相关
│   ├── aiflowy-common-tcaptcha     # 验证码相关
│   ├── aiflowy-common-web          # web 相关通用配置
│   └── pom.xml
├── aiflowy-modules                 # 业务模块
│   ├── aiflowy-module-ai           # ai 相关，如插件、知识库等
│   ├── aiflowy-module-auth         # 鉴权模块
│   ├── aiflowy-module-autoconfig   # 自动配置
│   ├── aiflowy-module-common       # 公共模块
│   ├── aiflowy-module-core         # 核心模块，如字典加载器等
│   ├── aiflowy-module-log          # 日志模块
│   ├── aiflowy-module-system       # 系统模块，如用户、角色、菜单等
│   └── pom.xml
└── aiflowy-starter                 # 启动项目
```

## 前端目录结构 - React
```
├── dist                       # 编译产物，正式部署用到其内容
├── mock                       # Mokc 数据，数据模拟
├── public                     # 公共静态资源文件，其目录下的 url 结构不会发生变化
├── src
│   ├── assets                 # 一些静态文件，比如图片等
│   ├── components             # AIFlowy 的自定义组件
│   ├── hooks                  # AIFlowy 的自定义 hook 文件
│   ├── libs                   # 一些工具类
│   ├── locales                # 国际化相关
│   ├── pages                  # 页面的根目录
│   │   ├── ai                 # ai 相关的页面
│   │   ├── commons            # 通用页面
│   │   └── system             # 系统相关页面
│   ├── routers
│   │   └── router.tsx         # 路由的自动加载处理
│   ├── store
│   │   └── appStore.ts        # Zustand 的 store 模块
│   ├── types                  # 涉及的一些通用类定义
│   │   ├── Page.ts
│   │   └── Result.ts
│   ├── App.less               # 整个 APP 涉及的一些通用样式
│   ├── App.tsx                # App 初始化跟节点
│   ├── config.tsx             # 整个前端应用的一些配置
│   ├── main.tsx               # 启动入口
│   └── vite-env.d.ts          
├── index.html                 # 入口文件
├── tsconfig.json              # Typescript 的一些配置
├── tsconfig.node.json         # Node 的一些配置
├── package-lock.json           
├── package.json               # 依赖以及项目配置
└── vite.config.ts             # vite 编译的环境变量的配置
```