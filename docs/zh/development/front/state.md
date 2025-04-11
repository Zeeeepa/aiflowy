# 状态管理

## 前言

在 React （也包括 Vue 等）中，状态管理非常重要，好用的状态管理框架能在大型的应用中，
轻松的在组件之间进行数据传递，无需进行 props 穿透。同时还有简化组件之间的代码逻辑，提高代码性能。

## Zustand

在 AIFlowy 中，我们是使用 [Zustand](https://github.com/pmndrs/zustand) 进行状态管理的，
Zustand 是一个简单且轻量级的状态管理库，它的设计非常简洁，是 Redux 最佳的替代方案。目前已经有 51k 的 star。

## 示例代码

### 定义 Store 的数据类型

```typescript
export interface AppStore {
    nickName?: string,
    avatar?: string,
    jwt?: string,
    isLogin: () => boolean,
    setLogin: (jwt: string, nickName: string) => void,
    setNickname: (nickName: string) => void,
    setAvatar: (avatar: string) => void,
    setJwt: (jwt: string) => void,
    logOut: () => void,
}
```

### 通过 Zustand 创建 Store

```typescript
export const useAppStore = create<AppStore>()(persist(
    (set, get) => ({
        nickName: '',
        avatar: '',
        jwt: '',
        isLogin: () => {
            const token = get().jwt;
            if (!token) {
                return false;
            }
            const tokenParts = token.split('.');
            if (tokenParts.length != 3) {
                return false;
            }
            const payload = tokenParts[1];
            try {
                const {exp} = JSON.parse(window.atob(payload))
                return Date.now() < exp * 1000;
            } catch (e) {
                return false;
            }
        },
        setLogin: (jwt: string, nickName: string) => {
            localStorage.setItem(authKey, jwt)
            set({nickName: nickName, jwt: jwt})
        },
        setNickname: (nickName: string) => {
            set({nickName: nickName})
        },
        setAvatar: (avatar: string) => {
            set({avatar: avatar})
        },
        setJwt: (jwt: string) => {
            localStorage.setItem(authKey, jwt)
            set({jwt: jwt})
        },
        logOut: () => {
            localStorage.removeItem(authKey)
            set({nickName: '', avatar: '', jwt: ''})
        },
    }), {
        name: "custom-store",
        storage: createJSONStorage(() => localStorage)
    })
)
```

### 使用 Store

在使用 store 方面，涉及两个部分：
 - 1 修改 store 的数据
 - 2 监听 store 的数据变化

在 AIFlowy 中，有一个名称为 `Setting.tsx` 的页面组件，用于修改用户的资料，修改成功后会更新 store 的数据，代码如下：

```typescript
    const store = useAppStore();

    const onFinish: FormProps['onFinish'] = (values) => {
        doPost({
            data: values
        }).then(resp => {
            if (resp.data.errorCode == 0) {
                
                //更新 store 的数据
                store.setNickname(values.nickname as string)
                message.success("个人资料修改成功！")
            }
        })
    };
```































