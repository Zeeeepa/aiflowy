import React, {useEffect, useRef, useState} from 'react';
import {
    Button,
    Col,
    Form,
    Input, message,
    Modal, Pagination, Popconfirm,
    Row, Select,
    Space,
    Table,
    theme,

} from "antd";
import {
    EditOutlined, EyeOutlined,
    PlusOutlined, RestOutlined,
} from "@ant-design/icons";
import {useCheckPermission} from "../../../hooks/usePermissions.tsx";
import css from './aitable.module.css'

import {useGetManual, usePostManual} from "../../../hooks/useApis.ts";
import {ColumnsConfig} from "../../../components/AntdCrud";
import {DictSelect} from "../../../components/DictSelect";
import {useNavigate} from "react-router-dom";
import tableIcon from '../../../assets/table2x.png'

export const DatacenterTable: React.FC = () => {

    const hasQueryPermission = useCheckPermission(`/api/v1/datacenterTable/query`)
    const hasSavePermission = useCheckPermission(`/api/v1/datacenterTable/save`)
    const hasRemovePermission = useCheckPermission(`/api/v1/datacenterTable/remove`)

    const {token} = theme.useToken();
    const formStyle = {
        maxWidth: 'none',
        background: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        padding: 24,
        marginBottom: '20px'
    };
    const pageInfoInit = {
        pageNumber: 1,
        pageSize: 10,
        total: 0
    }
    const [searchForm] = Form.useForm()
    const [pageInfo, setPageInfo] = useState(pageInfoInit)
    const [queryParams, setQueryParams] = useState<any>({})

    useEffect(() => {
        getList();
    }, [pageInfo.pageNumber, pageInfo.pageSize, queryParams])

    const getList = async () => {
        const res = await doGet({
            params: {
                pageNumber: pageInfo.pageNumber,
                pageSize: pageInfo.pageSize,
                ...queryParams
            }
        });

        setTableData(res.data.data.records);
        setPageInfo(prev => ({
            ...prev,
            total: res.data.data.totalRow
        }));
    }

    const [modalTitle, setModalTitle] = useState("新增")
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [fieldModal, setFieldModal] = useState(false);

    const [saveForm] = Form.useForm();

    const tableModalCloes = () => {
        setIsModalOpen(false)
        setFieldModal(false)
        saveForm.resetFields()
        setFieldList([])
        setRemoveFields([])
    }

    const tableRef = useRef<any>(null);
    const {loading, doGet} = useGetManual('/api/v1/datacenterTable/page')
    const {loading: removeLoading, doGet: doRemove} = useGetManual('/api/v1/datacenterTable/removeTable');
    const {loading: saveTableLoading, doPost: doSaveTable} = usePostManual('/api/v1/datacenterTable/saveTable')
    const {loading: detailLoading, doGet: doDetailInfo} = useGetManual('/api/v1/datacenterTable/detailInfo')

    const [tableData, setTableData] = useState<any[]>([])
    const navigate = useNavigate();
    const columnsConfig: ColumnsConfig<any> = [
        {
            hidden: true,
            dataIndex: "id",
            key: "id"
        },

        {
            dataIndex: "tableName",
            title: "数据表名称",
            key: "tableName",
            render: (text: any) => {
                return (
                    <>
                        <img src={tableIcon} style={{width: "32px",height: "32px", marginRight: "10px"}} /> {text}
                    </>
                )
            }
        },

        {
            dataIndex: "tableDesc",
            title: "数据表描述",
            key: "tableDesc",
        },

        {
            dataIndex: "created",
            title: "创建时间",
            key: "created"
        },

        {
            width: "250px",
            title: "操作",
            key: "action",
            render: (_, data) => {
                return (
                    <Space size="middle">

                        {hasQueryPermission && <a onClick={() => {
                            navigate('/datacenter/tableDetail', {
                                state: {
                                    tableId: data.id,
                                }
                            })
                        }}> <EyeOutlined/> 查看 </a>}

                        {hasSavePermission && <a onClick={() => {
                            doDetailInfo({
                                params: {
                                    tableId: data.id
                                }
                            }).then(res => {
                                saveForm.setFieldsValue(res.data.data)
                                setFieldList(res.data.data.fields)
                            })
                            setModalTitle("编辑")
                            setIsModalOpen(true)
                        }}> <EditOutlined/> 编辑 </a>}

                        {hasRemovePermission && <Popconfirm
                            title="确定删除？"
                            description="您确定要删除这条数据吗？"
                            okButtonProps={{loading: removeLoading}}
                            onConfirm={async () => {
                                await doRemove({
                                    params: {
                                        tableId: data.id
                                    }
                                })
                                getList()
                            }}
                            okText="确定"
                            cancelText="取消"
                        >
                            <a style={{color: "#FF4D4D"}}> <RestOutlined/> 删除 </a>
                        </Popconfirm>}
                    </Space>
                )
            }
        }
    ];

    const [fieldList, setFieldList] = useState<any[]>([])
    const [removeFields, setRemoveFields] = useState<any[]>([])

    const fieldColumns = [
        {
            dataIndex: "fieldName",
            title: "字段名称",
            key: "fieldName",
            render: (_: any, data: any, index: number) => {
                return (
                    <Input value={data.fieldName} onChange={(e) => {
                        const value = e.target.value
                        const newData = [...fieldList];
                        newData[index].fieldName = value;
                        setFieldList(newData);
                    }}/>
                )
            }
        },

        {
            dataIndex: "fieldDesc",
            title: "字段描述",
            key: "fieldDesc",
            render: (_: any, data: any, index: number) => {
                return (
                    <Input value={data.fieldDesc} onChange={(e) => {
                        const value = e.target.value
                        const newData = [...fieldList];
                        newData[index].fieldDesc = value;
                        setFieldList(newData);
                    }}/>
                )
            }
        },

        {
            dataIndex: "fieldType",
            title: "字段类型",
            key: "fieldType",
            render: (_: any, data: any, index: number) => {
                return (
                    <DictSelect disabled={data.id} code={"fieldType"} initvalue={data.fieldType} onChange={(value) => {
                        const newData = [...fieldList];
                        newData[index].fieldType = value;
                        setFieldList(newData);
                    }}/>
                )
            }
        },

        {
            dataIndex: "required",
            title: "是否必填",
            key: "required",
            render: (_: any, data: any, index: number) => {
                return (
                    <Select
                        value={data.required}
                        onChange={(value) => {
                            const newData = [...fieldList];
                            newData[index].required = value;
                            setFieldList(newData);
                        }}
                    >
                        <Select.Option value={1}>是</Select.Option>
                        <Select.Option value={0}>否</Select.Option>
                    </Select>
                )
            }
        },

        {
            width: "100px",
            title: "操作",
            key: "action",
            render: (_: any, data: any, index: number) => {
                return (
                    <Space size="middle">
                        <Popconfirm
                            title="确定删除？"
                            description="您确定要删除这条数据吗？"
                            okButtonProps={{loading: removeLoading}}
                            onConfirm={() => {
                                const newData = [...fieldList];
                                const filteredData = newData.filter((_, i) => i !== index);
                                if (data.id) {
                                    const item = {
                                        ...data,
                                        handleDelete: true,
                                    }
                                    setRemoveFields([...removeFields, item])
                                }
                                setFieldList(filteredData);
                            }}
                            okText="确定"
                            cancelText="取消"
                        >
                            <a style={{color: "#FF4D4D"}}> <RestOutlined/> 删除 </a>
                        </Popconfirm>
                    </Space>
                )
            }
        }
    ]

    return (
        <div className={css.tablePageContainer}>
            <Modal
                loading={detailLoading}
                width={"60%"}
                title={modalTitle}
                open={isModalOpen}
                onCancel={() => {
                    tableModalCloes()
                }}
                footer={<>
                    <Button onClick={() => {
                        tableModalCloes()
                    }}>取消</Button>
                    <Button type={"primary"} onClick={() => {
                        saveForm.validateFields().then(() => {
                            setIsModalOpen(false)
                            setFieldModal(true)
                        }).catch(() => {
                        })

                    }}>下一步</Button>
                </>}
            >
                <Form
                    form={saveForm}
                    name="saveForm"
                    labelCol={{span: 6}}
                    wrapperCol={{span: 16}}
                    style={formStyle}
                    autoComplete="off"
                >
                    <Form.Item
                        hidden={true}
                        label="id"
                        name="id"
                    >
                        <Input/>
                    </Form.Item>
                    <Form.Item
                        label="数据表名称"
                        name="tableName"
                        rules={[
                            {required: true, message: '请输入数据表名称'},
                            {
                                pattern: /^[a-z][a-z0-9_]*$/,
                                message: '数据库名称只能包含小写字母、数字和下划线，且必须以小写字母开头',
                            },
                        ]}
                    >
                        <Input disabled={modalTitle === "编辑"}/>
                    </Form.Item>
                    <Form.Item
                        label="数据表描述"
                        name="tableDesc"
                    >
                        <Input.TextArea/>
                    </Form.Item>
                </Form>
            </Modal>
            <Modal
                width={"60%"}
                title={"编辑表结构"}
                open={fieldModal}
                onCancel={() => {
                    tableModalCloes()
                }}
                footer={<>
                    <Button onClick={() => {
                        setFieldModal(false)
                        setIsModalOpen(true)
                    }}>上一步</Button>
                    <Button
                        loading={saveTableLoading}
                        type={"primary"}
                        onClick={() => {
                            const body = {
                                ...saveForm.getFieldsValue(),
                                fields: [
                                    ...fieldList,
                                    ...removeFields
                                ]
                            }
                            doSaveTable({
                                data: body
                            }).then(res => {
                                if (res.data.errorCode === 0) {
                                    message.success("保存成功")
                                    tableModalCloes()
                                    getList()
                                } else {
                                    message.error("保存失败")
                                }
                            })
                        }}>完成</Button>
                </>}
            >
                <Table
                    rowKey={"rowKey"}
                    dataSource={fieldList}
                    columns={fieldColumns}
                    pagination={false}
                />
                <br/>
                <Button
                    color="primary"
                    variant={"filled"}
                    onClick={() => {
                        setFieldList([...fieldList, {
                            fieldName: "",
                            fieldDesc: "",
                            fieldType: 1,
                            required: 0,
                            handleDelete: false,
                            rowKey: new Date().getTime().toString()
                        }])
                    }}
                ><PlusOutlined/>新增</Button>
            </Modal>
            <div style={{marginTop: "8px", marginBottom: "24px"}}>
                <Form
                    form={searchForm}
                    layout={'inline'}
                    onFinish={(values) => {
                        setQueryParams(values)
                    }}
                >
                    <Row style={{width: "calc(100% - 150px)"}}>
                        <Col span={6}>
                            <Form.Item
                                name={'tableName'}
                            >
                                <Input placeholder={"数据表名称"} allowClear/>
                            </Form.Item>
                        </Col>
                        <Col span={6}>
                            <Form.Item>
                                <Space>
                                    <Button type={"primary"} htmlType={"submit"}>搜索</Button>
                                    <Button onClick={() => {
                                        searchForm.resetFields()
                                        setQueryParams({})
                                        setPageInfo(pageInfoInit)
                                    }}>重置</Button>
                                </Space>
                            </Form.Item>
                        </Col>
                    </Row>
                    <div style={{display: "flex", justifyContent: "end", flex: "1"}}>
                        {hasSavePermission && <Button type="primary" onClick={() => {
                            setModalTitle("新增")
                            setIsModalOpen(true)
                        }}><PlusOutlined/>新建数据表</Button>}
                    </div>
                </Form>
            </div>
            <div
                ref={tableRef}
            >
                <Table
                    rowKey='id'
                    pagination={false}
                    loading={loading}
                    columns={columnsConfig}
                    dataSource={tableData}
                />
            </div>
            <div
                style={{display: "flex", justifyContent: "center", marginTop: "8px"}}
            >
                <Pagination
                    onChange={(page, pageSize) => {
                        setPageInfo(prev => ({
                            ...prev,
                            pageNumber: page,
                            pageSize: pageSize
                        }));
                    }}
                    pageSize={pageInfo.pageSize}
                    current={pageInfo.pageNumber}
                    total={pageInfo.total}
                    showTotal={(total) => `共 ${total} 条数据`}
                />
            </div>
        </div>
    )
};

export default {
    path: "/datacenter/table",
    element: DatacenterTable
};
