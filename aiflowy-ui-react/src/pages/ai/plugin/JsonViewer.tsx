import React from 'react';
import {Button, message, Tag} from 'antd';
// import { CopyOutlined } from '@ant-design/icons';
import './less/JsonViewer.less';
import {CopyOutlined} from "@ant-design/icons"; // 可选的样式文件

interface JsonViewerProps {
    jsonString: string;
}

const JsonViewer: React.FC<JsonViewerProps> = ({ jsonString }) => {
    // 解析JSON字符串
    const parseJson = () => {
        try {
            return JSON.parse(jsonString);
        } catch (error) {
            return null;
        }
    };

    const jsonData = parseJson();
    if (!jsonData) return <div></div>;

    // 复制JSON到剪贴板
    const copyToClipboard = () => {
        navigator.clipboard.writeText(jsonString)
            .then(() => {
                message.success('JSON已复制到剪贴板');
            })
            .catch(() => {
                message.error('复制失败');
            });
    };

    // 递归渲染JSON数据
    const renderJson = (data: any, depth = 0) => {
        if (typeof data === 'string') {
            return <span className="json-string">"{data}"</span>;
        }
        if (typeof data === 'number') {
            return <span className="json-number">{data}</span>;
        }
        if (typeof data === 'boolean') {
            return <span className="json-boolean">{data.toString()}</span>;
        }
        if (data === null) {
            return <span className="json-null">null</span>;
        }
        if (Array.isArray(data)) {
            return (
                <div style={{ marginLeft: `${depth * 15}px` }}>
                    [
                    {data.map((item, index) => (
                        <div key={index}>
                            {renderJson(item, depth + 1)}
                            {index < data.length - 1 && ','}
                        </div>
                    ))}
                    ]
                </div>
            );
        }
        if (typeof data === 'object') {
            return (
                <div style={{ marginLeft: `${depth * 15}px` }}>
                    {'{'}
                    {Object.entries(data).map(([key, value], index, array) => (
                        <div key={key}>
                            <span className="json-key">"{key}"</span>: {renderJson(value, depth + 1)}
                            {index < array.length - 1 && ','}
                        </div>
                    ))}
                    {'}'}
                </div>
            );
        }
        return <span>{String(data)}</span>;
    };

    return (
        <>
            <div className="json-meta">
                {jsonData.info && <Tag color="blue">{jsonData.info}</Tag>}
                {jsonData.status && (
                    <Tag color={jsonData.status === '1' ? 'green' : 'red'}>
                        状态: {jsonData.status}
                    </Tag>
                )}
            </div>


            {/* 完整JSON展示 */}
            <div className="json-display">
                <span style={{display:'flex',justifyContent:'flex-end'}} >
                     <Button onClick={copyToClipboard} icon={<CopyOutlined />}></Button>
                </span>

                <pre>{renderJson(jsonData)}</pre>
            </div>
        </>

    );
};

export default JsonViewer;