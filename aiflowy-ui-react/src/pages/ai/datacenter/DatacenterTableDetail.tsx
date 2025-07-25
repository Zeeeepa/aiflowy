import React, {useEffect, useState} from 'react'
import {useLocation} from "react-router-dom";
import {Button, Col, Menu, message, Popconfirm, Row, Space, Spin, Table, Tooltip} from "antd";
import {useLayout} from "../../../hooks/useLayout.tsx";
import css from './aitable.module.css'
import {
    EditOutlined,
    LeftOutlined,
    PlusOutlined,
    ReloadOutlined,
    RestOutlined,
} from "@ant-design/icons";
import tableIcon from '../../../assets/table2x.png'
import {useGetManual, usePostManual} from "../../../hooks/useApis.ts";
import {DataSave} from "./DataSave.tsx";

export const DatacenterTableDetail: React.FC = () => {
    const location = useLocation();
    const {tableId} = location.state || {};

    const {loading: detailLoading, doGet: getDetailInfo} = useGetManual('/api/v1/datacenterTable/detailInfo')
    const {loading: dataLoading, doGet: getTablePageData} = useGetManual('/api/v1/datacenterTable/getPageData')
    const {doGet: getHeaders} = useGetManual('/api/v1/datacenterTable/getHeaders')

    const [tableInfo, setTableInfo] = useState<any>(null)

    const [tableDataList, setTableDataList] = useState<any[]>([])

    const {loading: removeLoading, doPost: removeRecord} = usePostManual('/api/v1/datacenterTable/removeValue')

    useEffect(() => {
        if (tableId) {
            getDetailInfo({
                params: {
                    tableId
                }
            }).then(res => {
                setTableInfo(res.data.data)
                setFieldList(res.data.data.fields)
            })
            getHeaders({
                params: {
                    tableId
                }
            }).then(res => {
                const columns = res.data.data
                const dataCols: any[] = []
                for (const column of columns) {
                    const fieldType = column.fieldType
                    dataCols.push({
                        ...column,
                        render: (text:any) => {
                            if (fieldType === 3) {
                                return (
                                    <div>{text}</div>
                                )
                            }
                            if (fieldType === 5) {
                                return (
                                    <div>{1 ===  text ? "是" : "否"}</div>
                                )
                            }
                            return (
                                <div>{text}</div>
                            )
                        }
                    })
                }
                dataCols.push({
                    key:"action",
                    title: "操作",
                    render: (_: any, data: any) => {
                        return (
                            <Space size="middle">

                                <a onClick={() => {
                                    setCurrentData(data)
                                    setDataModal(true)
                                }}><EditOutlined/> 编辑 </a>

                                <Popconfirm
                                    title="确定删除？"
                                    description="您确定要删除这条数据吗？"
                                    okButtonProps={{loading: removeLoading}}
                                    onConfirm={async () => {
                                        await removeRecord({
                                            params: {
                                                tableId,
                                                id: data.id
                                            }
                                        })
                                        getTableData()
                                    }}
                                    okText="确定"
                                    cancelText="取消"
                                >
                                    <a style={{color: "#FF4D4D"}}> <RestOutlined/> 删除 </a>
                                </Popconfirm>
                            </Space>
                        )
                    }
                })
                setDataColumns(dataCols)
                getTableData()
            })
        } else {
            message.warning("tableId非法")
        }
    }, [])

    const pageInfo = {
        pageNumber: 1,
        pageSize: 10,
    }

    const [pageParams, setPageParams] = useState(pageInfo)
    const [total, setTotal] = useState(0)
    useEffect(() => {
        getTableData()
    },[pageParams])

    const getTableData = () => {
        getTablePageData({
            params: {
                tableId,
                ...pageParams
            }
        }).then(res => {
            setTableDataList(res.data.data.records)
            setTotal(res.data.data.totalRow)
        })
    }

    const items: any[] = [
        {key: '1', label: '表结构'},
        {key: '2', label: '数据'},
    ];

    const {setOptions} = useLayout();
    useEffect(() => {
        if (tableId) {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: [
                    {title: '首页'},
                    {title: 'AI能力'},
                    {title: '数据中枢'},
                    {title: '详情'},
                ]
            })
        }
        return () => {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: [],
                breadcrumbRightEl: null
            })
        }
    }, [tableId])

    const [activeKey, setActiveKey] = useState('1');
    const onClick = (e: any) => {
        setActiveKey(e.key)
    };

    const [fieldList, setFieldList] = useState<any[]>([])

    const fieldTypeConvert = {
        1: "String",
        2: "Integer",
        3: "Time",
        4: "Number",
        5: "Boolean",
    }

    const fieldColumns = [
        {
            dataIndex: "fieldName",
            title: "字段名称",
            key: "fieldName",
        },

        {
            dataIndex: "fieldDesc",
            title: "字段描述",
            key: "fieldDesc",
        },

        {
            dataIndex: "fieldType",
            title: "字段类型",
            key: "fieldType",
            render: (_: any, data: any) => {
                return fieldTypeConvert[data.fieldType as keyof typeof fieldTypeConvert] || '未知类型';
            }
        },

        {
            dataIndex: "required",
            title: "是否必填",
            key: "required",
            render: (t: any) => {
                return t === 1 ? "是" : "否"
            }
        },
    ]

    const [dataColumns, setDataColumns] = useState<any[]>([])

    const [currentData, setCurrentData] = useState<any>(null)
    const [dataModal, setDataModal] = useState(false)

    return (
        <div style={{padding: "24px"}}>
            <DataSave
                tableId={tableId}
                formItems={dataColumns}
                entity={currentData}
                open={dataModal}
                ok={() => {
                    message.success("保存成功")
                    setDataModal(false)
                    getTableData()
                }}
                cancel={() => {
                    setDataModal(false)
                }}

            />
            <Spin spinning={detailLoading}>
                <Row>
                    <Col span={24}>
                        <div className={css.detailHeader}>
                            <div className={css.iconContainer}>
                                <LeftOutlined className={css.backIcon} onClick={() => {
                                    history.back()
                                }}/>
                                <img src={tableIcon} className={css.iconStyle}/>
                                <div className={css.headerTableInfo}>
                                    <div className={css.tableName}>
                                        {tableInfo?.tableName}
                                    </div>
                                    <div className={css.tableDesc}>
                                        {tableInfo?.tableDesc}
                                    </div>
                                </div>
                            </div>
                            <div className={css.btnContainer}>
                                <Space>
                                    {activeKey === '2' && <Button type={"primary"} onClick={() => {
                                        setDataModal(true)
                                        setCurrentData(null)
                                    }}><PlusOutlined/>增加行</Button>}
                                    {/*{activeKey === '2' && <Button type={"primary"}><ToTopOutlined/>批量导入</Button>}*/}
                                </Space>
                            </div>
                        </div>
                    </Col>
                    <Col span={6} className={css.menuContainer}>
                        <Menu
                            style={{borderInlineEnd: 'none'}}
                            onClick={onClick}
                            defaultSelectedKeys={['1']}
                            mode="inline"
                            items={items}
                        />
                    </Col>
                    <Col span={18}>
                        {activeKey === '1' &&
                            <div className={css.tableContainer}>
                                <Table
                                    rowKey={"id"}
                                    dataSource={fieldList}
                                    columns={fieldColumns}
                                    pagination={false}
                                />
                            </div>
                        }
                        {activeKey === '2' &&
                            <div className={css.tableContainer}>
                                <div style={{
                                    display: "flex",
                                    justifyContent: "flex-end",
                                    marginBottom: "10px",
                                    marginRight: "5px",
                                    marginTop: "-10px"
                                }}>
                                    <Space align={"center"} size={"middle"}>
                                        <Tooltip placement="top" title="刷新">
                                            <ReloadOutlined onClick={() => {
                                                getTableData()
                                            }}/>
                                        </Tooltip>
                                    </Space>
                                </div>
                                <Table
                                    loading={dataLoading}
                                    rowKey={"id"}
                                    dataSource={tableDataList}
                                    columns={dataColumns}
                                    onChange={(pagination, _) => {
                                        setPageParams({
                                            pageNumber: pagination.current || pageInfo.pageNumber,
                                            pageSize: pagination.pageSize || pageInfo.pageSize,
                                        })
                                    }}
                                    pagination={
                                        {
                                            position: ["bottomCenter"],
                                            pageSize: pageParams.pageSize,
                                            showQuickJumper: true,
                                            current: pageParams.pageNumber,
                                            total: total || 0,
                                            showTotal: (total) => `共 ${total} 条数据`,
                                        }
                                    }
                                />
                            </div>
                        }
                    </Col>
                </Row>
            </Spin>
        </div>
    )
};

export default {
    path: "/datacenter/tableDetail",
    element: DatacenterTableDetail
};