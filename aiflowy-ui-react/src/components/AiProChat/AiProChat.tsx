import React, {useLayoutEffect, useRef, useState} from 'react';
import {Bubble, Prompts, Sender, Welcome} from '@ant-design/x';
import {Button, GetProp, message, Space, Spin} from 'antd';
import {CopyOutlined, OpenAIOutlined, SyncOutlined} from '@ant-design/icons';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import remarkBreaks from 'remark-breaks';
import logo from "/favicon.png";
import { UserOutlined } from '@ant-design/icons';
import './aiprochat.less'
const fooAvatar: React.CSSProperties = {
    color: '#fff',
    backgroundColor: '#87d068',
};

export type ChatMessage = {
    id: string;
    content: string;
    role: 'user' | 'assistant' | 'aiLoading';
    created: number;
    updateAt: number;
    loading?: boolean;
};

export type AiProChatProps = {
    loading?: boolean;
    chats?: ChatMessage[];
    onChatsChange?: (value: ((prevState: ChatMessage[]) => ChatMessage[]) | ChatMessage[]) => void;
    style?: React.CSSProperties;
    appStyle?: React.CSSProperties;
    helloMessage?: string;
    botAvatar?: string;
    request: (messages: ChatMessage[]) => Promise<Response>;
    clearMessage?: () => void;
    prompts?: GetProp<typeof Prompts, 'items'>;
    inputDisabled?: boolean;
};

export const AiProChat = ({
                              loading,
                              chats: parentChats,
                              onChatsChange: parentOnChatsChange,
                              style = {},
                              appStyle = {},
                              helloMessage = '欢迎使用 AIFlowy',
                              botAvatar = `${logo}`,
                              request,
                              clearMessage,
                              inputDisabled = false,
                              prompts,
                          }: AiProChatProps) => {
    const isControlled = parentChats !== undefined && parentOnChatsChange !== undefined;
    const [internalChats, setInternalChats] = useState<ChatMessage[]>([]);
    const chats = isControlled ? parentChats : internalChats;
    const setChats = isControlled ? parentOnChatsChange : setInternalChats;
    const [content, setContent] = useState('');
    const [sendLoading, setSendLoading] = useState(false);
    const [isStreaming, setIsStreaming] = useState(false);
    const messagesContainerRef = useRef<HTMLDivElement>(null);
    const messagesEndRef = useRef<HTMLDivElement>(null);
    // 控制是否允许自动滚动
    const autoScrollEnabled = useRef(true); // 默认允许自动滚动
    const isUserScrolledUp = useRef(false); // 用户是否向上滚动过
    // 滚动到底部逻辑
    const scrollToBottom = () => {
        const container = messagesContainerRef.current;
        if (container && autoScrollEnabled.current) {
            container.scrollTop = container.scrollHeight;
        }
    };

    // 组件挂载时滚动
    useLayoutEffect(() => {
        scrollToBottom();
    }, []);

    // 消息更新时滚动
    useLayoutEffect(() => {
        if (autoScrollEnabled.current) {
            scrollToBottom();
        }
    }, [chats]);
    useLayoutEffect(() => {
        const container = messagesContainerRef.current;
        if (!container) return;

        const handleScroll = () => {
            const { scrollTop, scrollHeight, clientHeight } = container;
            const atBottom = scrollHeight - scrollTop <= clientHeight + 5; // 允许误差 5px

            if (atBottom) {
                // 用户回到底部，恢复自动滚动
                autoScrollEnabled.current = true;
                isUserScrolledUp.current = false;
            } else {
                // 用户向上滚动，禁用自动滚动
                autoScrollEnabled.current = false;
                isUserScrolledUp.current = true;
            }
        };

        container.addEventListener('scroll', handleScroll);
        return () => {
            container.removeEventListener('scroll', handleScroll);
        };
    }, []);
    // 提交流程优化
    const handleSubmit = async (newMessage: string) => {
        // 使用 newMessage 的值（如果存在），否则使用 content 状态
        const messageContent = newMessage?.trim() || content.trim();
        if (!messageContent) return;
        setSendLoading(true);
        setIsStreaming(true);
        const userMessage: ChatMessage = {
            role: 'user',
            id: Date.now().toString(),
            content: messageContent,
            created: Date.now(),
            updateAt: Date.now(),
        };
        const aiMessage: ChatMessage = {
            role: 'assistant',
            id: Date.now().toString(),
            content: '',
            loading: true,
            created: Date.now(),
            updateAt: Date.now(),
        };
        const temp = [userMessage, aiMessage];
        setChats?.((prev: ChatMessage[]) => [...(prev || []), ...temp]);
        setTimeout(scrollToBottom, 50);
        setContent('');
        try {
            const response = await request([...(chats || []), userMessage]);
            if (!response?.body) return;
            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let partial = '';
            let currentContent = '';
            while (true) {
                const {done, value} = await reader.read();
                if (done) break;
                partial += decoder.decode(value, {stream: true});

                const id = setInterval(() => {
                    currentContent = partial.slice(0, currentContent.length + 2);
                    setChats?.((prev: ChatMessage[]) => {
                        const newChats = [...(prev || [])];
                        const lastMsg = newChats[newChats.length - 1];
                        if (lastMsg?.role === 'assistant') {
                            lastMsg.loading = false;
                            lastMsg.content = currentContent;
                            lastMsg.updateAt = Date.now();
                        }
                        return newChats;
                    });
                    if (autoScrollEnabled.current) {
                        scrollToBottom(); // 只有在自动滚动开启时才滚动
                    }
                    if (currentContent === partial) {
                        clearInterval(id);
                    }
                }, 50);

            }
        } catch (error) {
            console.error('Error:', error);
            console.error('出错了:', error);
        } finally {
            setIsStreaming(false);
            setSendLoading(false);
        }
    };

    // 重新生成消息
    const handleRegenerate = async (index: number) => {
        // 找到当前 assistant 消息对应的上一条用户消息
        const prevMessage: ChatMessage = {
            role: 'user',
            id: Date.now().toString(),
            content: chats[index - 1].content,
            loading: false,
            created: Date.now(),
            updateAt: Date.now(),
        };
        setContent(prevMessage.content)
        const aiMessage: ChatMessage = {
            role: 'assistant',
            id: Date.now().toString(),
            content: '',
            loading: true,
            created: Date.now(),
            updateAt: Date.now(),
        };
        const temp = [prevMessage, aiMessage];
        setChats?.((prev: ChatMessage[]) => [...(prev || []), ...temp]);
        setTimeout(scrollToBottom, 50);
        setContent('');
        try {
            const response = await request([...(chats || []), prevMessage]);
            if (!response?.body) return;
            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let partial = '';
            let currentContent = '';
            while (true) {
                const {done, value} = await reader.read();
                if (done) break;
                partial += decoder.decode(value, {stream: true});

                const id = setInterval(() => {
                    currentContent = partial.slice(0, currentContent.length + 2);
                    setChats?.((prev: ChatMessage[]) => {
                        const newChats = [...(prev || [])];
                        const lastMsg = newChats[newChats.length - 1];
                        if (lastMsg.role === 'assistant') {
                            lastMsg.loading = false;
                            lastMsg.content = currentContent;
                            lastMsg.updateAt = Date.now();
                        }
                        return newChats;
                    });
                    if (currentContent === partial) {
                        clearInterval(id);
                    }
                }, 50);



            }
        } catch (error) {
            console.error('Error:', error);
        } finally {
        }


    };

    // 渲染消息列表
    const renderMessages = () => {
        if (!chats?.length) {
            return (
                <Welcome
                    variant="borderless"
                    icon={<img
                        src={botAvatar}
                        style={{ width: 32, height: 32, borderRadius: '50%' }}
                        alt="AI Avatar"
                    />}
                    description={helloMessage}
                    styles={{icon: {width: 40, height: 40}}}
                />
            );
        }
        return (
            <Bubble.List
                autoScroll={true}
                items={chats.map((chat, index) => ({
                    key: chat.id + Math.random().toString(),
                    typing: {suffix: <>💗</>},
                    header: (
                        <Space>
                            {new Date(chat.created).toLocaleString()}
                        </Space>
                    ),
                    loading: chat.loading,
                    loadingRender: () => (
                        <Space>
                            <Spin size="small"/>
                            AI正在思考中...
                        </Space>
                    ),
                    footer: (
                        <Space>
                            {(chat.role === 'assistant') && (<Button
                                color="default"
                                variant="text"
                                size="small"
                                icon={<SyncOutlined/>}
                                onClick={() => {
                                    // 点击按钮时重新生成该消息
                                    if (chat.role === 'assistant') {
                                        handleRegenerate(index);
                                    }
                                }}
                            />)}

                            <Button
                                color="default"
                                variant="text"
                                size="small"
                                icon={<CopyOutlined/>}
                                onClick={async () => {
                                    try {
                                        await navigator.clipboard.writeText(chat.content);
                                        message.success('复制成功');
                                    } catch (error) {
                                        message.error('复制失败');
                                    }
                                }}
                            />
                        </Space>
                    ),
                    role: chat.role === 'user' ? 'local' : 'ai',
                    content: (
                        <div>
                            {chat.role === 'assistant' ? (
                                <ReactMarkdown remarkPlugins={[remarkGfm, remarkBreaks]}>
                                    {chat.content}
                                </ReactMarkdown>
                            ) : chat.content}
                        </div>
                    ),
                    avatar: chat.role === 'assistant' ? (
                        <img
                            src={botAvatar}
                            style={{ width: 32, height: 32, borderRadius: '50%' }}
                            alt="AI Avatar"
                        />
                    ) : { icon: <UserOutlined />, style: fooAvatar },
                }))}
                roles={{ai: {placement: 'start'}, local: {placement: 'end'}}}
            />
        );
    };
    const SENDER_PROMPTS = prompts || [
        {
            key: '1',
            description: '你好'
        },
        {
            key: '2',
            description: '你是谁？'
        }
    ];

    return (
        <div
            style={{
                width: '100%',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                background: '#fff',
                border: '1px solid #f3f3f3',
                ...appStyle,
                ...style,
            }}
        >
            {/* 消息容器 */}
            <div
                ref={messagesContainerRef}
                style={{
                    flex: 1,
                    overflowY: 'auto',
                    padding: '16px',
                    scrollbarWidth: 'none',
                }}
            >
                {loading ? (
                    <Spin tip="加载中..."/>
                ) : (
                    <>
                        {renderMessages()}
                        <div ref={messagesEndRef}/>
                        {/* 锚点元素 */}
                    </>
                )}
            </div>

            {/* 输入区域 */}
            <div
                style={{
                    borderTop: '1px solid #eee',
                    padding: '12px',
                    display: 'flex',
                    flexDirection: "column",
                    gap: '8px',
                }}
            >
                {/* 🌟 提示词 */}
                <Prompts
                    items={SENDER_PROMPTS}
                    onItemClick={(info) => {
                        handleSubmit(info.data.description as string)
                    }}
                    styles={{
                        item: { padding: '6px 12px' },
                    }}

                />
                <Sender
                    value={content}
                    onChange={setContent}
                    onSubmit={handleSubmit}
                    loading={sendLoading || isStreaming}
                    disabled={inputDisabled}
                    actions={(_, info) => (
                        <Space size="small">
                            <info.components.ClearButton
                                disabled={inputDisabled}  // 强制不禁用
                                title="删除对话记录"
                                style={{ fontSize: 20 }}
                                onClick={(e) => {
                                    e.preventDefault();  // 阻止默认行为（如果有）
                                    clearMessage?.();
                                }}
                            />
                            <info.components.SendButton
                                type="primary"
                                disabled={inputDisabled}
                                icon={<OpenAIOutlined/>}
                                loading={sendLoading || isStreaming}
                            />
                        </Space>
                    )}
                />
            </div>
        </div>
    );
};
