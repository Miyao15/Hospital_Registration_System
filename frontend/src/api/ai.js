import request from '@/utils/request';

/**
 * AI聊天接口
 * @param {string} message - 用户消息
 * @param {Array} history - 历史消息记录
 * @returns {Promise} - 返回AI回复
 */
export function sendAIMessage(message, history = []) {
  return request.post('/api/ai/chat', {
    message,
    history
  });
}
