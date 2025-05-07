# 文件生成

![file-gen.png](../resource/file-gen.png)

- 输入参数：`content` 文件内容。
- 输出参数：`url` 文件下载地址。

## 后端实现
在 `aiflowy-module-ai` 模块下的 `node` 包中。

`MakeFileNodeParser` 和 `MakeFileNode` 类。

> 目前实现了 `docx` 文件的生成，后续会添加更多支持。