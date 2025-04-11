# Hooks

## 前言
React Hooks 是 React 16.8 版本引入的新特性，
它允许我们在函数组件中使用状态（state）、生命周期方法和其他 React 特性，
从而使函数组件具有类组件相似的能力。在 AIFlowy 中，几乎所有的组件都是函数组件，而非 Class 组件。

在 React 中，如果我们希望在一个函数（方法）中去调用 Hooks，
我们也必须让这个函数变成 Hooks 函数，Hooks 函数要求方法的名称必须以 `use` 开头。

## Hooks 的使用

在 AIFlowy 中，我们内置了许多 Hooks，
方便我们对项目进行二开和复用。AIFlowy 中的所有 Hook 都存放在 src/hooks目录下。

### `useApis.ts`

这个文件是用于存放了 API 调用的 Hooks，这些 Hooks 在调用 API 时，
自动验证当前用户 Token 是否过期，若用户 Token 已过期，则自动调用退出代码让用户退出当前系统。
其支持的 Hooks 列表如下：
 - useGet：用于发送 get 请求
 - useGetManual：用于手动发送 get 请求
 - usePost：用于发送 post 请求
 - usePostManual：用于手动发送 get 请求
 - useSave：用于保存数据
 - useRemove：用于删除数据
 - useRemoveBatch：用于批量删除数据
 - useUpdate：用于更新数据
 - useList：用于请求数据列表
 - usePage：用于分页请求数据列表
 - userDetail：用于用于根据 id 查询数据详情
 - useUpload：用于上传文件

**示例代码**

`useGet`

```typescript jsx
const Profiles: React.FC = () => {

  const {loading, result} = useGet("/api/v1/sysAccount/myProfile");

  return (
    <div>{result.data.nickName}<div/>
  )
}
```

`useGetManual`
```typescript jsx
const Profiles: React.FC = () => {

  const {loading, result, doGet} = useGetManual("/api/v1/public/tcaptcha");

  return (
    <div>{result.data.nickName}<div/>
      
    <button onClick={()=>doGet()}>开始执行</button>  
  )
}
```

> `useGetManual` 要求开发者手动调用 `doGet` 方法，才会开始进行网络请求数据。

### `useAxios.ts`

主要用于通过 axios 框架来加载网络数据，其自动进行缓存、自动添加权限 Token 以及自动添加域名路径等功能。

**示例代码**

```typescript
 const [{loading}, doLogin] = useAxios({
    url: "/api/v1/account/login",  //请求 URL 地址
    method: "post", //请求方法
}, {
    manual: true, // 是否手动请求
    useCache: false //是否使用缓存
});
```
















