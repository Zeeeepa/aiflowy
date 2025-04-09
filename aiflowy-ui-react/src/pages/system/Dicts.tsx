import React from 'react';
import {SettingOutlined} from "@ant-design/icons";
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import DictItems from "./DictItems.tsx";
import Linkto from "../../components/Linkto";

const columns: ColumnsConfig<any> = [
    {
        dataIndex: 'id',
        key: 'id',
        hidden: true,
        form: {
            type: "Hidden"
        }
    },
    {
        title: '字典名称',
        dataIndex: 'name',
        key: 'name',
        placeholder: "请输入项目名称",
        supportSearch: true,
    },
    {
        title: '编码',
        dataIndex: 'code',
        key: 'code',
        placeholder: "请输入项目编码",
        supportSearch: true,
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "50%",
        form: {
            type: "textarea",
            attrs: {
                rows: 4,
            }
        }
    },
    {
        title: '字典类型',
        key: "dictType",
        supportSearch: true,
        dict: {
            name: "dictType",
            editCondition: (item) => {
                return item.key != 4;
            }
        },
        form: {
            type: "select",
        }
    },
    {
        title: '表名',
        dataIndex: 'options.tableName',
        key: 'options.tableName',
        placeholder: "请输入表名",
        hidden: true,
        editCondition: (data) => {
            return data?.dictType === 2;
        }
    },
    {
        title: '标题的列名',
        dataIndex: 'options.keyColumn',
        key: 'options.keyColumn',
        placeholder: "请输入列名",
        hidden: true,
        editCondition: (data) => {
            return data?.dictType === 2;
        }
    },
    {
        title: '内容的列名',
        dataIndex: 'options.labelColumn',
        key: 'options.labelColumn',
        placeholder: "请输入列名",
        hidden: true,
        editCondition: (data) => {
            return data?.dictType === 2;
        }
    },
    {
        title: '父级 ID 列名',
        dataIndex: 'options.parentColumn',
        key: 'options.parentColumn',
        placeholder: "请输入列名",
        hidden: true,
        editCondition: (data) => {
            return data?.dictType === 2;
        },
        form: {
            extra: "当字典为树形结构时，需配置此项内容"
        }
    },

    {
        title: '类名',
        dataIndex: 'options.enumClass',
        key: 'options.enumClass',
        placeholder: "请输入类名",
        hidden: true,
        editCondition: (data) => {
            return data?.dictType === 3;
        }
    },
    {
        title: '值的字段名',
        dataIndex: 'options.keyField',
        key: 'options.keyField',
        placeholder: "请输入字段名",
        hidden: true,
        editCondition: (data) => {
            return data?.dictType === 3;
        }
    },
    {
        title: '内容的字段名',
        dataIndex: 'options.labelField',
        key: 'options.labelField',
        placeholder: "请输入字段名",
        hidden: true,
        editCondition: (data) => {
            return data?.dictType === 3;
        }
    },
];


const Dicts: React.FC = () => {

    const actionConfig = {
        detailButtonEnable: false,
        width: 220,
        customActions: (data) => {
            return (
                data.dictType === 1 &&
                <Linkto component={DictItems} params={{dictId: data.id}} title={"字典内容配置"}><SettingOutlined/> 内容配置</Linkto>
            )
        },
    } as ActionConfig<any>

    return (
        <CrudPage columnsConfig={columns} tableAlias="sysDict" actionConfig={actionConfig}/>
    )
};

export default {
    path: "/system/dicts",
    element: Dicts,
};
