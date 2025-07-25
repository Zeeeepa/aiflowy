import React, {useEffect} from 'react'
import {DatePicker, Form, Input, InputNumber, Modal, Select, theme} from "antd";
import {ModalProps} from "antd/es/modal/interface";
import dayjs from "dayjs";
import {usePostManual} from "../../../hooks/useApis.ts";

export type DataSaveProps = {
    formItems: any[],
    tableId: string,
    entity: any,
    ok?: () => void,
    cancel?: () => void,
    save?: (data: any) => void,
} & ModalProps

export const DataSave: React.FC<DataSaveProps> = (props) => {

    const obj =  props.entity
    const {formItems} = props

    const [saveDataForm] = Form.useForm();

    useEffect(() => {
        if (obj) {
            const initialValues = { ...obj };
            // 处理时间戳字符串字段
            formItems.forEach(item => {
                if (item.fieldType === 3 && initialValues[item.key]) {
                    // 将时间戳字符串转换为dayjs对象
                    initialValues[item.key] = dayjs(parseInt(initialValues[item.key]));
                }
            });
            saveDataForm.setFieldsValue(obj);
        } else {
            saveDataForm.resetFields();
        }
    }, [obj, formItems, saveDataForm]);

    const renderFormItem = (item: {
        key: string;
        title: string;
        fieldType: number;
        required?: boolean;
    }) => {
        const rules = [{ required: item.required, message: `${item.title}不能为空` }];

        switch (item.fieldType) {
            case 1: // String
                return (
                    <Form.Item
                        key={item.key}
                        name={item.key}
                        label={item.title}
                        rules={rules}
                    >
                        <Input placeholder={`请输入${item.title}`} />
                    </Form.Item>
                );
            case 2: // Integer
                return (
                    <Form.Item
                        key={item.key}
                        name={item.key}
                        label={item.title}
                        rules={rules}
                    >
                        <InputNumber
                            placeholder={`请输入${item.title}`}
                            style={{ width: '100%' }}
                            precision={0}
                        />
                    </Form.Item>
                );
            case 3: // Time
                return (
                    <Form.Item
                        key={item.key}
                        name={item.key}
                        label={item.title}
                        rules={rules}
                        getValueProps={(value) => ({
                            value: value ? dayjs(value, 'YYYY-MM-DD HH:mm:ss') : null,
                        })}
                        normalize={(value) => value?.format('YYYY-MM-DD HH:mm:ss')}
                    >
                        <DatePicker
                            format={'YYYY-MM-DD HH:mm:ss'}
                            showTime
                            style={{ width: '100%' }}
                            placeholder={`请选择${item.title}`}
                        />
                    </Form.Item>
                );
            case 4: // Number
                return (
                    <Form.Item
                        key={item.key}
                        name={item.key}
                        label={item.title}
                        rules={rules}
                    >
                        <InputNumber
                            placeholder={`请输入${item.title}`}
                            style={{ width: '100%' }}
                        />
                    </Form.Item>
                );
            case 5: // Boolean
                return (
                    <Form.Item
                        key={item.key}
                        name={item.key}
                        label={item.title}
                        rules={rules}
                    >
                        <Select>
                            <Select.Option value={1}>是</Select.Option>
                            <Select.Option value={0}>否</Select.Option>
                        </Select>
                    </Form.Item>
                );
            default:
               return null
        }
    };

    const {loading: saveLoading, doPost: saveData} = usePostManual("/api/v1/datacenterTable/saveValue")

    const {token} = theme.useToken();
    const formStyle = {
        maxWidth: 'none',
        background: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        padding: 24,
        marginBottom: '20px'
    };

    return (
        <Modal
            title={obj?"修改":"新增"}
            {...props}
            onCancel={() => {
                saveDataForm.resetFields()
                if (props.cancel) {
                    props.cancel();
                }
            }}
            onOk={() => {
                saveDataForm.validateFields().then(values => {
                    const saveParam = {
                        ...values,
                        tableId: props.tableId,
                        id: obj?.id,
                    }
                    saveData({
                        params: saveParam
                    }).then(res => {
                        if (res.data.errorCode === 0) {
                            props.ok?.();
                        }
                    })
                }).catch((e) => {console.log(e)})}}
            confirmLoading={saveLoading}
        >
            <Form
                form={saveDataForm}
                name="saveDataForm"
                style={formStyle}
                labelCol={{span: 6}}
                wrapperCol={{span: 16}}
            >
                {formItems.map(item => renderFormItem(item))}
            </Form>
        </Modal>
    )
};