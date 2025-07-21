package tech.aiflowy.ai.entity.openAi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 讯飞星火大模型响应处理类
 */
public class SparkResponse {

    /**
     * 响应头信息
     */
    private Header header;

    /**
     * 响应载荷
     */
    private Payload payload;

    /**
     * 响应头类
     */
    public static class Header {
        /**
         * 错误码，0表示成功
         */
        private Integer code;

        /**
         * 错误描述
         */
        private String message;

        /**
         * 会话ID
         */
        private String sid;

        /**
         * 状态：0-首个结果，1-中间结果，2-最后一个结果
         */
        private Integer status;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }

    /**
     * 响应载荷类
     */
    public static class Payload {
        /**
         * 选择项
         */
        private Choices choices;

        /**
         * 使用统计
         */
        private Usage usage;

        /**
         * 选择项类
         */
        public static class Choices {
            /**
             * 状态：0-首个结果，1-中间结果，2-最后一个结果
             */
            private Integer status;

            /**
             * 序号
             */
            private Integer seq;

            /**
             * 文本内容列表
             */
            private List<Text> text;

            /**
             * 文本内容类
             */
            public static class Text {
                /**
                 * 内容
                 */
                private String content;

                /**
                 * 角色：user, assistant
                 */
                private String role;

                /**
                 * 索引
                 */
                private Integer index;

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getRole() {
                    return role;
                }

                public void setRole(String role) {
                    this.role = role;
                }

                public Integer getIndex() {
                    return index;
                }

                public void setIndex(Integer index) {
                    this.index = index;
                }
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public Integer getSeq() {
                return seq;
            }

            public void setSeq(Integer seq) {
                this.seq = seq;
            }

            public List<Text> getText() {
                return text;
            }

            public void setText(List<Text> text) {
                this.text = text;
            }
        }

        /**
         * 使用统计类
         */
        public static class Usage {
            /**
             * 文本统计
             */
            private Text text;

            /**
             * 文本统计类
             */
            public static class Text {
                /**
                 * 问题token数
                 */
                @JsonProperty("question_tokens")
                private Integer questionTokens;

                /**
                 * 提示词token数
                 */
                @JsonProperty("prompt_tokens")
                private Integer promptTokens;

                /**
                 * 完成token数
                 */
                @JsonProperty("completion_tokens")
                private Integer completionTokens;

                /**
                 * 总token数
                 */
                @JsonProperty("total_tokens")
                private Integer totalTokens;

                public Integer getQuestionTokens() {
                    return questionTokens;
                }

                public void setQuestionTokens(Integer questionTokens) {
                    this.questionTokens = questionTokens;
                }

                public Integer getPromptTokens() {
                    return promptTokens;
                }

                public void setPromptTokens(Integer promptTokens) {
                    this.promptTokens = promptTokens;
                }

                public Integer getCompletionTokens() {
                    return completionTokens;
                }

                public void setCompletionTokens(Integer completionTokens) {
                    this.completionTokens = completionTokens;
                }

                public Integer getTotalTokens() {
                    return totalTokens;
                }

                public void setTotalTokens(Integer totalTokens) {
                    this.totalTokens = totalTokens;
                }
            }

            public Text getText() {
                return text;
            }

            public void setText(Text text) {
                this.text = text;
            }
        }

        public Choices getChoices() {
            return choices;
        }

        public void setChoices(Choices choices) {
            this.choices = choices;
        }

        public Usage getUsage() {
            return usage;
        }

        public void setUsage(Usage usage) {
            this.usage = usage;
        }
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}