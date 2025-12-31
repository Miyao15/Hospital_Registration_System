<template>
  <div class="ai-assistant-container">
    <!-- 悬浮按钮 -->
    <div 
      class="ai-float-btn" 
      :class="{ 'is-open': isOpen }"
      @click="toggleChat"
    >
      <div v-if="!isOpen" class="avatar-3d">
        <!-- 3D粉色动漫女孩头像 SVG -->
        <svg viewBox="0 0 100 100" class="fairy-svg-3d">
          <defs>
            <linearGradient id="hairGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#FFB6C1"/>
              <stop offset="50%" style="stop-color:#FF69B4"/>
              <stop offset="100%" style="stop-color:#FF1493"/>
            </linearGradient>
            <radialGradient id="faceGradient" cx="40%" cy="30%" r="60%">
              <stop offset="0%" style="stop-color:#FFF5EE"/>
              <stop offset="100%" style="stop-color:#FFE4E1"/>
            </radialGradient>
            <clipPath id="circleClip">
              <circle cx="50" cy="50" r="48"/>
            </clipPath>
          </defs>
          <g clip-path="url(#circleClip)">
            <!-- 背景 -->
            <circle cx="50" cy="50" r="50" fill="#FFF0F5"/>
            <!-- 头发背景 -->
            <ellipse cx="50" cy="58" rx="42" ry="45" fill="url(#hairGradient)"/>
            <!-- 脸 -->
            <ellipse cx="50" cy="52" rx="30" ry="32" fill="url(#faceGradient)"/>
            <!-- 腮红 -->
            <ellipse cx="30" cy="58" rx="7" ry="4" fill="#FFB6C1" opacity="0.5"/>
            <ellipse cx="70" cy="58" rx="7" ry="4" fill="#FFB6C1" opacity="0.5"/>
            <!-- 眼睛 -->
            <ellipse cx="38" cy="50" rx="7" ry="8" fill="#fff"/>
            <ellipse cx="62" cy="50" rx="7" ry="8" fill="#fff"/>
            <circle cx="39" cy="51" r="5" fill="#6B4423"/>
            <circle cx="63" cy="51" r="5" fill="#6B4423"/>
            <circle cx="41" cy="49" r="2" fill="#fff"/>
            <circle cx="65" cy="49" r="2" fill="#fff"/>
            <!-- 眉毛 -->
            <path d="M30 42 Q38 38 46 42" stroke="#A0522D" stroke-width="2" fill="none" stroke-linecap="round"/>
            <path d="M54 42 Q62 38 70 42" stroke="#A0522D" stroke-width="2" fill="none" stroke-linecap="round"/>
            <!-- 嘴巴 - 微笑 -->
            <path d="M42 66 Q50 72 58 66" stroke="#FF69B4" stroke-width="2.5" fill="none" stroke-linecap="round"/>
            <!-- 刘海 -->
            <path d="M12 40 Q25 15 50 18 Q75 15 88 40 Q80 32 68 35 Q58 22 50 28 Q42 22 32 35 Q20 32 12 40" fill="url(#hairGradient)"/>
            <!-- 头发高光 -->
            <path d="M28 28 Q38 18 48 24" stroke="rgba(255,255,255,0.5)" stroke-width="3" fill="none" stroke-linecap="round"/>
            <!-- 蝴蝶结 -->
            <ellipse cx="78" cy="28" rx="12" ry="8" fill="#FF1493"/>
            <ellipse cx="78" cy="28" rx="8" ry="5" fill="#FF69B4"/>
            <circle cx="78" cy="28" r="4" fill="#FFB6C1"/>
          </g>
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
            <svg viewBox="0 0 100 100" class="header-fairy-svg">
              <defs>
                <linearGradient id="hairGradient2" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#FFB6C1"/>
                  <stop offset="100%" style="stop-color:#FF69B4"/>
                </linearGradient>
              </defs>
              <circle cx="50" cy="50" r="48" fill="#FFF0F5"/>
              <ellipse cx="50" cy="58" rx="40" ry="42" fill="url(#hairGradient2)"/>
              <ellipse cx="50" cy="52" rx="28" ry="30" fill="#FFE4E1"/>
              <ellipse cx="32" cy="58" rx="6" ry="4" fill="#FFB6C1" opacity="0.5"/>
              <ellipse cx="68" cy="58" rx="6" ry="4" fill="#FFB6C1" opacity="0.5"/>
              <ellipse cx="38" cy="50" rx="6" ry="7" fill="#fff"/>
              <ellipse cx="62" cy="50" rx="6" ry="7" fill="#fff"/>
              <circle cx="39" cy="51" r="4" fill="#6B4423"/>
              <circle cx="63" cy="51" r="4" fill="#6B4423"/>
              <circle cx="40" cy="49" r="1.5" fill="#fff"/>
              <circle cx="64" cy="49" r="1.5" fill="#fff"/>
              <path d="M42 66 Q50 71 58 66" stroke="#FF69B4" stroke-width="2" fill="none"/>
              <path d="M15 40 Q28 18 50 20 Q72 18 85 40 Q78 33 65 36 Q55 24 50 30 Q45 24 35 36 Q22 33 15 40" fill="url(#hairGradient2)"/>
            </svg>
          </div>
          <div class="header-info">
            <span class="assistant-name">小樱 · AI助手</span>
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
              <svg viewBox="0 0 100 100" class="msg-fairy-svg">
                <defs>
                  <linearGradient id="hairGradient3" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#FFB6C1"/>
                    <stop offset="100%" style="stop-color:#FF69B4"/>
                  </linearGradient>
                </defs>
                <circle cx="50" cy="50" r="48" fill="#FFF0F5"/>
                <ellipse cx="50" cy="58" rx="38" ry="40" fill="url(#hairGradient3)"/>
                <ellipse cx="50" cy="52" rx="26" ry="28" fill="#FFE4E1"/>
                <ellipse cx="34" cy="56" rx="5" ry="3" fill="#FFB6C1" opacity="0.5"/>
                <ellipse cx="66" cy="56" rx="5" ry="3" fill="#FFB6C1" opacity="0.5"/>
                <ellipse cx="40" cy="50" rx="5" ry="6" fill="#fff"/>
                <ellipse cx="60" cy="50" rx="5" ry="6" fill="#fff"/>
                <circle cx="41" cy="51" r="3" fill="#6B4423"/>
                <circle cx="61" cy="51" r="3" fill="#6B4423"/>
                <circle cx="42" cy="49" r="1" fill="#fff"/>
                <circle cx="62" cy="49" r="1" fill="#fff"/>
                <path d="M44 64 Q50 68 56 64" stroke="#FF69B4" stroke-width="2" fill="none"/>
                <path d="M18 40 Q30 20 50 22 Q70 20 82 40 Q75 34 65 36 Q55 26 50 30 Q45 26 35 36 Q25 34 18 40" fill="url(#hairGradient3)"/>
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
    text: '你好呀~ 我是小樱，您的专属医疗助手 💕\n有什么可以帮助您的吗？',
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

function handleImageError(e) {
  // 图片加载失败时使用备用图片
  e.target.src = 'data:image/svg+xml,' + encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">
      <circle cx="50" cy="50" r="48" fill="#FFB6C1"/>
      <circle cx="50" cy="40" r="20" fill="#FFE4E1"/>
      <ellipse cx="50" cy="70" rx="18" ry="22" fill="#FFB6C1"/>
      <circle cx="43" cy="38" r="3" fill="#333"/>
      <circle cx="57" cy="38" r="3" fill="#333"/>
      <path d="M45 48 Q50 52 55 48" stroke="#FF69B4" stroke-width="2" fill="none"/>
    </svg>
  `);
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
.ai-assistant-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 悬浮按钮 - 3D立体效果 */
.ai-float-btn {
  width: 75px;
  height: 75px;
  border-radius: 50%;
  background: 
    radial-gradient(ellipse at 30% 20%, rgba(255, 255, 255, 0.8) 0%, transparent 50%),
    radial-gradient(ellipse at 70% 80%, rgba(200, 50, 100, 0.3) 0%, transparent 40%),
    linear-gradient(160deg, #FFB6C1 0%, #FF69B4 40%, #FF1493 100%);
  box-shadow: 
    0 8px 32px rgba(255, 105, 180, 0.5),
    0 4px 16px rgba(255, 20, 147, 0.3),
    inset 0 2px 4px rgba(255, 255, 255, 0.6),
    inset 0 -3px 6px rgba(200, 50, 100, 0.3);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: visible;
  transform-style: preserve-3d;
  perspective: 500px;
}

/* 3D光环效果 */
.ai-float-btn::before {
  content: '';
  position: absolute;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 182, 193, 0.4) 0%, transparent 70%);
  animation: pulse3d 2s ease-in-out infinite;
  z-index: -1;
}

/* 底部阴影层 - 增加立体感 */
.ai-float-btn::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 12px;
  background: radial-gradient(ellipse, rgba(255, 105, 180, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(4px);
  z-index: -2;
}

@keyframes pulse3d {
  0%, 100% { 
    transform: scale(1);
    opacity: 0.6;
  }
  50% { 
    transform: scale(1.15);
    opacity: 0.3;
  }
}

.ai-float-btn:hover {
  transform: scale(1.1) translateY(-4px) rotateX(10deg);
  box-shadow: 
    0 16px 48px rgba(255, 105, 180, 0.6),
    0 8px 24px rgba(255, 20, 147, 0.4),
    inset 0 2px 4px rgba(255, 255, 255, 0.8),
    inset 0 -3px 6px rgba(200, 50, 100, 0.4);
}

.ai-float-btn:hover::after {
  bottom: -14px;
  width: 50px;
  opacity: 0.6;
}

.ai-float-btn:active {
  transform: scale(1.05) translateY(-2px);
  box-shadow: 
    0 6px 20px rgba(255, 105, 180, 0.5),
    0 3px 10px rgba(255, 20, 147, 0.3),
    inset 0 1px 2px rgba(255, 255, 255, 0.5),
    inset 0 -2px 4px rgba(200, 50, 100, 0.4);
}

.ai-float-btn.is-open {
  background: 
    radial-gradient(ellipse at 30% 20%, rgba(255, 255, 255, 0.7) 0%, transparent 50%),
    radial-gradient(ellipse at 70% 80%, rgba(150, 0, 80, 0.3) 0%, transparent 40%),
    linear-gradient(160deg, #FF69B4 0%, #FF1493 40%, #C71585 100%);
  animation: none;
}

.ai-float-btn.is-open::before {
  animation: none;
  opacity: 0;
}

.fairy-avatar {
  width: 45px;
  height: 45px;
}

/* 3D头像容器 */
.avatar-3d {
  width: 65px;
  height: 65px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #FFF5EE, #FFE4E1);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 
    0 4px 12px rgba(0, 0, 0, 0.15),
    inset 0 2px 4px rgba(255, 255, 255, 0.8),
    inset 0 -2px 4px rgba(255, 105, 180, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.7);
  overflow: visible;
}

.fairy-svg-3d {
  width: 60px;
  height: 60px;
  filter: drop-shadow(0 2px 3px rgba(255, 105, 180, 0.3));
  animation: float3d 3s ease-in-out infinite;
}

@keyframes float3d {
  0%, 100% { 
    transform: translateY(0) rotateY(0deg);
  }
  25% {
    transform: translateY(-3px) rotateY(5deg);
  }
  50% { 
    transform: translateY(-5px) rotateY(0deg);
  }
  75% {
    transform: translateY(-3px) rotateY(-5deg);
  }
}

.fairy-avatar-img {
  width: 58px;
  height: 58px;
  border-radius: 50%;
  object-fit: cover;
  animation: float3d 3s ease-in-out infinite;
  border: 2px solid rgba(255, 255, 255, 0.7);
  box-shadow: 
    0 4px 12px rgba(0, 0, 0, 0.15),
    inset 0 -2px 4px rgba(0, 0, 0, 0.1);
  filter: drop-shadow(0 2px 4px rgba(255, 105, 180, 0.3));
}

.fairy-svg {
  width: 100%;
  height: 100%;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

.float-btn-text {
  font-size: 11px;
  color: #fff;
  font-weight: 600;
  margin-top: -5px;
}

.close-icon {
  font-size: 32px;
  color: #fff;
  font-weight: 300;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  animation: rotateIn 0.3s ease-out;
}

@keyframes rotateIn {
  from {
    transform: rotate(-90deg) scale(0.5);
    opacity: 0;
  }
  to {
    transform: rotate(0deg) scale(1);
    opacity: 1;
  }
}

/* AI标识徽章 */
.ai-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  background: #fff;
  color: #FF69B4;
  font-size: 10px;
  font-weight: 700;
  padding: 3px 6px;
  border-radius: 8px;
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  border: 2px solid #FF69B4;
  letter-spacing: 0.5px;
  animation: badgePulse 2s ease-in-out infinite;
  z-index: 10;
}

.ai-badge span {
  display: block;
  text-shadow: none;
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 
      0 2px 8px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 
      0 4px 12px rgba(255, 105, 180, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
}

/* 聊天窗口 - 3D立体效果 */
.chat-window {
  position: absolute;
  bottom: 90px;
  right: 0;
  width: 360px;
  height: 500px;
  background: linear-gradient(180deg, #ffffff 0%, #FFF9FC 100%);
  border-radius: 24px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 8px 24px rgba(255, 105, 180, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(255, 182, 193, 0.3);
  transform-origin: bottom right;
}

.chat-header {
  background: 
    linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 50%),
    linear-gradient(135deg, #FFB6C1 0%, #FF69B4 60%, #FF1493 100%);
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 
    0 4px 12px rgba(255, 105, 180, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.header-avatar {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: 
    0 4px 12px rgba(0, 0, 0, 0.1),
    inset 0 2px 4px rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.header-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.header-fairy-svg {
  width: 42px;
  height: 42px;
}

.mini-fairy {
  width: 35px;
  height: 35px;
}

.header-info {
  display: flex;
  flex-direction: column;
}

.assistant-name {
  color: #fff;
  font-weight: 600;
  font-size: 16px;
}

.assistant-status {
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #FFF5F7;
}

.message {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #FFF5EE 0%, #FFE4E1 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.2);
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.msg-fairy-svg {
  width: 32px;
  height: 32px;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
}

.message.bot .message-content {
  background: #fff;
  border-bottom-left-radius: 4px;
  box-shadow: 
    0 4px 12px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 182, 193, 0.2);
}

.message.user .message-content {
  background: 
    linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
    linear-gradient(135deg, #FF69B4 0%, #FF1493 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
  box-shadow: 
    0 4px 12px rgba(255, 105, 180, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.message-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-line;
}

.message-time {
  font-size: 10px;
  opacity: 0.6;
  display: block;
  margin-top: 4px;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 18px;
  width: fit-content;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #FF69B4;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-8px); opacity: 1; }
}

/* 快捷问题 */
.quick-questions {
  padding: 12px 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  background: #fff;
  border-top: 1px solid #FFE4E9;
}

.quick-btn {
  padding: 8px 14px;
  background: linear-gradient(180deg, #FFF5F7 0%, #FFE8ED 100%);
  border: 1px solid #FFB6C1;
  border-radius: 18px;
  font-size: 12px;
  color: #FF69B4;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 
    0 2px 6px rgba(255, 182, 193, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.quick-btn:hover {
  background: linear-gradient(135deg, #FF69B4 0%, #FF1493 100%);
  color: #fff;
  transform: translateY(-2px) scale(1.02);
  box-shadow: 
    0 4px 12px rgba(255, 105, 180, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  border-color: transparent;
}

.quick-btn:active {
  transform: translateY(0) scale(1);
}

/* 输入区域 */
.chat-input {
  padding: 12px 16px;
  display: flex;
  gap: 10px;
  background: #fff;
  border-top: 1px solid #FFE4E9;
}

.chat-input input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #FFB6C1;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
  transition: all 0.2s;
}

.chat-input input:focus {
  border-color: #FF69B4;
  box-shadow: 0 0 0 3px rgba(255, 105, 180, 0.1);
}

.chat-input button {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: 
    radial-gradient(ellipse at 30% 20%, rgba(255, 255, 255, 0.3) 0%, transparent 50%),
    linear-gradient(135deg, #FF69B4 0%, #FF1493 100%);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 
    0 4px 12px rgba(255, 105, 180, 0.4),
    inset 0 1px 2px rgba(255, 255, 255, 0.3);
}

.chat-input button:hover:not(:disabled) {
  transform: scale(1.1) translateY(-2px);
  box-shadow: 
    0 6px 16px rgba(255, 105, 180, 0.5),
    inset 0 1px 2px rgba(255, 255, 255, 0.4);
}

.chat-input button:active:not(:disabled) {
  transform: scale(1.05);
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.chat-input button svg {
  width: 18px;
  height: 18px;
  color: #fff;
}

/* 动画 - 3D弹出效果 */
.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(30px) scale(0.85) rotateX(10deg);
}

/* 滚动条美化 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #FFB6C1;
  border-radius: 3px;
}
</style>
