import {Button, Card, Divider, Input, message, Modal, Tooltip, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {ModalProps} from "antd/es/modal/interface";
import logo from "/favicon.png";
import {useGetManual, usePostManual, useSave} from "../../../hooks/useApis.ts";

const { Text } = Typography;
interface Tool {
    id: string;
    name: string;
    description: string;
    usageCount: number;
    params: {
        key: string;
        label: string;
        required: boolean;
        defaultValue?: any;
    }[];
}

export interface Plugin {
    id: string;
    name: string;
    description: string;
    icon: string;
    tools: Tool[];
}

interface PluginModalProps extends ModalProps {
    params?: any;
    onToolExecute?: (pluginId: string, toolId: string, params: Record<string, any>) => void;
}
export const PluginsModal: React.FC<PluginModalProps> = ({
                                                             params,
                                                            onToolExecute,
                                                            ...modalProps
                                                        }) => {
    const {doPost: doPostGetToolsList} = usePostManual('/api/v1/aiPluginTool/toolsList')
    const {doSave: doSavePlugin} = useSave("aiBotPlugins");
    const {doPost: doRemovePlugin} = usePostManual('/api/v1/aiBotPlugins/doRemove');
    const {doPost: doPostGetPluginsList} = usePostManual(('/api/v1/aiPlugin/getList'))
    const {doGet: doGetPluginsList} = useGetManual(('/api/v1/aiPlugin/list'))


    const [selectedPlugin, setSelectedPlugin] = useState<Plugin | null>(null);
    const [activeTool] = useState<Tool | null>(null);
    const [plugins, setPlugins] = useState<Plugin[]>([]);
    const [toolParams, setToolParams] = useState<Record<string, any>>({});
    const [loading, setLoading] = useState(false);
    const [pluginTools, setPluginTools] = useState([])
    const handlePluginSelect = (plugin: Plugin) => {
        doPostGetToolsList({data: {pluginId: plugin.id, botId: params.id}}).then(res => {
            setPluginTools(res?.data?.data)
        })
        setSelectedPlugin(plugin);
    };
    useEffect(() => {
        doPostGetPluginsList({data: {botId: params.id}}).then(res => {
            setPlugins(res?.data?.data)
        })
    }, [])
    const executeTool = async () => {
        if (!selectedPlugin || !activeTool) return;

        setLoading(true);
        try {
            await onToolExecute?.(selectedPlugin.id, activeTool.id, toolParams);
            message.success(`${activeTool.name} 执行成功`);
        } catch (error) {
            // @ts-ignore
            message.error(`执行失败: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };
    const [hoverStates, setHoverStates] = useState<Record<string, boolean>>({});
    const handleRemoveBotPluginTool = (plugintoolId: string) =>{
        doRemovePlugin({data: {pluginToolId: plugintoolId, botId: params.id}}).then(res => {
            if (res?.data?.errorCode === 0){
                message.success('删除成功')
                doPostGetToolsList({data: {pluginId: selectedPlugin?.id, botId: params.id}}).then(res => {
                    setPluginTools(res?.data?.data)
                })
            } else {
                message.error('删除失败')
            }
        })
    }
    return (
        <Modal
            title="插件工具管理"
            width={900}
            styles={{
                body: { padding: '24px' }, // 使用 styles 替代 bodyStyle
            }}
            {...modalProps}
            footer={null}
        >
            <div style={{ display: 'flex', gap: 24 }}>
                {/* 左侧插件列表 */}
                <div style={{ width: 280 }}>
                    <div style={{ marginBottom: 16 }}>
                        <Input.Search placeholder="搜索插件..." onSearch={value => {
                            doGetPluginsList({params:{name: value}}).then(r =>{
                                setPlugins(r?.data?.data)
                            })
                        }}/>
                    </div>

                    <div style={{
                        height: 500,
                        overflowY: 'auto',
                        border: '1px solid #f0f0f0',
                        borderRadius: 8
                    }}>
                        {plugins?.map(plugin => (
                            <Card
                                key={plugin.id}
                                hoverable
                                style={{
                                    marginBottom: 12,
                                    borderRadius: 8,
                                    border: selectedPlugin?.id === plugin.id
                                        ? '1px solid #1890ff'
                                        : '1px solid #f0f0f0'
                                }}
                                onClick={() => handlePluginSelect(plugin)}
                            >
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    <img
                                        src={plugin.icon? plugin.icon : logo}
                                        alt={plugin.name}
                                        style={{
                                            width: 40,
                                            height: 40,
                                            marginRight: 12,
                                            borderRadius: 4
                                        }}
                                    />
                                    <div>
                                        <Text strong>{plugin.name}</Text>
                                        <div style={{ color: '#999', fontSize: 12 }}>
                                            <Tooltip title={plugin.description}>
                                                <div style={{
                                                    display: '-webkit-box',
                                                    WebkitLineClamp: 1,      // 限制显示行数
                                                    WebkitBoxOrient: 'vertical',
                                                    overflow: 'hidden',
                                                    textOverflow: 'ellipsis',
                                                }}>
                                                    {plugin.description}
                                                </div>
                                            </Tooltip>
                                        </div>
                                    </div>
                                </div>
                            </Card>
                        ))}
                    </div>
                </div>

                {/* 右侧工具详情 */}
                <div style={{ flex: 1 }}>
                    {selectedPlugin ? (
                        <>
                            <div style={{
                                display: 'flex',
                                alignItems: 'center',
                                marginBottom: 16
                            }}>
                                <img
                                    src={selectedPlugin.icon ? selectedPlugin.icon : logo }
                                    alt={selectedPlugin.name}
                                    style={{
                                        width: 32,
                                        height: 32,
                                        marginRight: 8,
                                        borderRadius: 4
                                    }}
                                />
                                <Text strong style={{ fontSize: 16 }}>
                                    {selectedPlugin.name}
                                </Text>
                            </div>

                            <div style={{
                                height: 500,
                                overflowY: 'auto',
                                padding: 16,
                                border: '1px solid #f0f0f0',
                                borderRadius: 8
                            }}>
                                {pluginTools.map((item: any) => (
                                    <Card key={item?.id} style={{ marginBottom: 16 }}>
                                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                            <div style={{ display: 'flex', flexDirection: "column" }}>
                                                <Text strong>{item?.name}</Text>
                                                <Tooltip title={item?.description}>
                                                    <div style={{
                                                        display: '-webkit-box',
                                                        WebkitLineClamp: 1,      // 限制显示行数
                                                        WebkitBoxOrient: 'vertical',
                                                        overflow: 'hidden',
                                                        color: '#A0A1A5',
                                                        fontSize: 12,
                                                        textOverflow: 'ellipsis',
                                                    }}>
                                                       {item?.description}
                                                    </div>
                                                </Tooltip>
                                            </div>
                                            {item.joinBot ? (
                                                <Button
                                                    danger={hoverStates[item.id]} // 仅当前按钮变红
                                                    onMouseEnter={() => setHoverStates({ ...hoverStates, [item.id]: true })}
                                                    onMouseLeave={() => setHoverStates({ ...hoverStates, [item.id]: false })}
                                                    onClick={() => handleRemoveBotPluginTool(item.id)}
                                                    style={{ width: "80px" }}
                                                >
                                                    {hoverStates[item.id] ? "移除" : "已添加"}
                                                </Button>
                                            ) : (
                                                <Button
                                                    type="primary"
                                                    onClick={() => {
                                                        doSavePlugin({
                                                            data: {
                                                                botId: params.id,
                                                                pluginToolId: item.id,
                                                            }
                                                        }).then(r => {
                                                            if (r?.data?.errorCode === 0){
                                                                message.success("添加成功")
                                                                doPostGetToolsList({data: {pluginId: selectedPlugin.id, botId: params.id}}).then(res => {
                                                                    setPluginTools(res?.data?.data)
                                                                })
                                                                onToolExecute?.(selectedPlugin.id, item.id, toolParams);
                                                            } else {
                                                                message.error("添加失败")
                                                            }

                                                        })
                                                    }}
                                                    style={{ width: "80px" }}
                                                >
                                                    添加
                                                </Button>
                                            )}
                                        </div>
                                    </Card>
                                ))}
                            </div>
                        </>
                    ) : (
                        <div style={{
                            height: 500,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            color: '#999'
                        }}>
                            请从左侧选择一个插件
                        </div>
                    )}
                </div>

                {/* 工具参数面板 */}
                {activeTool && (
                    <div style={{ width: 300 }}>
                        <div style={{
                            padding: 16,
                            height: 500,
                            border: '1px solid #f0f0f0',
                            borderRadius: 8
                        }}>
                            <Text strong style={{ fontSize: 16 }}>
                                {activeTool.name} 参数设置
                            </Text>
                            <Divider style={{ margin: '12px 0' }} />

                            {activeTool.params.map(param => (
                                <div key={param.key} style={{ marginBottom: 16 }}>
                                    <Text strong>{param.label}:</Text>
                                    <Input
                                        style={{ marginTop: 4 }}
                                        placeholder={`请输入${param.label}`}
                                        value={toolParams[param.key] || ''}
                                        onChange={(e) => setToolParams({
                                            ...toolParams,
                                            [param.key]: e.target.value
                                        })}
                                    />
                                </div>
                            ))}

                            <Button
                                type="primary"
                                block
                                loading={loading}
                                onClick={executeTool}
                                style={{ marginTop: 24 }}
                            >
                                执行工具
                            </Button>
                        </div>
                    </div>
                )}
            </div>
        </Modal>

    );
};
export default {
    path: "/ai/pluginModal",
    element: PluginsModal
};
