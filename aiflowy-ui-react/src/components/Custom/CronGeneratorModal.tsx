import React, { useEffect, useState } from 'react';
import { Space ,Card, Tabs, Radio, InputNumber, Select, Row, Col, Input, Form, Modal } from 'antd';

const { Option } = Select;

type TimeField = 'second' | 'minute' | 'hour' | 'day' | 'month' | 'week';
type SelectionMode = 'every' | 'interval' | 'specific' | 'range' | 'last' | 'workday' | 'none';

interface CronGeneratorModalProps {
    visible: boolean;
    initialValue?: string;
    handleCancel: () => void;
    handleOk: (value: string) => void;
}

interface TimeSelectionState {
    mode: SelectionMode;
    intervalStart?: number;
    intervalStep?: number;
    rangeStart?: number;
    rangeEnd?: number;
    specificValues?: string[];
    workday?: number;
    lastDay?: boolean;
    lastWeekday?: string;
    nearestWeekday?: number;
}

const defaultCron = '0/5 * * * * ?';

const weekDays = [
    { value: 'SUN', label: '周日' },
    { value: 'MON', label: '周一' },
    { value: 'TUE', label: '周二' },
    { value: 'WED', label: '周三' },
    { value: 'THU', label: '周四' },
    { value: 'FRI', label: '周五' },
    { value: 'SAT', label: '周六' },
];

const months = [
    { value: '1', label: '一月' },
    { value: '2', label: '二月' },
    { value: '3', label: '三月' },
    { value: '4', label: '四月' },
    { value: '5', label: '五月' },
    { value: '6', label: '六月' },
    { value: '7', label: '七月' },
    { value: '8', label: '八月' },
    { value: '9', label: '九月' },
    { value: '10', label: '十月' },
    { value: '11', label: '十一月' },
    { value: '12', label: '十二月' },
];

const CronGeneratorModal: React.FC<CronGeneratorModalProps> = ({ visible, initialValue, handleCancel, handleOk }) => {
    const [activeTab, setActiveTab] = useState<TimeField>('second');
    const [cronExpression, setCronExpression] = useState<string>(initialValue || defaultCron);

    const [timeSelections, setTimeSelections] = useState<Record<TimeField, TimeSelectionState>>({
        second: { mode: 'interval', intervalStart: 0, intervalStep: 5 },
        minute: { mode: 'every' },
        hour: { mode: 'every' },
        day: { mode: 'every' },
        month: { mode: 'every' },
        week: { mode: 'none' }, // Quartz 中日和周通常互斥
    });

    useEffect(() => {
        if (initialValue && initialValue !== cronExpression) {
            setCronExpression(initialValue);
            parseCronExpression(initialValue);
        }
    }, [initialValue]);

    const parseCronExpression = (expression: string) => {
        // TODO: 实现从 cron 表达式解析为 timeSelections 的逻辑
        console.log('Parsing cron expression:', expression);
    };

    const updateCronExpression = (field: TimeField, newSelection: Partial<TimeSelectionState>) => {
        setTimeSelections(prev => {
            const updated = {
                ...prev,
                [field]: {
                    ...prev[field],
                    ...newSelection
                }
            };

            // 处理日和周的互斥关系
            if (field === 'day' && newSelection.mode !== 'every') {
                updated.week = { mode: 'none' };
            } else if (field === 'week' && newSelection.mode !== 'none') {
                updated.day = { mode: 'every' };
            }

            const newExpression = generateQuartzCron(updated);
            setCronExpression(newExpression);

            return updated;
        });
    };

    const generateQuartzCron = (selections: Record<TimeField, TimeSelectionState>): string => {
        const parts = [
            generateCronPart('second', selections.second),
            generateCronPart('minute', selections.minute),
            generateCronPart('hour', selections.hour),
            generateCronPart('day', selections.day),
            generateCronPart('month', selections.month),
            generateCronPart('week', selections.week),
        ];

        return parts.join(' ');
    };

    const generateCronPart = (field: TimeField, selection: TimeSelectionState): string => {
        const { mode } = selection;

        switch (mode) {
            case 'every':
                return '*';
            case 'none':
                return '?'; // Quartz 中使用 ? 表示不指定
            case 'interval':
                return `${selection.intervalStart || 0}/${selection.intervalStep || 1}`;
            case 'specific':
                return selection.specificValues?.join(',') || (field === 'week' ? '?' : '*');
            case 'range':
                return `${selection.rangeStart || 0}-${selection.rangeEnd || getFieldMax(field)}`;
            case 'last':
                if (field === 'day') return 'L';
                if (field === 'week') return `${selection.lastWeekday || 'SUN'}L`;
                return '*';
            case 'workday':
                return `${selection.nearestWeekday || 1}W`;
            default:
                return field === 'week' ? '?' : '*';
        }
    };

    const getFieldMax = (field: TimeField): number => {
        switch (field) {
            case 'second':
            case 'minute':
                return 59;
            case 'hour':
                return 23;
            case 'day':
                return 31;
            case 'month':
                return 12;
            case 'week':
                return 7;
            default:
                return 0;
        }
    };

    const handleTabChange = (key: string) => {
        setActiveTab(key as TimeField);
    };

    const renderTimeFieldTab = (
        field: TimeField,
        min: number,
        max: number,
        unit: string,
        options?: {
            isWeek?: boolean;
            isMonth?: boolean;
        }
    ) => {
        const selection = timeSelections[field];

        return (
            <div>
                <Form.Item label="选择模式">
                    <Radio.Group
                        value={selection.mode}
                        onChange={(e) => updateCronExpression(field, { mode: e.target.value })}
                    >
                        <Radio value="every">{field === 'week' ? '不指定(使用?)' : `每${unit}`}</Radio>
                        {field === 'week' && <Radio value="none">不指定(使用?)</Radio>}
                        <Radio value="interval">周期</Radio>
                        <Radio value="specific">具体{unit}(可多选)</Radio>
                        <Radio value="range">区间</Radio>
                        {field === 'day' && (
                            <>
                                <Radio value="last">最后一天</Radio>
                                <Radio value="workday">最近工作日</Radio>
                            </>
                        )}
                        {field === 'week' && <Radio value="last">最后一个星期</Radio>}
                    </Radio.Group>
                </Form.Item>

                {selection.mode === 'interval' && (
                    <Form.Item label="周期设置">
                        <Space.Compact>
                            <span style={{ lineHeight: '32px', marginRight: 8 }}>从</span>
                            <InputNumber
                                min={min}
                                max={max}
                                value={selection.intervalStart}
                                onChange={(value) => updateCronExpression(field, { intervalStart: value || 0 })}
                            />
                            <span style={{ lineHeight: '32px', marginRight: 8, marginLeft: 8 }}>开始，每</span>
                            <InputNumber
                                min={1}
                                max={max}
                                value={selection.intervalStep}
                                onChange={(value) => updateCronExpression(field, { intervalStep: value || 1 })}
                            />
                            <span style={{ lineHeight: '32px', marginLeft: 8 }}>{unit}执行一次</span>
                        </Space.Compact>
                    </Form.Item>
                )}

                {selection.mode === 'specific' && (
                    <Form.Item label={`选择具体${unit}`}>
                        <Select
                            mode="multiple"
                            style={{ width: '100%' }}
                            placeholder={`请选择具体的${unit}`}
                            value={selection.specificValues}
                            onChange={(value) => updateCronExpression(field, { specificValues: value })}
                        >
                            {options?.isWeek ? (
                                weekDays.map(day => (
                                    <Option key={day.value} value={day.value}>{day.label}</Option>
                                ))
                            ) : options?.isMonth ? (
                                months.map(month => (
                                    <Option key={month.value} value={month.value}>{month.label}</Option>
                                ))
                            ) : (
                                Array.from({ length: max - min + 1 }, (_, i) => {
                                    const val = i + min;
                                    return <Option key={val} value={val.toString()}>{val}</Option>;
                                })
                            )}
                        </Select>
                    </Form.Item>
                )}

                {selection.mode === 'range' && (
                    <Form.Item label="设置区间">
                        <Space.Compact>
                            <span style={{ lineHeight: '32px', marginRight: 8 }}>从</span>
                            <InputNumber
                                min={min}
                                max={max}
                                value={selection.rangeStart}
                                onChange={(value) => updateCronExpression(field, { rangeStart: value || min })}
                            />
                            <span style={{ lineHeight: '32px', marginRight: 8, marginLeft: 8 }}>到</span>
                            <InputNumber
                                min={min}
                                max={max}
                                value={selection.rangeEnd}
                                onChange={(value) => updateCronExpression(field, { rangeEnd: value || max })}
                            />
                        </Space.Compact>
                    </Form.Item>
                )}

                {selection.mode === 'workday' && field === 'day' && (
                    <Form.Item label="设置最近工作日">
                        <Space.Compact>
                            <span style={{ lineHeight: '32px', marginRight: 8 }}>本月</span>
                            <InputNumber
                                min={1}
                                max={31}
                                value={selection.nearestWeekday}
                                onChange={(value) => updateCronExpression(field, { nearestWeekday: value || 1 })}
                            />
                            <span style={{ lineHeight: '32px', marginLeft: 8 }}>日最近的工作日</span>
                        </Space.Compact>
                    </Form.Item>
                )}

                {selection.mode === 'last' && field === 'week' && (
                    <Form.Item label="设置最后一个星期">
                        <Select
                            style={{ width: '100%' }}
                            value={selection.lastWeekday || 'SUN'}
                            onChange={(value) => updateCronExpression(field, { lastWeekday: value })}
                        >
                            {weekDays.map(day => (
                                <Option key={day.value} value={day.value}>{day.label}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                )}
            </div>
        );
    };

    const tabItems = [
        {
            key: 'second',
            label: '秒',
            children: renderTimeFieldTab('second', 0, 59, '秒'),
        },
        {
            key: 'minute',
            label: '分钟',
            children: renderTimeFieldTab('minute', 0, 59, '分钟'),
        },
        {
            key: 'hour',
            label: '小时',
            children: renderTimeFieldTab('hour', 0, 23, '小时'),
        },
        {
            key: 'day',
            label: '日',
            children: renderTimeFieldTab('day', 1, 31, '日'),
        },
        {
            key: 'month',
            label: '月',
            children: renderTimeFieldTab('month', 1, 12, '月', { isMonth: true }),
        },
        {
            key: 'week',
            label: '周',
            children: renderTimeFieldTab('week', 1, 7, '周', { isWeek: true }),
        }
    ];

    return (
        <Modal
            title="Cron 表达式生成器"
            open={visible}
            onOk={() => {
                handleOk(cronExpression)
            }}
            onCancel={() => {
                handleCancel();
            }}
            width={600}
            styles={{body: { padding: '16px 0' }}}
        >
            <Card>
                <Row gutter={16} style={{ marginBottom: 16 }}>
                    <Col span={24}>
                        <Form.Item label="Cron 表达式">
                            <Input
                                value={cronExpression}
                                onChange={(e) => {
                                    setCronExpression(e.target.value);
                                }}
                            />
                        </Form.Item>
                    </Col>
                </Row>

                <Tabs activeKey={activeTab} onChange={handleTabChange} items={tabItems} />
            </Card>
        </Modal>
    );
};

export default CronGeneratorModal;