import React, {useEffect, useMemo, useRef, useState} from 'react';
import TreeClassifiedPage from "../../components/TreeClassifiedPage";
import CrudPage from "../../components/CrudPage";
import {ColumnsConfig} from "../../components/AntdCrud";
import {Button, Form, FormInstance, Input, message, Modal, Select, Switch, Tag, theme} from "antd";
import CustomLeftArrowIcon from "../../components/CustomIcon/CustomLeftArrowIcon.tsx";
import KeywordSearchForm from "../../components/AntdCrud/KeywordSearchForm.tsx";
import {RawValueType} from 'rc-select/lib/interface';
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
import {usePost} from "../../hooks/useApis.ts";
import {convertAttrsToObject} from "../../libs/utils.ts";
import {PlusOutlined} from "@ant-design/icons";
import {useForm} from "antd/es/form/Form";


const columns: ColumnsConfig<any> = [
    {
        title: '标识',
        key: 'id',
        hidden: true,
        form: {
            type: "hidden"
        }
    },
    {
        title: '自定义输入标识',
        key: 'options.isCustomInput',
        hidden: true,
        form: {
            type: "hidden"
        }
    },
    {
        title: '自定义输入标识',
        key: 'isCustomInput',
        hidden: true,
        form: {
            type: "hidden"
        }
    },
    {
        title: 'Icon',
        dataIndex: 'icon',
        key: 'icon',
        form: {
            type: "image",
        },
        render: (text: any) => <img src={text} alt="" style={{width: 30, height: 30}}/>,
    },
    {
        title: '名称',
        dataIndex: 'title',
        key: 'title',
        placeholder: "请输入名称",
        supportSearch: true,
        form: {
            type: "input",
            rules: [{required: true, message: '请输入名称'}],
        }
    },
    {
        title: '供应商',
        dataIndex: 'brand',
        key: 'brand',
        hidden: true,
        dict: '/api/v1/aiLlmBrand/list?asTree=true',
        form: {
            type: "select",
            attrs: {
                fieldNames: {
                    label: "title",
                    value: "key"
                }
            },
            rules: [{required: true, message: '请选择模型供应商'}]
        },

    },
    {
        title: '模型名称',
        dataIndex: 'llmModel',
        key: 'llmModel',
        hidden: true,
        form: {
            type: "input",
            rules: [{required: true, message: '请输入模型名称'}],
        },
    },
    {
        title: '请求地址',
        dataIndex: 'llmEndpoint',
        key: 'llmEndpoint',
        hidden: true,
        editCondition: (data) => {
            return data?.isCustomInput || data?.brand === "ollama"
        }

    },
    {
        title: 'API Key',
        dataIndex: 'llmApiKey',
        key: 'llmApiKey',
        hidden: true,
    },
    {
        title: 'API Secret',
        dataIndex: 'options.apiSecret',
        key: 'options.apiSecret',
        hidden: true,
        form: {
            type: "input",
        },
        editCondition: (data: any) => {
            return data?.brand === "ollama"
        }
    },
    {
        title: '对话路径',
        dataIndex: 'options.chatPath',
        key: 'options.chatPath',
        hidden: true,
        form: {type: 'input'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput && data?.brand !== "ollama";
        },
    },
    {
        title: '向量化路径',
        dataIndex: 'options.embedPath',
        key: 'options.embedPath',
        hidden: true,
        form: {type: 'input'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput && data?.brand !== "ollama" && data?.brand !== "spark";
        },
    },
    {
        title: '重排路径',
        dataIndex: 'options.rerankPath',
        key: 'options.rerankPath',
        hidden: true,
        form: {type: 'input'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput && data?.brand !== "ollama" && data?.brand !== "spark";
        },
    },
    {
        title: '能力',
        dataIndex: 'supportFeatures',
        key: 'supportFeatures',
        editCondition: () => false,
        render: (features: any) => {
            if (!features) {
                return ''
            }

            return features.map((feature: any) => <Tag key={feature}>{feature}</Tag>)
        },
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "20%",
        form: {
            type: "TextArea",
            attrs: {
                rows: 3
            }
        }
    },
    {
        title: '对话模型',
        dataIndex: 'supportChat',
        key: 'supportChat',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand === "ollama" || (data?.brand && data?.isCustomInput);
        },
    },
    {
        title: '方法调用',
        dataIndex: 'supportFunctionCalling',
        key: 'supportFunctionCalling',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (_data: any) => {
            return false;
        },
    },
    {
        title: '向量化',
        dataIndex: 'supportEmbed',
        key: 'supportEmbed',
        hidden: true,
        form: {
            type: 'switch',
        },
        editCondition: (data) => {
            return (data?.brand === "ollama" || (data?.brand && data?.isCustomInput)) && data?.brand !== "spark";
        },
    },
    {
        title: '重排',
        dataIndex: 'supportReranker',
        key: 'supportReranker',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return (data?.brand === "ollama" || (data?.brand && data?.isCustomInput)) && data?.brand !== "spark";
        },
    },
    {
        title: '文生图',
        dataIndex: 'supportTextToImage',
        key: 'supportTextToImage',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand === "ollama" || (data?.brand && data?.isCustomInput);
        },
    },
    {
        title: '图生图',
        dataIndex: 'supportImageToImage',
        key: 'supportImageToImage',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand === "ollama" || (data?.brand && data?.isCustomInput);
        },
    },
    {
        title: '文生音频',
        dataIndex: 'supportTextToAudio',
        key: 'supportTextToAudio',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand === "ollama" || (data?.brand && data?.isCustomInput);
        },
    },
    {
        title: '音频转音频',
        dataIndex: 'supportAudioToAudio',
        key: 'supportAudioToAudio',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand === "ollama" || (data?.brand && data?.isCustomInput);
        },
    },
    {
        title: '文生视频',
        dataIndex: 'supportTextToVideo',
        key: 'supportTextToVideo',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand === "ollama" || (data?.brand && data?.isCustomInput);
        },
    },
    {
        title: '图片转视频',
        dataIndex: 'supportImageToVideo',
        key: 'supportImageToVideo',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand === "ollama" || (data?.brand && data?.isCustomInput);
        },
    },
    {
        title: '多模态',
        dataIndex: 'options.multimodal',
        key: 'options.multimodal',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return (data?.brand !== "ollama" && data?.brand !== "spark") && (data?.brand && data?.isCustomInput);
        },
    },
];

const ModelNameInput: React.FC<{
    treeData: any[];
    columnConfig: any;
    form: FormInstance;
    value?: string;
    onChange?: (value: string) => void;
}> = ({
          treeData,
          columnConfig,
          form,
          value,
          onChange,
      }) => {
    const [modelOptions, setModelOptions] = useState([]);
    const [isCustomInput, setIsCustomInput] = useState<boolean | null>(null);

    // 监听brand字段变化
    const brandValue = Form.useWatch('brand', form);

    // 使用 useRef 来追踪初始化状态，避免无限循环
    const hasInitialized = useRef(false);
    const prevBrandValue = useRef(brandValue);


    // 当品牌变化时，需要判断是否为编辑状态
    useEffect(() => {
        // 如果treeData还没加载完成，不执行逻辑
        if (!treeData.length) return;


        const formId = form.getFieldValue('id');
        const formValues = form.getFieldsValue();

        // 如果是编辑状态且表单已有数据，优先使用表单中的brand值
        const isEditMode = !!formId;
        const formBrandValue = formValues.brand;

        // 确定有效的brand值
        let effectiveBrandValue = brandValue;

        // 如果是编辑模式且表单中有brand值，使用表单中的值
        if (isEditMode && formBrandValue) {
            effectiveBrandValue = formBrandValue;
        } else if (isEditMode && !brandValue && !formBrandValue) {
            // 检查是否有其他相关字段有值，说明是从数据库加载的数据
            const hasDbData = formValues.llmEndpoint || formValues['options.chatPath'] || formValues.llmModel;
            if (hasDbData) {
                return; // 跳过本次更新，等待brand值正确加载
            }
        }


        // 查找品牌数据并设置模型选项
        const brandData = treeData.find(brand => brand.key === effectiveBrandValue);
        const options = brandData?.options.modelList?.map((model: { title: any; llmModel: any; }) => ({
            label: model.title,
            value: model.llmModel
        })) || [];
        setModelOptions(options);

        // 只有在品牌真正发生变化时才执行配置更新逻辑
        const brandChanged = prevBrandValue.current !== effectiveBrandValue;
        prevBrandValue.current = effectiveBrandValue;

        if (!effectiveBrandValue) {
            // 只在新增模式下才清空字段
            if (!isEditMode) {
                form.setFieldValue("brand", undefined);
                form.setFieldValue("llmEndpoint", undefined);
                form.setFieldValue("options.chatPath", undefined);
                form.setFieldValue("options.embedPath", undefined);
                form.setFieldValue("options.rerankPath", undefined);

                // 品牌发生变化时才清空模型名称和描述
                if (brandChanged && hasInitialized.current) {
                    form.setFieldValue("llmModel", undefined);
                    form.setFieldValue("description", undefined);
                    onChange?.('');
                }
            }
        } else {
            // 确保brand字段被正确设置
            form.setFieldValue("brand", effectiveBrandValue);

            if (brandData) {
                const brandOptions = brandData.options;

                // 判断是否应该使用品牌的默认配置
                // 只有在以下情况才使用默认配置：
                // 1. 新增模式
                // 2. 编辑模式下用户主动切换了品牌
                const shouldUseDefaultConfig = !isEditMode || (isEditMode && brandChanged && hasInitialized.current);

                if (shouldUseDefaultConfig) {
                    form.setFieldValue("llmEndpoint", brandOptions.llmEndpoint);
                    form.setFieldValue("options.chatPath", brandOptions.chatPath);
                    form.setFieldValue("options.embedPath", brandOptions.embedPath);
                    form.setFieldValue("options.rerankPath", brandOptions.rerankPath);
                }

                // 新增模式下品牌变化时清空模型名称
                if (!isEditMode && brandChanged && hasInitialized.current) {
                    form.setFieldValue("llmModel", undefined);
                    onChange?.('');
                }
            }

            // 触发表单项的条件渲染更新
            columns.forEach((column) => {
                if (column.editCondition && column.onValuesChange) {
                    column.onValuesChange("", "");
                }
            });
        }

        hasInitialized.current = true;
    }, [brandValue, treeData]);

    useEffect(() => {
        if (isCustomInput == null) {
            const dbIsCustomInput = form.getFieldValue('options.isCustomInput');


            if (dbIsCustomInput !== undefined && dbIsCustomInput !== null) {
                setIsCustomInput(dbIsCustomInput);
                form.setFieldValue("isCustomInput", dbIsCustomInput);
            } else {
                // 如果数据库中没有这个值，根据当前模型值判断是否应该使用自定义输入
                if (value && brandValue && treeData.length > 0) {
                    const brandData = treeData.find(brand => brand.key === brandValue);
                    const modelList = brandData?.options?.modelList || [];
                    const isModelInList = modelList.some((model: any) => model.llmModel === value);
                    setIsCustomInput(!isModelInList);
                    form.setFieldValue("isCustomInput", !isModelInList);
                } else if (!value) {
                    setIsCustomInput(false);
                    form.setFieldValue("isCustomInput", false);
                }
            }
        }

        columns.forEach((column) => {
            if (column.editCondition && column.onValuesChange) {
                column.onValuesChange("", "");
            }
        });


    }, [value, brandValue, treeData]);


    useEffect(() => {

        form.setFieldValue("options.isCustomInput", isCustomInput);
        form.setFieldValue("isCustomInput", isCustomInput);
        form.getFieldsValue();
        columns.forEach(column => {
            if (column.onValuesChange) {
                column.onValuesChange(column, "");
            }
        })
    }, [isCustomInput]);

    const findModelListByBrandKey = (brandKey: string) => {
        const options = treeData.find(tree => tree.key === brandKey)?.options;
        let llmList = [];
        if (options) {
            llmList = options.modelList;
        }
        return llmList;
    }

    const setFormSupportValues = (form: FormInstance, model: any) => {
        form.setFieldValue("supportChat", model.supportChat);
        form.setFieldValue("supportFunctionCalling", model.supportFunctionCalling);
        form.setFieldValue("supportEmbed", model.supportEmbed);
        form.setFieldValue("supportReranker", model.supportReranker);
        form.setFieldValue("supportTextToImage", model.supportTextToImage);
        form.setFieldValue("supportImageToImage", model.supportImageToImage);
        form.setFieldValue("supportTextToAudio", model.supportTextToAudio);
        form.setFieldValue("supportAudioToAudio", model.supportAudioToAudio);
        form.setFieldValue("supportTextToVideo", model.supportTextToVideo);
        form.setFieldValue("supportImageToVideo", model.supportImageToVideo);

        if (model.multimodal) {
            form.setFieldValue("options.multimodal", model.multimodal);
        } else {
            form.setFieldValue("options.multimodal", false);
        }

        if (model.version) {
            form.setFieldValue("options.version", model.version);
        }


        if (model.description) {
            form.setFieldValue("description", model.description);
        }
    }

    return (
        <div>
            {isCustomInput || brandValue === "ollama" ? (
                <Input
                    placeholder={columnConfig.placeholder}
                    {...columnConfig.form?.attrs}
                    value={value}
                    onChange={(e) => {
                        const inputValue = e.target.value;
                        onChange?.(inputValue);
                        form.setFieldValue('llmModel', inputValue);
                    }}
                />
            ) : (
                <Select
                    allowClear
                    showSearch
                    placeholder={columnConfig.placeholder}
                    {...columnConfig.form?.attrs}
                    options={modelOptions}
                    value={value}
                    onChange={(selectValue) => {
                        onChange?.(selectValue);
                        form.setFieldValue('llmModel', selectValue);


                        // 设置其他相关字段
                        const llmList = findModelListByBrandKey(brandValue);
                        if (llmList) {
                            const model = llmList.find((llm: { llmModel: any; }) => llm.llmModel === selectValue);
                            if (model) {
                                setFormSupportValues(form, model);
                            }
                        }
                    }}
                    filterOption={(input, option) => {
                        return ((option?.label ?? '') as string).toLowerCase().includes(input.toLowerCase())
                    }}
                    optionRender={(option, _info) => {
                        const llmList = findModelListByBrandKey(brandValue);

                        if (llmList && llmList.length) {
                            const currentModel = llmList.find((model: {
                                llmModel: RawValueType | undefined;
                            }) => model.llmModel === option.value);

                            if (currentModel) {
                                return (
                                    <div style={{display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap'}}>
                                        <span>{option.label}</span>
                                        <div style={{display: 'flex', gap: '4px', flexWrap: 'wrap'}}>
                                            {currentModel.supportChat && (
                                                <Tag color="blue">文本聊天</Tag>
                                            )}
                                            {currentModel.multimodal && (
                                                <Tag color="purple">多模态</Tag>
                                            )}
                                            {currentModel.supportEmbed && (
                                                <Tag color="orange">向量化</Tag>
                                            )}
                                        </div>
                                    </div>
                                );
                            }
                        }

                        return <span>{option.label}</span>;
                    }}
                />
            )}
            <div style={{marginTop: 8}}>
                {
                    brandValue !== "ollama" &&
                    <Switch
                        size="small"
                        checked={isCustomInput || false}
                        onChange={(checked) => {
                            setIsCustomInput(checked);
                            // 切换时清空当前值
                            const fieldValue = form.getFieldValue("llmModel");
                            if (!fieldValue) {
                                onChange?.('');
                                form.setFieldValue('llmModel', '');
                            }

                            if (!checked) {

                                const llmList = findModelListByBrandKey(brandValue);

                                const find = llmList.find((llm: any) => {
                                    return fieldValue === llm.llmModel
                                })
                                if (!find) {
                                    form.setFieldValue('llmModel', '');
                                }
                            }

                            form.setFieldValue('options.isCustomInput', checked);
                            form.setFieldValue('isCustomInput', checked);

                        }}
                        checkedChildren="自定义"
                        unCheckedChildren="选择"
                    />
                }
            </div>
        </div>
    );
}


const Llms: React.FC<{ paramsToUrl: boolean }> = () => {

    const crudRef = useRef<{ openAddModal: () => void, onSearch: (values: any) => void, formReset: () => void }>(null);

    const [groupId, setGroupId] = useState('')
    const [treeData, setTreeData] = useState<any[]>([]);


    const {doPost: verifyLlmConfig, loading: verifyLoading} = usePost("/api/v1/aiLlm/verifyLlmConfig");


    // 自定义表单渲染工厂函数
    const customFormRenderFactory = (position: "edit" | "search", columnConfig: any, form: FormInstance): JSX.Element | null => {
        if (position === 'search') return null;

        // 自定义模型名称字段
        if (columnConfig.key === 'llmModel') {
            return <ModelNameInput treeData={treeData} columnConfig={columnConfig} form={form}/>
        }

        if (columnConfig.key === 'supportChat') {

            const originOnValuesChange = columnConfig.onValuesChange;

            columnConfig.onValuesChange = (values: any, allChangeValues: any) => {
                if (originOnValuesChange) {
                    originOnValuesChange(values, allChangeValues);
                }
                if (values.supportChat) {
                    form.setFieldValue("supportFunctionCalling", values.supportChat);
                    form.setFieldValue("supportEmbed", !values.supportChat)
                    form.setFieldValue("supportReranker", !values.supportChat);
                }

            }
        }

        if (columnConfig.key === 'supportEmbed') {

            const originOnValuesChange = columnConfig.onValuesChange;

            columnConfig.onValuesChange = (values: any, allChangeValues: any) => {
                if (originOnValuesChange) {
                    originOnValuesChange(values, allChangeValues);
                }
                if (values.supportEmbed) {
                    form.setFieldValue("supportFunctionCalling", !values.supportEmbed);
                    form.setFieldValue("supportChat", !values.supportEmbed)
                    form.setFieldValue("options.multimodal", !values.supportEmbed);
                    form.setFieldValue("supportReranker", !values.supportEmbed);
                }


            }
        }

        if (columnConfig.key === 'options.multimodal') {

            const originOnValuesChange = columnConfig.onValuesChange;

            columnConfig.onValuesChange = (values: any, allChangeValues: any) => {
                if (originOnValuesChange) {
                    originOnValuesChange(values, allChangeValues);
                }

                const multimodal = form.getFieldValue("options.multimodal");

                if (multimodal) {
                    form.setFieldValue("supportEmbed", !multimodal)
                    form.setFieldValue("supportReranker", !multimodal);
                }

            }
        }

        if (columnConfig.key === 'supportReranker') {
            const originOnValuesChange = columnConfig.onValuesChange;

            columnConfig.onValuesChange = (values: any, allChangeValues: any) => {
                if (originOnValuesChange) {
                    originOnValuesChange(values, allChangeValues);
                }
                if (values.supportReranker) {
                    form.setFieldValue("supportFunctionCalling", !values.supportReranker);
                    form.setFieldValue("supportChat", !values.supportReranker)
                    form.setFieldValue("options.multimodal", !values.supportReranker);
                    form.setFieldValue("supportEmbed", !values.supportReranker)
                }


            }
        }


        return null;
    };

    const editLayout: EditLayout = {
        openType: "modal",
        customButton: (form) => {

            return (
                <Button variant="solid" loading={verifyLoading} onClick={async () => {

                    const fieldsValue = form.getFieldsValue();

                    if (!fieldsValue.brand) {
                        message.error("请先选择供应商")
                        return;
                    }

                    if (!fieldsValue.title) {
                        message.error("请输入标题")
                        return;
                    }

                    if (!fieldsValue.llmModel) {
                        message.error("请选择/手动输入大模型名称")
                        return;
                    }

                    if (!(
                        fieldsValue.supportAudioToAudio ||
                        fieldsValue.supportChat ||
                        fieldsValue.supportEmbed ||
                        fieldsValue.supportReranker ||
                        fieldsValue.FunctionCalling

                    )) {
                        message.error("请在 对话模型/向量化/重排 中至少勾选一项")
                        return;
                    }

                    convertAttrsToObject(fieldsValue);

                    const resp = await verifyLlmConfig(
                        {
                            data: fieldsValue,
                        }
                    )

                    if (resp.data.errorCode === 0) {
                        message.success("验证通过")
                    }


                }}>
                    验证配置
                </Button>

            )
        }
    }

    const {token} = theme.useToken();
    const [quickAddModalShow, setQuickAddModalShow] = useState<boolean>()
    const [quickAddFrom] = useForm();

    const [refreshTrigger,setRefreshTrigger] = useState(0);

    const {doPost: doQuickAdd, loading: quickAddLoading} = usePost("/api/v1/aiLlm/quickAdd");

    const treeOptions = useMemo(() => {

        return treeData.map((item) => {

            return {
                label: item.title,
                value: item.key,
            }
        }).filter(item => {
            return item.value !== "ollama"
        });

    }, [treeData])

    const submitForm = async () => {
       const resp =  await doQuickAdd({
            data: quickAddFrom.getFieldsValue(),
        })

        if(resp.data.errorCode === 0) {
            message.success("添加成功！")

        }else{
            message.error("添加失败，请稍后重试！")
        }

        closeQuickAddModal();
        setRefreshTrigger(refreshTrigger == 0 ? 1 : 0)
    }

    const formStyle = {
        maxWidth: 'none',
        background: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        padding: 24,
        marginBottom: '20px'
    };

    const closeQuickAddModal = () => {
        quickAddFrom.resetFields();
        setQuickAddModalShow(false)
    }

    return (
        <div style={{margin: "24px"}}>
            <KeywordSearchForm
                setIsEditOpen={() => {
                    crudRef.current?.openAddModal()
                }}
                addButtonText="新增大模型"
                columns={columns}
                customHandleButton={<Button icon={<PlusOutlined/>} type="primary" onClick={() => {
                    setQuickAddModalShow(true)
                }}>一键添加</Button>}
                tableAlias={"aiLlm"}
                onSearch={(values: any) => {
                    crudRef.current?.onSearch(values)
                }}/>

            <TreeClassifiedPage treeTableAlias="aiLlmBrand"
                                treeCardTitle={"接入平台"}
                                treeEditable={false}
                                onTreeDataLoaded={setTreeData}
                                treeTitleIconRender={(item) => {
                                    if (typeof item.icon === "string" && (item.icon.startsWith("http") || item.icon.startsWith("/"))) {
                                        return <img src={item.icon} alt={item.title} style={{width: 14, height: 14}}/>
                                    }
                                    return React.isValidElement(item.icon)
                                        ? item.icon
                                        : <div dangerouslySetInnerHTML={{__html: item.icon || ""}}
                                               style={{padding: "3px"}}/>
                                }}
                                treeCardExtra={
                                    <CustomLeftArrowIcon/>
                                }
                                onTreeSelect={setGroupId}>
                <CrudPage columnsConfig={columns}  tableAlias="aiLlm" params={{brand: groupId}} key={groupId}
                          needHideSearchForm={true} externalRefreshTrigger={refreshTrigger}
                          editLayout={editLayout} ref={crudRef} formRenderFactory={customFormRenderFactory}/>
            </TreeClassifiedPage>
            <Modal
                title={"一键添加大模型"}
                closable={!quickAddLoading}
                maskClosable={!quickAddLoading}
                open={quickAddModalShow}
                onCancel={closeQuickAddModal}
                onOk={() => {
                    quickAddFrom.submit();
                }}
                cancelButtonProps={{
                    disabled: !quickAddLoading,
                }}
                okButtonProps={{
                    loading: quickAddLoading,
                }}
            >
                <Form style={formStyle} form={quickAddFrom} onFinish={submitForm}>
                    <Form.Item
                        label={"供应商"}
                        name={"brand"}
                        rules={[
                            {required: true, message: '请选择供应商'}
                        ]}
                    >
                        <Select
                            placeholder={"选择供应商"}
                            allowClear
                            filterOption={(input, option) =>
                                ((option?.label ?? '') as string).toLowerCase().includes(input.toLowerCase())
                            }
                            options={treeOptions}
                        />
                    </Form.Item>
                    <Form.Item label={"Api Key"} name={"apiKey"} required={false}>
                        <Input/>
                    </Form.Item>

                </Form>
            </Modal>
        </div>
    )
};

export default {
    path: "/ai/llms",
    element: Llms
};