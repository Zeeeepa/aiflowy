import React, {forwardRef, useImperativeHandle, useState} from "react";
import {Button, Empty, Form, message, Spin} from "antd";
import {SendOutlined} from "@ant-design/icons";
import {usePostManual} from "../../../hooks/useApis.ts";
import JsonView from "react18-json-view";
import {DynamicItem} from "../../../libs/workflowUtil.tsx";

export type SingleRunProps = {
    ref?: any,
    workflowId: any,
    node: any
}
export const SingleRun: React.FC<SingleRunProps> = forwardRef(({workflowId, node}, ref) => {

    const {doPost: doSingleRun} = usePostManual("/api/v1/aiWorkflow/singleRun")
    const [form] = Form.useForm();
    const [submitLoading, setSubmitLoading] = useState(false)
    const [executeResult, setExecuteResult] = useState<any>("")
    const onFinish = (values: any) => {
        const params = {
            id: workflowId,
            node: node,
            variables: values
        }
        setSubmitLoading(true)
        doSingleRun({
            data: params
        }).then(res => {
            setExecuteResult(res.data.data)
            setSubmitLoading(false)
            if (res.data.errorCode == 0) {
                message.success("成功")
            } else {
                message.error(res.data.message)
            }
        })
    };
    const onFinishFailed = (errorInfo: any) => {
        console.log('Failed:', errorInfo);
    };

    useImperativeHandle(ref, () => ({
        resetForm: () => {
            form.resetFields()
            setExecuteResult("")
        }
    }));

    return (
        <>
            <Spin spinning={submitLoading}>
                <Form
                    form={form}
                    name="basic"
                    labelCol={{span: 5}}
                    wrapperCol={{span: 16}}
                    onFinish={onFinish}
                    onFinishFailed={onFinishFailed}
                    autoComplete="off"
                >
                    {node.data?.parameters?.map((item: any) => {
                        let text = item.description ? item.description : item.name
                        return (
                            <Form.Item
                                key={item.id}
                                label={text}
                                name={item.name}
                                rules={[{required: true, message: '请输入' + text}]}
                            >
                                {DynamicItem(item, form, item.name)}
                            </Form.Item>
                        )
                    })}
                    <Form.Item wrapperCol={{offset: 5, span: 18}}>
                        <Button disabled={submitLoading} loading={submitLoading} type="primary" htmlType="submit">
                            <SendOutlined/> 开始运行
                        </Button>
                    </Form.Item>
                </Form>
                <div style={{marginBottom: "10px"}}>执行结果：</div>
                <div style={{
                    padding: "20px",
                    backgroundColor: "#fafafa",
                    textAlign: "center",
                    wordWrap: "break-word",
                }}
                >
                    {executeResult ?
                        <JsonView src={executeResult}/> :
                        <Empty description={'暂无内容'} image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                    }
                </div>
            </Spin>
        </>
    )
})