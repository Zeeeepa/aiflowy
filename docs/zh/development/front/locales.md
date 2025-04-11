# 国际化

在 AIFlowy 中，已经内置了 `i18next` 库，用于国际化的场景。

## 使用方法

在 `src/locales/translations` 目录中，内置两个 `en.json` 和 `zh.json` 文件。
用于存放英文和中文的语言文件。如下所示：

```json
{
  "page2": "页面2",
  "menu": {
    "home": "首页",
    "user management": "用户管理",
    "system management": "系统管理"
  }
}
```
然后，在任何 React 组件中，通过 useTranslation()Hook 使用即可，示例代码如下：

```typescript jsx
function App() {
    const [t] = useTranslation();
    return (
        <div>
          {t("page2")}
        </div>
    )
}

```
以上代码中，显示 “`页面2`”。















