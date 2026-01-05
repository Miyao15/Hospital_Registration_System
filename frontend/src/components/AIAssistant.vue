<template>
  <div class="ai-assistant-container">
    <!-- 悬浮按钮 -->
    <div 
      class="ai-float-btn" 
      :class="{ 'is-open': isOpen }"
      @click="toggleChat"
    >
      <div v-if="!isOpen" class="avatar-simple">
        <!-- 简约AI图标 -->
        <svg viewBox="0 0 100 100" class="ai-icon">
          <circle cx="50" cy="50" r="45" fill="#2A2A2A"/>
          <circle cx="35" cy="40" r="5" fill="white"/>
          <circle cx="65" cy="40" r="5" fill="white"/>
          <path d="M35 60 Q50 70 65 60" stroke="white" stroke-width="3" fill="none" stroke-linecap="round"/>
        </svg>
      </div>
      <!-- AI标识徽章 -->
      <div class="ai-badge" v-if="!isOpen">
        <span>AI</span>
      </div>
      <span class="close-icon" v-if="isOpen">×</span>
    </div>

    <!-- 聊天窗口 -->
    <transition name="chat-slide">
      <div class="chat-window" v-if="isOpen">
        <div class="chat-header">
          <div class="header-avatar">
            <svg viewBox="0 0 100 100" class="ai-icon-small">
              <circle cx="50" cy="50" r="45" fill="#2A2A2A"/>
              <circle cx="35" cy="40" r="5" fill="white"/>
              <circle cx="65" cy="40" r="5" fill="white"/>
              <path d="M35 60 Q50 70 65 60" stroke="white" stroke-width="3" fill="none" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="header-info">
            <span class="assistant-name">AI助手</span>
            <span class="assistant-status">● 在线为您服务</span>
          </div>
        </div>
        
        <div class="chat-messages" ref="messagesContainer">
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            class="message"
            :class="msg.type"
          >
            <div class="message-avatar" v-if="msg.type === 'bot'">
              <svg viewBox="0 0 100 100" class="ai-icon-msg">
                <circle cx="50" cy="50" r="45" fill="#2A2A2A"/>
                <circle cx="35" cy="40" r="5" fill="white"/>
                <circle cx="65" cy="40" r="5" fill="white"/>
                <path d="M35 60 Q50 70 65 60" stroke="white" stroke-width="3" fill="none" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="message-content">
              <p>{{ msg.text }}</p>
              <span class="message-time">{{ msg.time }}</span>
            </div>
          </div>
          <div class="typing-indicator" v-if="isTyping">
            <span></span><span></span><span></span>
          </div>
        </div>

        <div class="quick-questions">
          <button 
            v-for="(q, i) in quickQuestions" 
            :key="i" 
            @click="askQuestion(q)"
            class="quick-btn"
          >
            {{ q }}
          </button>
        </div>

        <div class="chat-input">
          <input 
            v-model="inputText" 
            @keyup.enter="sendMessage"
            placeholder="输入您的问题..."
            :disabled="isTyping"
          />
          <button @click="sendMessage" :disabled="!inputText.trim() || isTyping">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
            </svg>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { sendAIMessage } from '@/api/ai';

const isOpen = ref(false);
const inputText = ref('');
const isTyping = ref(false);
const messagesContainer = ref(null);

const messages = ref([
  {
    type: 'bot',
    text: '你好！我是AI助手，有什么可以帮助您的吗？',
    time: getCurrentTime()
  }
]);

const quickQuestions = [
  '如何预约挂号？',
  '怎么查看预约？',
  '如何取消预约？',
  '有哪些科室？'
];

function getCurrentTime() {
  const now = new Date();
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
}

function toggleChat() {
  isOpen.value = !isOpen.value;
}

// 构建历史消息用于API调用
function buildHistory() {
  const history = [];
  // 只取最近10条消息作为上下文
  const recentMessages = messages.value.slice(-10);
  for (const msg of recentMessages) {
    if (msg.type === 'user') {
      history.push({ role: 'user', content: msg.text });
    } else if (msg.type === 'bot') {
      history.push({ role: 'assistant', content: msg.text });
    }
  }
  return history;
}

async function sendMessage() {
  if (!inputText.value.trim() || isTyping.value) return;
  
  const userMessage = inputText.value.trim();
  messages.value.push({
    type: 'user',
    text: userMessage,
    time: getCurrentTime()
  });
  
  inputText.value = '';
  scrollToBottom();
  
  isTyping.value = true;
  
  try {
    // 调用真实的AI API
    const history = buildHistory();
    // 移除最后一条用户消息，因为会单独传递
    history.pop();
    
    const reply = await sendAIMessage(userMessage, history);
    
    // reply 直接就是AI回复的字符串（因为后端返回 { success: true, data: reply }，前端拦截器返回 data）
    if (reply) {
      messages.value.push({
        type: 'bot',
        text: reply,
        time: getCurrentTime()
      });
    } else {
      messages.value.push({
        type: 'bot',
        text: '抱歉，我暂时无法回答，请稍后再试~ 💦',
        time: getCurrentTime()
      });
    }
  } catch (error) {
    console.error('AI chat error:', error);
    // 如果API调用失败，使用本地回复
    const reply = getLocalReply(userMessage);
    messages.value.push({
      type: 'bot',
      text: reply,
      time: getCurrentTime()
    });
  } finally {
    isTyping.value = false;
    scrollToBottom();
  }
}

function askQuestion(question) {
  inputText.value = question;
  sendMessage();
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
}

// 本地备用回复（当API不可用时）
function getLocalReply(question) {
  const q = question.toLowerCase();
  
  if (q.includes('预约') && q.includes('挂号')) {
    return '预约挂号很简单哦~ ✨\n1. 在首页搜索科室或医生\n2. 选择合适的时间段\n3. 填写就诊信息\n4. 确认预约即可！';
  }
  if (q.includes('查看') && q.includes('预约')) {
    return '查看预约的方法：\n登录后点击右上角头像，进入"我的预约"页面就能看到所有预约记录啦~ 💕';
  }
  if (q.includes('取消') && q.includes('预约')) {
    return '取消预约步骤：\n1. 进入"我的预约"\n2. 找到要取消的预约\n3. 点击"取消预约"按钮\n注意：请提前24小时取消哦~ 🌸';
  }
  if (q.includes('科室')) {
    return '我们医院有很多科室呢~\n包括：心血管内科、骨科、皮肤科、儿科、眼科、口腔科、妇科等 🏥\n点击首页的"查看科室"可以看到全部哦！';
  }
  if (q.includes('医生') || q.includes('专家')) {
    return '我们有很多优秀的医生~ 👨‍⚕️\n您可以在首页搜索医生姓名，或者按科室浏览医生列表，还能看到患者评价呢！';
  }
  if (q.includes('时间') || q.includes('工作')) {
    return '医院门诊时间：\n周一至周五 8:00-17:00\n周六周日 8:00-12:00\n具体医生排班请查看预约页面哦~ ⏰';
  }
  if (q.includes('你好') || q.includes('在吗')) {
    return '在的在的~ 我一直在这里等你呢！有什么问题尽管问我吧 💖';
  }
  
  return '网络好像有点问题呢~ 🤔\n您可以稍后再试，或者直接联系客服电话：022-60362255 哦！';
}
</script>


<style scoped>
/* ========== HarmonyOS Style Variables ========== */
:root {
  --harmony-gradient-start: #FFD300;
  --harmony-gradient-end: #FF9800;
  --harmony-text-gray: #9CA3AF;
  --harmony-accent: #FFD300;
}

.ai-assistant-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 悬浮按钮 - HarmonyOS风格 */
.ai-float-btn {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  border-radius: 50%;
  box-shadow: 0 8px 30px rgba(255, 211, 0, 0.4);
  border: none;
  animation: subtlePulse 3s ease-in-out infinite;
}

@keyframes subtlePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}

.ai-float-btn:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 40px rgba(255, 211, 0, 0.6);
  animation: none;
}

.ai-float-btn:active {
  transform: translateY(-2px) scale(1.02);
}

.ai-float-btn.is-open {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  animation: none;
}

/* 头像容器 */
.avatar-simple {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-icon {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.ai-icon circle:first-child {
  fill: #2A2A2A;
}

.ai-icon-small {
  width: 40px;
  height: 40px;
}

.ai-icon-msg {
  width: 100%;
  height: 100%;
}

.close-icon {
  font-size: 32px;
  color: #2A2A2A;
  font-weight: 300;
  transition: transform 0.3s ease;
}

.ai-float-btn:hover .close-icon {
  transform: rotate(90deg);
}

/* AI标识徽章 */
.ai-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 4px 8px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
  border: 2px solid #FFF9E5;
  letter-spacing: 0.5px;
}

/* 聊天窗口 - HarmonyOS风格 */
.chat-window {
  position: absolute;
  bottom: 84px;
  right: 0;
  width: 380px;
  height: 520px;
  background: linear-gradient(180deg, #FFF9E5 0%, #FFFDF5 50%, #FFF9E5 100%);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(255, 211, 0, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(255, 211, 0, 0.3);
  backdrop-filter: blur(20px);
}

.chat-header {
  background: rgba(255, 249, 229, 0.8);
  backdrop-filter: blur(20px);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 14px;
  border-bottom: 1px solid rgba(255, 211, 0, 0.2);
  position: relative;
}

.chat-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
}

.header-avatar {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(255, 211, 0, 0.3);
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.assistant-name {
  color: #2A2A2A;
  font-weight: 700;
  font-size: 18px;
}

.assistant-status {
  color: #666;
  font-size: 13px;
  font-weight: 500;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background: transparent;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  animation: fadeInMessage 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeInMessage {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  box-shadow: 0 4px 12px rgba(255, 211, 0, 0.3);
}

.message-content {
  max-width: 70%;
  padding: 14px 18px;
  border-radius: 16px;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message.bot .message-content {
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.15), rgba(255, 152, 0, 0.1));
  border: 1px solid rgba(255, 211, 0, 0.2);
  color: #2A2A2A;
}

.message.user .message-content {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  color: #2A2A2A;
  border: 1px solid rgba(255, 211, 0, 0.3);
}

.message-content p {
  margin: 0;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-line;
  font-weight: 500;
}

.message-time {
  font-size: 11px;
  opacity: 0.7;
  display: block;
  margin-top: 6px;
  font-weight: 500;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 14px 18px;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.15), rgba(255, 152, 0, 0.1));
  border: 1px solid rgba(255, 211, 0, 0.2);
  border-radius: 16px;
  width: fit-content;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: var(--harmony-gradient-start);
  border-radius: 50%;
  animation: typingBounce 1.4s infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.5; }
  30% { transform: translateY(-8px); opacity: 1; }
}

/* 快捷问题 */
.quick-questions {
  padding: 16px 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  background: rgba(255, 249, 229, 0.6);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 211, 0, 0.2);
}

.quick-btn {
  padding: 10px 18px;
  background: rgba(255, 249, 229, 0.8);
  border: 1px solid rgba(255, 211, 0, 0.3);
  border-radius: 20px;
  font-size: 13px;
  color: #2A2A2A;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 6px rgba(255, 211, 0, 0.1);
}

.quick-btn:hover {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border-color: transparent;
  color: #2A2A2A;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 211, 0, 0.3);
}

.quick-btn:active {
  transform: translateY(0);
}

/* 输入区域 */
.chat-input {
  padding: 16px 20px;
  display: flex;
  gap: 12px;
  background: rgba(255, 249, 229, 0.8);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 211, 0, 0.2);
}

.chat-input input {
  flex: 1;
  padding: 14px 20px;
  border: 1px solid rgba(255, 211, 0, 0.3);
  border-radius: 28px;
  outline: none;
  font-size: 15px;
  font-weight: 500;
  background: rgba(255, 249, 229, 0.6);
  color: #2A2A2A;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-input input::placeholder {
  color: rgba(156, 163, 175, 0.6);
}

.chat-input input:focus {
  border-color: var(--harmony-gradient-start);
  background: rgba(255, 249, 229, 0.9);
  box-shadow: 0 0 0 4px rgba(255, 211, 0, 0.1);
}

.chat-input button {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(255, 211, 0, 0.4);
  position: relative;
  overflow: hidden;
}

.chat-input button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.chat-input button:hover:not(:disabled)::before {
  left: 100%;
}

.chat-input button:hover:not(:disabled) {
  transform: scale(1.08) rotate(5deg);
  box-shadow: 0 6px 20px rgba(255, 211, 0, 0.6);
}

.chat-input button:active:not(:disabled) {
  transform: scale(1.02);
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.chat-input button svg {
  width: 20px;
  height: 20px;
  color: #2A2A2A;
  position: relative;
  z-index: 1;
}

/* 动画 */
.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(24px) scale(0.95);
}

/* 滚动条美化 - HarmonyOS风格 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 211, 0, 0.3);
  border-radius: 3px;
  transition: background 0.3s ease;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 211, 0, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-window {
    width: calc(100vw - 40px);
    max-width: 380px;
  }
}

@media (max-width: 480px) {
  .ai-assistant-container {
    bottom: 20px;
    right: 20px;
  }
  
  .ai-float-btn {
    width: 56px;
    height: 56px;
  }
  
  .chat-window {
    bottom: 76px;
    width: calc(100vw - 40px);
    height: 480px;
  }
  
  .chat-messages {
    padding: 16px 20px;
  }
  
  .message-content {
    max-width: 80%;
  }
}
</style>
