import React, {useEffect, useState} from 'react';
import {EditOutlined, EllipsisOutlined, MinusCircleOutlined, PlusOutlined, SettingOutlined} from '@ant-design/icons';
import {Avatar, Button, Card, Checkbox, Col, Form, FormProps, Input, Modal, Radio, Row, Select, Space} from 'antd';
import {usePage} from "../../hooks/useApis.ts";
import SearchForm from "../../components/AntdCrud/SearchForm.tsx";
import {ColumnsConfig} from "../../components/AntdCrud";
import {useBreadcrumbRightEl} from "../../hooks/useBreadcrumbRightEl.tsx";
import ImageUploader from "../../components/ImageUploader";
import TextArea from "antd/es/input/TextArea";
import {CheckboxGroupProps} from "antd/es/checkbox";

const actions: React.ReactNode[] = [
	<EditOutlined key="edit" />,
	<SettingOutlined key="setting" />,
	<EllipsisOutlined key="ellipsis" />,
];

const Plugin: React.FC = () => {
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
			title: 'Icon',
			dataIndex: 'icon',
			key: 'icon',
			form: {
				type: "image"
			}
		},
		{
			form: {
				type: "input",
				rules: [{required: true, message: '请输入插件名称'}]
			},
			dataIndex: "pluginName",
			title: "插件名称",
			key: "pluginName",
			supportSearch: true,
			placeholder: "请输入插件名称",

		},

		{
			form: {
				type: "TextArea",
				rules: [{required: true, message: '请输入插件描述'}],
				attrs: {
					rows: 3
				}
			},
			dataIndex: "pluginDesc",
			title: "插件描述",
			key: "pluginDesc"
		},

		{
			hidden: true,
			form: {
				type: "hidden"
			},
			dataIndex: "options",
			title: "插件配置",
			key: "options"
		},

		{
			form: {
				type: "select"
			},
			dataIndex: "status",
			title: "数据状态",
			key: "status",
			dict: {
				name: "dataStatus"
			}
		},
	];
	type FieldType = {
		icon?: string;
		name?: string;
		description	?: string;
		baseUrl?: string; // 基础地址
		headers?: string;
		authData?: string;
		authType?: string;
		position?: string;

	};

	const onFinish: FormProps<FieldType>['onFinish'] = (values) => {
		console.log('Success:', values);
	};
	const options: CheckboxGroupProps<string>['options'] = [
		{ label: 'headers', value: 'headers' },
		{ label: 'query', value: 'query' },
	];
	const onFinishFailed: FormProps<FieldType>['onFinishFailed'] = (errorInfo) => {
		console.log('Failed:', errorInfo);
	};
	// 控制新增插件模态框的显示与隐藏
	const [addPluginIsOpen, setAddPluginIsOpen] = useState(false)
	// 认证类型
	const [authType, setAuthType] = useState('none')
	// 认证参数位置
	const [positionValue, setPositionValue] = useState('headers')
	const {
		loading,
		result,
		doGet
	} = usePage('aiBotPlugin', {}, {manual: true})
	useBreadcrumbRightEl(<Button type={"primary"} onClick={() => setAddPluginIsOpen(true)}>
		<PlusOutlined/>新增插件</Button>)
	useEffect(() => {
		doGet({
			params: {
				pageNumber: 1,
				pageSize: 10,
			}
		}).then(res => {
			console.log(res.data.data)
		})
	}, [])

	// 创建表单实例
	const [form] = Form.useForm();
	const handleaddPluginCancle = () => {
		form.resetFields()
		setPositionValue('headers')
		setAuthType('none')
		setAddPluginIsOpen(false);
	};

	const handleaddPluginOk = () => {
		// setAddPluginIsOpen(false);
	};
	return (
		<>
			<SearchForm columns={columnsConfig} colSpan={6}
						onSearch={(values: any) => {
							// setLocalPageNumber(1)
							// setSearchParams(values)
							// setUrlParams(values)
						}}
						// onSearchValueInit={(key) => urlParams[key]}
			/>
			<Row className={"card-row"} gutter={[16, 16]}>
				{result?.data?.records?.length > 0 ? result?.data?.records?.map((item: any) => (
					<Col span={6}>
						<Card  actions={actions} style={{padding: 8}} >
							<Card.Meta
								avatar={<Avatar src={item.icon || "/favicon.png"} />}
								title={item.name}
								description={
									<>
										<p>{item.description}</p>
									</>
								}
							/>
						</Card>
					</Col>)

				) : (<div></div>)}

			</Row>
			<Modal title="新增插件" open={addPluginIsOpen} onOk={handleaddPluginOk}
				   onCancel={handleaddPluginCancle}
				   styles={{
					   body: { maxHeight: '500px', overflowY: 'auto' }, // 使用 styles 替代 bodyStyle
				   }}
				   footer={null}
			>
				<Form
					form={form}
					layout="vertical"
					name="basic"
					style={{ width: '100%'}}
					initialValues={{ authType: 'none', position: 'headers' }}
					onFinish={onFinish}
					onFinishFailed={onFinishFailed}
					autoComplete="off"
				>
						<Form.Item<FieldType>
							name="icon"
							style={{ textAlign: 'center'}}
						>
							<div style={{ display: 'flex', justifyContent: 'center' }}>
								{/* 使用 flex 布局确保 ImageUploader 居中 */}
								<ImageUploader />
							</div>
						</Form.Item>


					<Form.Item<FieldType>
						label="插件名称"
						name="name"
						rules={[{ required: true, message: '请输入插件名称!' }]}
					>
						<Input maxLength={30} showCount/>
					</Form.Item>
					<Form.Item<FieldType>
						label="插件描述"
						name="description"
						rules={[{ required: true, message: '请输入插件描述!' }]}
					>
						<TextArea
							showCount
							maxLength={500}
							placeholder="disable resize"
							style={{ height: 80, resize: 'none' }}
						/>
					</Form.Item>

					<Form.Item<FieldType>
						name="baseUrl"
						label={'插件URL'}
					  	rules={[{ required: true, message: '请输入插件URL' }]}
					>
						<Input />
					</Form.Item>
					<Form.Item<FieldType>
						name="headers"
						label={'Headers'}
					>
					<Form.List name="headers">
						{(fields, { add, remove }) => (
							<>
								{fields.map(({ key, name, ...restField }) => (
									<Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
										<Form.Item
											{...restField}
											name={[name, 'first']}
											rules={[{ required: true, message: 'Missing first name' }]}
										>
											<Input placeholder="Headers Name" />
										</Form.Item>
										<Form.Item
											{...restField}
											name={[name, 'last']}
											rules={[{ required: true, message: 'Missing last name' }]}
										>
											<Input placeholder="Headers value" />
										</Form.Item>
										<MinusCircleOutlined onClick={() => remove(name)} />
									</Space>
								))}
									<Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
										添加 headers
									</Button>
							</>
						)}
					</Form.List>
					</Form.Item>
					<Form.Item<FieldType>
						label="认证方式"
						name="authType"
						rules={[{ required: true, message: '请输入插件名称!' }]}
					>
						<Select
							onChange={(value) => {
								setAuthType(value)
							}}
							options={[{ value: 'none', label: '无需认证' }, { value: 'apiKey', label: 'serviceToken/apiKey' }]}
						/>
					</Form.Item>
					{authType === 'apiKey' ?
						<>
						<Form.Item<FieldType>
						name="position"
						label={'参数位置'}
						rules={[{ required: true, message: '请输入认证数据' }]}
						>
						<Radio.Group options={options} value={positionValue} onChange={(e) =>{
							setPositionValue(e.target.value)
						}} />

						</Form.Item>
						<Form.Item<FieldType>
							label="Parameter name"
							name="name"
							rules={[{ required: true, message: 'Parameter name!' }]}
						>
							<Input maxLength={100} showCount/>
						</Form.Item>
						<Form.Item<FieldType>
						label="Service token / API key"
						name="name"
						rules={[{ required: true, message: 'Service token / API key!' }]}>
					<Input maxLength={100} showCount/>
					</Form.Item>
						</>
						: <></> }


					<Form.Item label={null}>
						<Space style={{ display: 'flex', justifyContent: 'flex-end' }} >
							{/* 取消按钮 */}
							<Button onClick={handleaddPluginCancle}>取消</Button>
							{/* 确定按钮 */}
							<Button type="primary" htmlType="submit" style={{ marginRight: 8 }}>
								确定
							</Button>
						</Space>
					</Form.Item>
				</Form>
			</Modal>
		</>
	);
};

export default {
	path: "/ai/plugin",
	element: Plugin
};
