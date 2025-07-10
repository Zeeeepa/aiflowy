import React, {useEffect, useRef, useState} from 'react';
import {
    Button,
    Checkbox,
    Col,
    Dropdown, Empty,
    Form, Image,
    Input,
    MenuProps,
    message, Modal,
    Pagination, Popconfirm,
    Row,
    Space, Spin,
    Table, Tag, theme,
    Tooltip
} from "antd";
import {
    AppstoreOutlined, ColumnHeightOutlined, DeleteOutlined,
    DownloadOutlined, EditOutlined, EyeOutlined, FormatPainterOutlined,
    PlusOutlined,
    ReloadOutlined, RestOutlined,
    UnorderedListOutlined
} from "@ant-design/icons";
import {DictSelect} from "../../../components/DictSelect";
import './ai-resource.css'
import {useReactToPrint} from "react-to-print";
import {ColumnsConfig} from "../../../components/AntdCrud";
import {CardItem} from "./CardItem.tsx";
import {useGetManual, useRemove, useRemoveBatch, useSave, useUpdate} from "../../../hooks/useApis.ts";
import {useCheckPermission} from "../../../hooks/usePermissions.tsx";
import {TableRowSelection} from "antd/es/table/interface";
import FileUploader from "../../../components/FileUploader";
import videoIcon from '../../../assets/video-icon.png'
import audioIcon from '../../../assets/audio-icon.png'
import docIcon from '../../../assets/doc-icon.png'
import otherIcon from '../../../assets/other-icon.png'

export const AiResourceNew: React.FC = () => {

    const hasSavePermission = useCheckPermission(`/api/v1/aiResource/save`)
    const hasRemovePermission = useCheckPermission(`/api/v1/aiResource/remove`)

    const [modalTitle, setModalTitle] = useState("新增")
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalRow, setModalRow] = useState<any>(null);

    const getSrc = (item: any) => {
        let res;
        switch (item.resourceType) {
            case 0:
                res = item.resourceUrl;
                break;
            case 1:
                res = videoIcon
                break;
            case 2:
                res = audioIcon
                break;
            case 3:
                res = docIcon
                break;
            default:
                res = otherIcon
        }
        return res;
    }

    const columnsConfig: ColumnsConfig<any> = [
        {
            hidden: true,
            dataIndex: "id",
            key: "id"
        },

        {
            dataIndex: "resourceName",
            title: "素材名称",
            key: "resourceName",
            render: (_, item: any) => {
                return (
                    <>
                        <div style={{width: "100%"}}>
                            <div style={{display: "inline-block", lineHeight: "32px"}}>
                                <img src={getSrc(item)} alt="" style={{
                                    width: "32px",
                                    height: "32px",
                                    marginRight: "24px",
                                }}/>
                                {/*<div style={{display: "inline-block"}}>{item.resourceName}</div>*/}
                            </div>
                            <Tooltip title={item.resourceName}>
                                <div style={{
                                    whiteSpace: "nowrap",
                                    overflow: "hidden",
                                    textOverflow: "ellipsis",
                                    maxWidth: "260px",
                                    display: "inline-block",
                                }}>{item.resourceName}.{item.suffix}</div>
                            </Tooltip>
                        </div>
                    </>
                )
            }
        },

        {
            dataIndex: "origin",
            title: "素材来源",
            key: "origin",
            render: (_, item: any) => {
                let res = null;
                switch (item.origin) {
                    case 0:
                        res = <Tag color="#E6FEFC"><span style={{color: "#00B8A9"}}>系统上传</span></Tag>
                        break;
                    case 1:
                        res = <Tag color="#E6F0FF"><span style={{color: "#0066FF"}}>工作流生成</span></Tag>
                        break;
                    default:
                        res = <Tag>未知</Tag>
                        break;
                }
                return res;
            }
        },

        {
            dataIndex: "resourceType",
            title: "素材类型",
            key: "resourceType",
            render: (_, item: any) => {
                let res = null;
                switch (item.resourceType) {
                    case 0:
                        res = <Tag color="#E6F0FF"><span style={{color: "#0066FF"}}>图片</span></Tag>
                        break;
                    case 1:
                        res = <Tag color="#FFEED1"><span style={{color: "#FFA200"}}>视频</span></Tag>
                        break;
                    case 2:
                        res = <Tag color="#F1EBFF"><span style={{color: "#5600FF"}}>音频</span></Tag>
                        break;
                    case 3:
                        res = <Tag color="#E6F9FF"><span style={{color: "#0099CC"}}>文档</span></Tag>
                        break;
                    default:
                        res = <Tag color="#F6F6F6"><span style={{color: "#757575"}}>其他</span></Tag>
                        break;
                }
                return res;
            }
        },

        // {
        //     dataIndex: "suffix",
        //     title: "后缀",
        //     key: "suffix"
        // },

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

                        <a onClick={() => {
                            // 图片、视频、音频可以预览
                            const arr = [0, 1, 2]
                            if (!arr.includes(data.resourceType)) {
                                message.warning("暂不支持该素材类型预览")
                                return
                            }
                            setPreviewOpen(true)
                            setCurrentRecord(data)
                        }}><EyeOutlined/> 预览 </a>

                        <a href={data.resourceUrl} target={"_blank"}>
                            <DownloadOutlined/>下载
                        </a>

                        {hasSavePermission && <a onClick={() => {
                            setModalRow(data)
                            setModalTitle("编辑")
                            setIsModalOpen(true)
                        }}> <EditOutlined/> 编辑 </a>}


                        {hasRemovePermission && <Popconfirm
                            title="确定删除？"
                            description="您确定要删除这条数据吗？"
                            okButtonProps={{loading: removeLoading}}
                            onConfirm={async () => {
                                await doRemove({
                                    data: {
                                        id: data.id
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

    const [previewOpen, setPreviewOpen] = useState(false);
    const [currentRecord, setCurrentRecord] = useState<any>(null);

    const getPreview = (record: any) => {
        if (record?.resourceType == 0) {
            return <Image width={200} src={record.resourceUrl}/>
        }
        if (record?.resourceType == 1) {
            return (
                <video
                    controls
                    width="640"
                    height="360"
                >
                    <source src={record.resourceUrl} type="video/mp4"/>
                    您的浏览器不支持 video 元素。
                </video>
            )
        }
        if (record?.resourceType == 2) {
            return (
                <audio controls src={record.resourceUrl}>
                    您的浏览器不支持 audio 元素。
                </audio>
            )
        }
        return null
    }

    const {loading, doGet} = useGetManual('/api/v1/aiResource/page')
    const {loading: saveLoading, doSave} = useSave('aiResource', null, {manual: true});
    const {loading: removeLoading, doRemove} = useRemove('aiResource', null, {manual: true});
    const {loading: removeBatchLoading, doRemoveBatch} = useRemoveBatch('aiResource', null, {manual: true});
    const {loading: updateLoading, doUpdate} = useUpdate('aiResource', null, {manual: true});

    const selectNone = () => {
        setSelectedRowKeys([]);
        setSelectedRows([]);
        setSelectCount(0);
    };

    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [selectCount, setSelectCount] = useState(0);
    const [tableData, setTableData] = useState<any[]>([])

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

    const [searchForm] = Form.useForm()

    const [activeMode, setActiveMode] = useState('list')

    const changeMode = (mode: "list" | "card" | "both") => {
        if (mode !== activeMode) {
            setTransitionClass('view-exit view-exit-active');
            setTimeout(() => {
                setActiveMode(mode);
                setTransitionClass('view-enter');
                setTimeout(() => setTransitionClass('view-enter-active'), 10);
            }, 100);
            setSelectedRows([]);
            setSelectedRowKeys([]);
            setSelectCount(0);
            setSelectedIds([])
        }
    }

    const [tableSize, setTableSize] = useState<"small" | "middle" | "large">("middle");

    const items: MenuProps['items'] = [
        {
            key: '1',
            label: '宽松',
            onClick: () => {
                setTableSize("large")
            }
        },
        {
            key: '2',
            label: '一般',
            onClick: () => {
                setTableSize("middle")
            }
        },
        {
            key: '3',
            label: '紧凑',
            onClick: () => {
                setTableSize("small")
            }
        },
    ];

    const tableRef = useRef<any>(null);

    const pageInfoInit = {
        pageNumber: 1,
        pageSize: 8,
        total: 0
    }

    const [transitionClass, setTransitionClass] = useState('');

    const [pageInfo, setPageInfo] = useState(pageInfoInit)
    const [queryParams, setQueryParams] = useState<any>({})

    useEffect(() => {
        getList();
    }, [pageInfo.pageNumber, pageInfo.pageSize, queryParams])

    const handlePrint = useReactToPrint({
        documentTitle: "表格打印",
        contentRef: tableRef,
        pageStyle: `@page {padding-top:10px;} @media print {body { font-family: "SimSun", "宋体", serif;}}`,
    });

    function download(columns: ColumnsConfig<any>, dataSource: any[]) {
        let cvs = '';
        columns.map((column) => {
            cvs += `${column.title}, `;
        })
        cvs += '\r\n'

        dataSource.map((data) => {
            columns.map((column) => {
                cvs += `${data[column.key as string]}, `;
            })
            cvs += '\r\n'
        });

        const _utf = "\uFEFF"; // 为了使Excel以utf-8的编码模式，同时也是解决中文乱码的问题
        const url = 'data:application/csv;charset=utf-8,' + _utf + encodeURIComponent(cvs);
        const link = document.createElement("a");
        link.href = url;
        link.style.cssText = "visibility:hidden";
        link.download = "data.csv";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    const [saveForm] = Form.useForm();
    const {token} = theme.useToken();
    const formStyle = {
        maxWidth: 'none',
        background: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        padding: 24,
        marginBottom: '20px'
    };

    useEffect(() => {
        console.log(modalRow)
        if (modalRow) {

            saveForm.setFieldsValue(modalRow)
        }
    }, [modalRow])

    const handleSave = () => {
        saveForm.validateFields().then((values) => {
            if (values.id) {
                doUpdate({data: values}).then(res => {
                    if (res.data.errorCode === 0) {
                        message.success("成功")
                        setIsModalOpen(false)
                        saveForm.resetFields()
                        getList()
                    }
                })
            } else {
                doSave({data: values}).then(res => {
                    if (res.data.errorCode === 0) {
                        message.success("成功")
                        setIsModalOpen(false)
                        saveForm.resetFields()
                        getList()
                    }
                })
            }

        }).catch(info => {
            console.log('Validate Failed:', info);
        });
    };

    const [selectedIds, setSelectedIds] = useState<any>([]);

    // 计算是否全选和部分选择状态
    const indeterminate = selectedIds.length > 0 && selectedIds.length < tableData.length;
    const checkAll = selectedIds.length === tableData.length && tableData.length > 0;
    const toggleItemSelection = (id: any, checked: any) => {
        if (checked) {
            setSelectedIds([...selectedIds, id]);
        } else {
            setSelectedIds(selectedIds.filter((itemId: any) => itemId !== id));
        }
    };

    const toggleSelectAll = (checked: any) => {
        setSelectedIds([]);
        setHoverStates({});
        if (checked) {
            setSelectedIds(tableData.map(item => item.id));
            setHoverStates(tableData.reduce((acc, item) => {
                acc[item.id] = true;
                return acc;
            }, {} as Record<string, boolean>));
        }
    };

    const [hoverStates, setHoverStates] = useState<Record<string, boolean>>({});
    const handleMouseEnter = (id: string) => {
        setHoverStates(prev => ({...prev, [id]: true}));
    };

    const handleMouseLeave = (id: string) => {
        if (!selectedIds.includes(id)) {
            setHoverStates(prev => ({...prev, [id]: false}));
        }
    };


    return (
        <div className={"page-container"}>
            <Modal
                title={modalTitle}
                open={isModalOpen}
                onCancel={() => {
                    setIsModalOpen(false)
                    saveForm.resetFields()
                    setModalRow(null)
                }}
                destroyOnClose={true}
                onOk={handleSave}
                confirmLoading={saveLoading || updateLoading}
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
                        label="素材类型"
                        name="resourceType"
                        rules={[{required: true, message: '请选择素材类型'}]}
                    >
                        <DictSelect
                            style={{width: '100%'}}
                            code={"resourceType"}
                            initvalue={modalRow?.resourceType}
                            onChange={(v) => {
                                setModalRow({...modalRow, resourceType: v})
                            }}
                        />
                    </Form.Item>
                    <Form.Item
                        label="素材名称"
                        name="resourceName"
                        rules={[{required: true, message: '请输入素材名称'}]}
                    >
                        <Input/>
                    </Form.Item>
                    <Form.Item
                        label="url"
                        name="resourceUrl"
                        rules={[{required: true, message: '请上传素材文件'}]}
                    >
                        <FileUploader/>
                    </Form.Item>
                </Form>
            </Modal>
            <Modal
                title={"预览"}
                width={"60%"}
                open={previewOpen}
                footer={null}
                onCancel={() => {
                    setPreviewOpen(false)
                }}
                destroyOnClose={true}
            >
                <div style={{textAlign: "center"}}>
                    {getPreview(currentRecord)}
                </div>
            </Modal>
            <div style={{marginTop: "8px", marginBottom: "10px"}}>
                <Form
                    form={searchForm}
                    layout={'inline'}
                    onFinish={(values) => {
                        setQueryParams(values)
                    }}
                >
                    <Row style={{width: "calc(100% - 100px)"}}>
                        <Col span={6}>
                            <Form.Item
                                className={"query-form-item"}
                                name={'resourceType'}
                            >
                                <DictSelect
                                    style={{width: '100%'}}
                                    code={'resourceType'}
                                    placeholder={"素材类型"}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={6}>
                            <Form.Item
                                className={"query-form-item"}
                                name={'resourceName'}
                            >
                                <Input placeholder={"素材名称"} allowClear/>
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
                            setModalRow(null);
                            setModalTitle("新增")
                            setIsModalOpen(true)
                        }}><PlusOutlined/>新增</Button>}
                    </div>
                </Form>
            </div>
            <div style={{display: "flex", justifyContent: "space-between", marginBottom: "8px"}}>
                {activeMode === 'card' && <div style={{marginLeft: "16px"}}>
                    <Checkbox
                        indeterminate={indeterminate}
                        checked={checkAll}
                        onChange={(e) =>
                            toggleSelectAll(e.target.checked)
                        }>全选</Checkbox>
                </div>}
                <div>
                    {selectCount > 0 && hasRemovePermission &&
                        <div style={{
                            border: "1px solid #abdcff",
                            borderRadius: "6px",
                            padding: "0 10px",
                            background: "#f0faff",
                            height: "32px",
                            display: "flex",
                            fontSize: "14px"
                        }}>
                            <Space>
                                <div>
                                    已选择 {selectCount} 项
                                </div>
                                <Popconfirm
                                    title="确定删除？"
                                    description="您确定要全部删除所选的数据吗？"
                                    okButtonProps={{loading: removeBatchLoading}}
                                    onConfirm={async () => {
                                        await doRemoveBatch({
                                            data: {
                                                ids: selectedRows.map(item => item.id)
                                            }
                                        })
                                        setSelectedRows([]);
                                        setSelectedRowKeys([]);
                                        setSelectCount(0);
                                        getList()
                                    }}
                                    okText="确定删除"
                                    cancelText="取消"
                                >
                                    <a style={{color: "red"}}>
                                        <DeleteOutlined/>全部删除
                                    </a>

                                </Popconfirm>

                                <a style={{paddingLeft: "20px"}} onClick={selectNone}> 取消选择 </a>
                            </Space>
                        </div>
                    }
                </div>
                <div style={{display: "flex", marginBottom: "8px"}}>
                    {activeMode === 'list' && <div className={"tools-div"}>
                        <Space align={"center"} size={"middle"}>
                            <Tooltip placement="top" title="刷新">
                                <ReloadOutlined onClick={() => {
                                    getList()
                                }}/>
                            </Tooltip>

                            <Tooltip placement="top" title="导出">
                                <DownloadOutlined onClick={() => {
                                    download(columnsConfig, tableData);
                                }}/>
                            </Tooltip>

                            <Tooltip placement="top" title="行高">
                                <div>
                                    <Dropdown menu={{items}} placement="bottom" trigger={["click"]} arrow>
                                        <ColumnHeightOutlined/>
                                    </Dropdown>
                                </div>

                            </Tooltip>

                            <Tooltip placement="top" title="打印">
                                <FormatPainterOutlined onClick={() => {
                                    message.success("打印数据准备中...");
                                    handlePrint()
                                }}/>
                            </Tooltip>
                        </Space>
                    </div>}
                    {activeMode === 'card' && selectedIds.length > 0 && <div className={"tools-div"}>
                        <div className={"card-handle-div"}>
                            <Space align={"center"} size={"middle"}>

                                <a
                                    className={"card-handle-btn"}
                                    onClick={async () => {
                                        if (selectedIds.length === 0) {
                                            message.warning("请选择要下载的内容！")
                                            return
                                        }
                                        const urls = []
                                        for (const row of tableData) {
                                            if (selectedIds.includes(row.id)) {
                                                urls.push(row.resourceUrl)
                                            }
                                        }
                                        urls.forEach(url => {
                                            const a = document.createElement('a');
                                            a.href = url;
                                            a.target = '_blank';
                                            a.download = url.split('/').pop();
                                            document.body.appendChild(a);
                                            a.click();
                                            document.body.removeChild(a);
                                        });
                                    }}
                                >
                                    <DownloadOutlined/>下载
                                </a>

                                {hasSavePermission && <a className={"card-handle-btn"} onClick={() => {
                                    if (selectedIds.length === 0 || selectedIds.length > 1) {
                                        message.warning("只能编辑单条数据！")
                                        return
                                    }
                                    setModalRow(tableData.find(item => item.id === selectedIds[0]))
                                    setModalTitle("编辑")
                                    setIsModalOpen(true)
                                }}> <EditOutlined/> 编辑 </a>}


                                {hasRemovePermission && <Popconfirm
                                    title="确定删除？"
                                    description="您确定要删除数据吗？"
                                    okButtonProps={{loading: removeBatchLoading}}
                                    onConfirm={async () => {
                                        if (selectedIds.length === 0) {
                                            message.warning("请选择要删除的数据！")
                                            return
                                        }
                                        await doRemoveBatch({
                                            data: {
                                                ids: selectedIds
                                            }
                                        })
                                        setSelectedIds([]);
                                        getList()
                                    }}
                                    okText="确定"
                                    cancelText="取消"
                                >
                                    <a className={"card-handle-btn"}> <RestOutlined/> 删除 </a>
                                </Popconfirm>}
                            </Space>
                        </div>
                    </div>}
                    <div className={"mode-div-container"}>
                        <div className={"mode-div"} onClick={() => {
                            changeMode("list")
                        }}>
                            <UnorderedListOutlined
                                className={"mode-icon " + `${activeMode === "list" ? "active-icon" : ""}`}/>
                        </div>
                        <div className={"mode-div"} onClick={() => {
                            changeMode("card")
                        }}>
                            <AppstoreOutlined
                                className={"mode-icon card-icon " + `${activeMode === "card" ? "active-icon" : ""}`}/>
                        </div>
                    </div>
                </div>

            </div>

            {activeMode === "list" &&
                <div
                    ref={tableRef}
                    className={`view-container ${transitionClass}`}
                >
                    <Table
                        rowKey='id'
                        pagination={false}
                        loading={loading}
                        columns={columnsConfig}
                        dataSource={tableData}
                        size={tableSize}
                        rowSelection={{
                            type: 'checkbox',
                            selectedRowKeys,
                            onChange: (selectedRowKeys: React.Key[], selectedRows: any[]) => {
                                setSelectedRows(selectedRows);
                                setSelectedRowKeys([...selectedRowKeys]);
                                setSelectCount(selectedRows.length);
                            }
                        } as TableRowSelection<any>}
                    />
                </div>}
            {activeMode === "card" &&
                <Spin spinning={loading}>
                    <div className={`view-container ${transitionClass}`}>
                        <Row gutter={24}>
                            {tableData.length === 0 &&
                                <div style={{width: "100%", textAlign: "center",backgroundColor: "#fff"}}>
                                    <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                                </div>
                            }
                            {tableData.map((item, index) => {
                                return (<Col span={6} key={index}
                                             onMouseEnter={() => handleMouseEnter(item.id)}
                                             onMouseLeave={() => handleMouseLeave(item.id)}
                                >
                                    {(hoverStates[item.id]) && <div className={"card-check-box"}>
                                        <Checkbox
                                            checked={selectedIds.includes(item.id)}
                                            onChange={(e) => toggleItemSelection(item.id, e.target.checked)}
                                        />
                                    </div>}
                                    <CardItem
                                        item={item}
                                        onPreview={(item: any) => {
                                            // 图片、视频、音频可以预览
                                            const arr = [0, 1, 2]
                                            if (!arr.includes(item.resourceType)) {
                                                message.warning("暂不支持该素材类型预览")
                                                return
                                            }
                                            setPreviewOpen(true)
                                            setCurrentRecord(item)
                                        }}
                                    />
                                </Col>)
                            })}
                        </Row>

                    </div>
                </Spin>
            }

            <div
                className={`view-container ${transitionClass}`}
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
}

export default {
    path: "/ai/aiResource",
    element: AiResourceNew
};
