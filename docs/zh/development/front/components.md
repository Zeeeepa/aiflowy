# 组件使用

## CrudPage 组件

CrudPage 组件是 AIFlowy 中，最为重要的组件之一，用于实现对于一张数据库表的增删改查功能。

### 使用方法

`CrudPage` 的使用分为如下步骤：
 - 定义列信息。
 - 配置 `tableAlias` 表别名。
 - 通过 `<CrudPage columnsConfig... />` 进行使用，如下代码所示：

```typescript jsx
// 1.定义列信息
const columns: ColumnsConfig<any> = [
    {
        dataIndex: 'id',
        key: 'id',
        hidden: true,
        form: {
            type: "hidden"
        }
    },
    {
        title: '登录账号',
        dataIndex: 'loginName',
        key: 'loginName',
        placeholder: "请输入登录账号",
        supportSearch: true,
    },
    
    //.....
];

export const Accounts: React.FC<> = () => {
    return (
        // 2.使用 CrudPage 组件
        <CrudPage columnsConfig={columns} tableAlias="sysAccount"/>
    )
};
```
在以上代码中：
 - `columns` 用于定义列的信息，其用于配置列表显示，搜索功能，以及编辑功能等。
 - `tableAlias` 的配置，用于指定是哪一张数据库表对应的实体类的别名，
例如实体类的名称为 `SysAccount.java` ，那么，别名的值应该为 `sysAccount` 。

### 配置

`CrudPage` 除了 `columnsConfig` 和 `tableAlias` 的配置以外，还支持其他许多额外的配置，具体如下所示：

```typescript
interface CurdPageProps {
    // 表别名，用于对哪个表进行增删改查操作
    tableAlias: string,
    // 显示类型，分别指的是`列表显示`还是`分页显示`。
    showType?: "list" | "page",
    // 在分页的情况下，设置每页数据量，默认为 10
    defaultPageSize?: number,
    // 在有上下级的列表中，是否默认展开所有 
    defaultExpandedAllRow?: boolean,
    // 列信息配置
    columnsConfig: ColumnsConfig<any>,
    // 操作项配置
    actionConfig?: ActionConfig<any>,
    // 是否显示新增按钮，默认显示
    addButtonEnable?: boolean,
    // 列表页顶部的自定义按钮
    customButton?: () => React.ReactNode | null,
    // 是否启用行选择功能，默认启用
    rowSelectEnable?: boolean,
    // 当前页面是否需要额外的参数，默认情况下二级页面一般都是需要上一级页面的参数的
    params?: any,
    // 是否让 url 和 搜索参数进行同步保持，一般只有在第一级别页面中启用
    paramsToUrl?: boolean,
    // 编辑页面配置
    editLayout?: EditLayout
}
```

#### 列配置 `columnsConfig`

columns 配置包含了 AntDesign Table 的 Column 配置，具体可以看 [ant design columns](https://ant-design.antgroup.com/components/table-cn#column)，这里只介绍特有的一些配置。

```typescript
export type ColumnConfig<RecordType = unknown> = ((ColumnGroupType<RecordType> | ColumnType<RecordType>) & {
    // 编辑类型
    form?: FormConfig,
    // 数据字典，配置后，自动去请求后台的数据字典 url 来填充数据
    dict?: string | DictConfig,
    // 分组的 key
    groupKey?: string,
    // 占位字符
    placeholder?: string,
    // 是否支持搜索
    supportSearch?: boolean,
    // 值改变事件
    onValuesChange?: (changedValues: any, allValues: any) => void,
    // 编辑条件，决定编辑时是否显示
    editCondition?: (data: any) => boolean,
});
```
**`form`**
```typescript
export type FormConfig = {
    // 组件的类型，input select 等
    type?: string,
    // 是否包裹 col 组件，如果包裹，由 DynamicFormItem 自行包裹
    wrapCol?: boolean,
    // 组件的其他属性，如 select 组件的 options 等
    attrs?: any,
    // 验证规则，ant design form rule
    rules?: Rule[],
    // 备注说明
    extra?: string,
}
```
**`dict`**

```typescript
export type DictConfig = {
    // 请求地址
    url?: string,
    // 字典 key
    name?: string,
    // 是否是树形结构
    asTree?: boolean,
    // 查询参数 keys
    paramKeys?: string[],
    // 禁用项在表格记录中的 key
    disabledItemAndChildrenKey?: string,
    // 字典在编辑时是否显示
    editCondition?: (item: any) => boolean,
    // 字典的额外数据
    editExtraData?: any[]
}
```

#### 操作按钮配置 `actionConfig`

```typescript
export type ActionConfig<T> = {
    // 是否隐藏
    hidden?: boolean,
    // 标题
    title?: string,
    // fixed 位置
    fixedRight?: boolean,
    // 宽度
    width?: number | string,
    // 是否显示详情按钮
    detailButtonEnable?: boolean,
    // 是否显示编辑按钮
    editButtonEnable?: boolean,
    // 是否显示删除按钮
    deleteButtonEnable?: boolean,
    // 自定义按钮
    customActions?: (data: T) => JSX.Element | null
}
```
#### 弹窗配置 `editLayout`

```typescript
export type EditLayout = {
    // 弹窗类型
    openType?: "modal" | "drawer",
    // 列数
    columnsCount?: number,
    // 标签布局
    labelLayout?: "horizontal" | "vertical",
    // 标签宽度
    labelWidth?: number,
    // 编辑页自定义按钮
    customButton?: () => JSX.Element | null
}
```











