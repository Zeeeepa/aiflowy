# 路由管理

在 AIFlowy 中，React 的路由是自动发现以及自动管理的。
只需要我们在 pages 目录下，创建任意 React 页面组件，并以下如下的格式导出，就能被 AIFlowy 的路由管理器接管并管理，例如：
```typescript jsx
const MyPage: React.FC = () => {
    return (
      <div>
        ...
      </div>
    );
};

// 导出内容非常重要，需要导出一个带有 path 以及 element 的对象
export default {
    path: "/your/path",
    element: MyPage
};
```
> 在以上代码中，我们可以通过浏览器访问 /your/path 就能够正常访问到这个 React 页面组件，不需要再进行其他额外的路由配置。

## 实现原理

AIFlowy 的路由逻辑代码在 src/routers/router.txs中，其核心代码如下：

```typescript
export const portalRouters: RouteObject[] = []

/**
 * 自动导入 pages 目录下的页面
 */
export const autoImportPages = import.meta.glob('../pages/*/*.tsx', {eager: true});

/**
 * 通过 import.meta 导入的路由配置，全部添加到变量 portalPages 上去
 */
Object.entries(autoImportPages).map(([_path, module]) => {
    const pageRoute = (module as any).default;
    if (typeof pageRoute === "object" && pageRoute.path && pageRoute.element) {
        //在 page 的导出定义中，element 可以是一个方法，也可以是一个节点，比如：<Login>
        if (typeof pageRoute.element === "function") {
            portalRouters.push({
                ...pageRoute,
                //paramsToUrl 保存 CURD 的搜索、分页等数据同步到 URL
                element: React.createElement(pageRoute.element, {paramsToUrl: true})
            })
        } else {
            portalRouters.push(pageRoute)
        }
    }
})
```
其通过 `import.meta.glob` 自动导入 `../pages/*/*.tsx` 下的所有内容，若其 `default` 导入是一个 `object`，并有 `path` 和 `element` 属性，那么会自动通过 React 来创建这个 Element，并添加到 `portalRouters` 路由管理器中去。

最后，通过 `React-Router` 的 `createBrowserRouter(routers)` 创建路由，并导出给 `App.tsx` 下的
`<RouterProvider>`。





