import React, {useState} from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
import {Button, DatePicker, Form,  message, Modal} from "antd";
import {useGetManual, usePostManual} from "../../hooks/useApis.ts";
import {EditOutlined} from "@ant-design/icons";


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
		dataIndex: "token",
		title: "token",
		editCondition: () => {
            return false
        },
		key: "token"
	},
	{
		form: {
			type: "datetimepicker"
		},
		dataIndex: "expireTime",
		title: "过期时间",
		key: "expireTime"
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "createdAt",
		title: "创建时间",
		key: "createdAt",
		editCondition: () => {
            return false
        },
	}
];

//编辑页面设置
const editLayout = {
	labelLayout: "horizontal",
	labelWidth: 80,
	columnsCount: 1,
	openType: "modal"
} as EditLayout;




export const SysToken: React.FC = () => {
	const {doGet: doGetGenerate} = useGetManual('/api/v1/sysToken/generateToken');
	const {doPost: doPostUpdate} = usePostManual('/api/v1/sysToken/updateToken')
	const [expireTime, setExpireTime] = useState<string>('')
	const [editId, setEditId] = useState<string>('')
	const [isEditModalOpen, setIsEditModalOpen] = useState<boolean>(false)
	const [form] = Form.useForm();
	const [refreshTrigger, setRefreshTrigger] = useState(0);


	const handleOk = () => {
		onFinish()
	};

	const handleCancel = () => {
		setIsEditModalOpen(false);
	};
	//操作列配置
	const actionConfig = {
		addButtonEnable: true,
		detailButtonEnable: false,
		deleteButtonEnable: true,
		editButtonEnable: false,
		hidden: false,
		width: "200px",
		customActions: (data: any) => {
			return (
				<>
					<a onClick={() => {
						setExpireTime(data.expireTime)
						setEditId(data.id)
						setIsEditModalOpen(true)
					}}> <EditOutlined/> 编辑 </a>
				</>
			)
		}

	} as ActionConfig<any>

	const onFinish = () =>{
		let value = JSON.parse(JSON.stringify(form.getFieldsValue()));
		form.resetFields()
		doPostUpdate({data: {
			id: editId,
			expireTime: value.expireTime
			}}).then((r) => {
			setEditId('')
			if (r.data.errorCode === 0){
				message.success('修改成功')
				setIsEditModalOpen(false);
			}
			})
	}
    return (
		<>
			<CrudPage columnsConfig={columnsConfig} tableAlias="sysToken"
					  addButtonEnable={false}
					  externalRefreshTrigger={refreshTrigger}
					  customButton={() => {
						  return <><Button type="primary" onClick={() => {
							  Modal.confirm({
								  title: '提示',
								  content: '该操作会生成一个 token , 请确认是否生成',
								  okText: '确定',
								  cancelText: '取消',
								  onOk() {
									  doGetGenerate({params: {expireDays: 7}}).then((res) => {
										  console.log('res');
										  console.log(res);
										  if (res.data.errorCode === 0) {
											  message.success("token生成成功");
											  setRefreshTrigger(prev => prev + 1);
										  }
									  })
								  },
							  });
						  }}>新增</Button></>
					  }}
					  actionConfig={actionConfig} editLayout={editLayout}/>
			<Modal
				title="编辑"
				closable={{ 'aria-label': 'Custom Close Button' }}
				open={isEditModalOpen}
				onOk={handleOk}
				onCancel={handleCancel}
			>
				<Form
					layout={'horizontal'}
					form={form}
					size = {'middle'}
					onFinish={onFinish}
				>
					<Form.Item label="失效时间&nbsp;&nbsp;&nbsp;&nbsp;" name="expireTime" rules={[{ required: true, message: '请输入失效时间' }]}>
						<DatePicker value={expireTime} showTime showNow format={'YYYY-MM-DD HH:mm:ss'}  />
					</Form.Item>
				</Form>
			</Modal>
		</>

    )
};

export default {
    path: "sys/sysToken",
    element:  SysToken
};
