import React from 'react';
import {ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {dateFormat} from "../../libs/utils.ts";

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
        title: '操作',
        dataIndex: 'account.nickname',
        key: 'account.nickname',
        supportSearch: true,
    },
    {
        title: '操作',
        dataIndex: 'actionName',
        key: 'actionName',
        supportSearch: true,
    },
    {
        title: '类型',
        dataIndex: 'actionType',
        key: 'actionType',
        supportSearch: true,
    },
    {
        title: '涉及类',
        dataIndex: 'actionClass',
        key: 'actionClass',
        supportSearch: true,
    },
    {
        title: '方法',
        dataIndex: 'actionMethod',
        key: 'actionMethod',
        supportSearch: true,
    },
    {
        title: "IP地址",
        key: "actionIp",
        dataIndex: 'actionIp',
    },
    {
        title: "操作时间",
        key: "created",
        dataIndex: 'created',
        render: (value) => {
            return dateFormat(value, "YYYY-MM-DD HH:mm");
        }
    },
];


const Logs: React.FC<{ paramsToUrl: boolean }> = ({paramsToUrl}) => {
    return (
        <CrudPage columnsConfig={columns}
                  tableAlias="sysLog"
                  paramsToUrl={paramsToUrl}
                  addButtonEnable={false}
                  actionConfig={{
                      hidden: true
                  }}
        />
    )
};

export default {
    path: "/system/logs",
    element: Logs
};
