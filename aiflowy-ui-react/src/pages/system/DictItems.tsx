import React from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";

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
        title: '内容',
        dataIndex: 'text',
        key: 'text',
        placeholder: "请输入内容",
        supportSearch: true,
    },
    {
        title: '值',
        dataIndex: 'value',
        key: 'value',
        supportSearch: true,
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        supportSearch: true,
        form: {
            type: "textarea",
            attrs: {
                rows: 4,
            }
        }
    },
    {
        title: '排序序号',
        dataIndex: 'sortNo',
        key: 'sortNo',
    },
    {
        title: '是否禁用',
        dataIndex: 'status',
        key: 'status',
        render: (text: any) => {
            return text === 1 ? "已禁用" : "正常";
        },
        form:{
            type: "switch"
        },
    },
];

interface Props {
    dictId: any,
}

const DictItems: React.FC<Props> = ({dictId}) => {
    const actionConfig = {
        detailButtonEnable: false,
        width: 130,
    } as ActionConfig<any>
    return (
        <CrudPage columnsConfig={columns} tableAlias="sysDictItem" actionConfig={actionConfig} params={{dictId}}/>
    )
};

export default DictItems;
