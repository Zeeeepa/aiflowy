import type { BotInfo, Message, RequestResult, Session } from '@aiflowy/types';

import { api } from '#/api/request.js';

/** 获取bot详情 */
export const getBotDetails = (id: string) => {
  return api.get<RequestResult<BotInfo>>('/api/v1/aiBot/getDetail', {
    params: { id },
  });
};

export interface GetSessionListParams {
  botId: string;
  tempUserId: string;
}
/** 获取bot对话列表 */
export const getSessionList = (params: GetSessionListParams) => {
  return api.get<RequestResult<{ cons: Session[] }>>(
    '/api/v1/conversation/externalList',
    { params },
  );
};

export interface GetMessageListParams {
  sessionId: string;
  botId: string;
  isExternalMsg: number;
  tempUserId: string;
}
/** 获取单个对话的信息列表 */
export const getMessageList = (params: GetMessageListParams) => {
  return api.get<RequestResult<Message[]>>('/api/v1/aiBotMessage/messageList', {
    params,
  });
};
