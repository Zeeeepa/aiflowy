import React, {useState} from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
import {dateFormat} from "../../libs/utils.ts";
import {Button, message, Modal} from "antd";
import {useGetManual, usePostManual} from "../../hooks/useApis.ts";


//字段配置
const columnsConfig: ColumnsConfig<any> = [
	{
		hidden: true,
		form: {
			type: "hidden"
		},
		dataIndex: "id",
		key: "id"
	},
	{
		form: {
			type: "input"
		},
		dataIndex: "apiKey",
		title: "apiKey",
		key: "apiKey",
		supportSearch: true,
		editCondition: (data) => {
			return data?.id === undefined;
		}
	},
	{
		form: {
			type: "select"
		},
		dataIndex: "status",
		title: "状态",
		key: "status",
		supportSearch: true,

		dict: {
			name: "dataStatus"
		}
	},
	{
		form: {
			type: "hidden"
		},
		dataIndex: "created",
		render: (value) => {
			return <span>{dateFormat(value, "YYYY-MM-DD HH:mm:ss")}</span>
		},
		title: "创建时间",
		key: "created",
		editCondition: (data) => {
			return data?.id === undefined;
		}
	}
];
//编辑页面设置
const editLayout = {
	labelLayout: "horizontal",
	labelWidth: 80,
	columnsCount: 1,
	openType: "modal",
} as EditLayout;


//操作列配置
const actionConfig = {
    detailButtonEnable: false,
    deleteButtonEnable: true,
    editButtonEnable: true,
    hidden: false,
    width: "200px",
    
} as ActionConfig<any>

export const AiBotApiKey: React.FC = () => {
	const {doPost: useApiKeyPost} = usePostManual('/api/v1/aiBotApiKey/key/save');
	const {doGet: doPage} = useGetManual('/api/v1/aiBotApiKey/page');
	const [queryParam] = useState({
		pageNum: 1,
		pageSize: 10
	});
    return (
        <CrudPage columnsConfig={columnsConfig} tableAlias="aiBotApiKey" addButtonEnable={false}
				  customButton={() => {
					  return <><Button type="primary" onClick={() => {
							  Modal.confirm({
								  title: '提示',
								  content: '该操作会生成一个apiKey,请确认是否生成',
								  okText: '确定',
								  cancelText: '取消',
								  onOk() {
									  console.log('OK');
									  useApiKeyPost().then((res) => {
										  console.log('res');
										  console.log(res);
										  if (res.data.errorCode === 0) {
											  message.success("apiKey生成成功");
											  doPage({params: {
												  ...queryParam,
												  }}).then((res) => {
												  console.log('res');
												  console.log(res);
											  })
										  }
									  })
								  },
							  });
					  }}>新增</Button></>
        }}
            actionConfig={actionConfig} editLayout={editLayout}/>
    )
};

export default {
    path: "/ai/aiBotApiKey",
    element:  AiBotApiKey
};
