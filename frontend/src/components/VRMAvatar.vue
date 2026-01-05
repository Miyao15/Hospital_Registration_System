<template>
  <div ref="container" class="vrm-container">
    <!-- 加载指示器 -->
    <div v-if="isLoading" class="loading-indicator">
      <div class="spinner"></div>
      <span>加载中...</span>
    </div>
    <!-- 错误提示 -->
    <div v-if="loadError" class="error-message">
      <span>😢 模型加载失败</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { VRMLoaderPlugin, VRMUtils } from '@pixiv/three-vrm';

const props = defineProps({
  modelUrl: {
    type: String,
    default: 'https://cdn.jsdelivr.net/gh/pixiv/three-vrm@dev/packages/three-vrm/examples/models/VRM1_Constraint_Twist_Sample.vrm'
  },
  autoRotate: {
    type: Boolean,
    default: true
  },
  scale: {
    type: Number,
    default: 1.3
  },
  // 新增：相机距离调整
  cameraDistance: {
    type: Number,
    default: 2.8
  },
  // 新增：相机高度调整
  cameraHeight: {
    type: Number,
    default: 1.4
  },
  // 新增：模型初始旋转角度（弧度）
  initialRotation: {
    type: Number,
    default: Math.PI // 默认180度
  }
});

const container = ref(null);
const isLoading = ref(true);
const loadError = ref(false);
let scene, camera, renderer, vrm, clock;
let animationId = null;
let blinkTimer = 0;
let headRotationTimer = 0;
let expressionTimer = 0;
let breathingTimer = 0;
let armSwayTimer = 0;
let currentExpression = 'neutral';

onMounted(() => {
  initThreeJS();
  loadVRM();
});

onUnmounted(() => {
  cleanup();
});

function initThreeJS() {
  // 场景
  scene = new THREE.Scene();
  scene.background = null; // 透明背景
  
  // 相机 - 调整视角以显示完整模型
  camera = new THREE.PerspectiveCamera(
    45, // 适中的视野角度
    container.value.clientWidth / container.value.clientHeight,
    0.1,
    20
  );
  camera.position.set(0, props.cameraHeight, props.cameraDistance); // 使用props控制相机位置
  camera.lookAt(0, 0.5, 0); // 注视点在上半身
  
  // 渲染器
  renderer = new THREE.WebGLRenderer({ 
    alpha: true, 
    antialias: true 
  });
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio * 2, 3)); // 提高渲染质量
  renderer.outputEncoding = THREE.sRGBEncoding;
  container.value.appendChild(renderer.domElement);
  
  // 光照
  const light = new THREE.DirectionalLight(0xffffff, 1);
  light.position.set(1, 1, 1).normalize();
  scene.add(light);
  
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
  scene.add(ambientLight);
  
  // 时钟
  clock = new THREE.Clock();
  
  // 开始渲染循环
  animate();
}

async function loadVRM() {
  const loader = new GLTFLoader();
  loader.register((parser) => new VRMLoaderPlugin(parser));
  
  try {
    isLoading.value = true;
    loadError.value = false;
    
    const gltf = await loader.loadAsync(props.modelUrl);
    vrm = gltf.userData.vrm;
    
    // 调整模型
    VRMUtils.removeUnnecessaryVertices(gltf.scene);
    VRMUtils.removeUnnecessaryJoints(gltf.scene);
    
    // 设置模型缩放和位置
    vrm.scene.scale.set(props.scale, props.scale, props.scale);
    vrm.scene.position.set(0, -1.2, 0); // 大幅向下移动，让头部和上半身进入视野
    
    // 让模型正面朝向相机 - 使用可配置的旋转角度
    vrm.scene.rotation.y = props.initialRotation;
    
    scene.add(vrm.scene);
    
    // 调整手臂姿势 - 自然下垂站姿
    if (vrm.humanoid) {
      // 左臂 - 自然下垂
      const leftUpperArm = vrm.humanoid.getNormalizedBoneNode('leftUpperArm');
      const leftLowerArm = vrm.humanoid.getNormalizedBoneNode('leftLowerArm');
      const leftHand = vrm.humanoid.getNormalizedBoneNode('leftHand');
      if (leftUpperArm) {
        leftUpperArm.rotation.z = 1.4; // 接近垂直下垂
        leftUpperArm.rotation.x = 0.2; // 稍微向前
        leftUpperArm.rotation.y = 0;
      }
      if (leftLowerArm) {
        leftLowerArm.rotation.z = 0; // 小臂自然伸直
        leftLowerArm.rotation.x = 0;
      }
      if (leftHand) {
        leftHand.rotation.z = 0; // 手腕自然
      }
      
      // 右臂 - 自然下垂
      const rightUpperArm = vrm.humanoid.getNormalizedBoneNode('rightUpperArm');
      const rightLowerArm = vrm.humanoid.getNormalizedBoneNode('rightLowerArm');
      const rightHand = vrm.humanoid.getNormalizedBoneNode('rightHand');
      if (rightUpperArm) {
        rightUpperArm.rotation.z = -1.4; // 接近垂直下垂
        rightUpperArm.rotation.x = 0.2; // 稍微向前
        rightUpperArm.rotation.y = 0;
      }
      if (rightLowerArm) {
        rightLowerArm.rotation.z = 0; // 小臂自然伸直
        rightLowerArm.rotation.x = 0;
      }
      if (rightHand) {
        rightHand.rotation.z = 0; // 手腕自然
      }
    }
    
    // 初始化表情 - 设置为开心/微笑
    if (vrm.expressionManager) {
      vrm.expressionManager.setValue('happy', 0.6);
      vrm.expressionManager.setValue('relaxed', 0.3);
    }
    
    // 初始化眼睛注视 - 让眼睛看向相机
    if (vrm.lookAt) {
      vrm.lookAt.target = camera;
      // 确保眼睛注视功能启用
      vrm.lookAt.autoUpdate = true;
    }
    
    isLoading.value = false;
    
  } catch (error) {
    console.error('VRM加载失败:', error);
    isLoading.value = false;
    loadError.value = true;
  }
}

function animate() {
  animationId = requestAnimationFrame(animate);
  
  const deltaTime = clock.getDelta();
  
  if (vrm) {
    // 呼吸动画 - 身体轻微上下浮动
    breathingTimer += deltaTime;
    const breathingOffset = Math.sin(breathingTimer * 1.5) * 0.02;
    vrm.scene.position.y = -1.2 + breathingOffset; // 保持向下偏移
    
    // 眨眼动画 (每3-5秒眨一次)
    blinkTimer += deltaTime;
    if (blinkTimer > 3 + Math.random() * 2) {
      blinkTimer = 0;
      if (vrm.expressionManager) {
        // 快速眨眼
        vrm.expressionManager.setValue('blink', 1.0);
        setTimeout(() => {
          if (vrm && vrm.expressionManager) {
            vrm.expressionManager.setValue('blink', 0);
          }
        }, 150);
      }
    }
    
    // 头部自然摆动 - 加大幅度
    headRotationTimer += deltaTime;
    const headBone = vrm.humanoid?.getNormalizedBoneNode('head');
    if (headBone) {
      headBone.rotation.y = Math.sin(headRotationTimer * 0.5) * 0.25;
      headBone.rotation.x = Math.sin(headRotationTimer * 0.3) * 0.12;
      headBone.rotation.z = Math.sin(headRotationTimer * 0.4) * 0.08;
    }
    
    // 手臂轻微摆动动画 - 自然站姿的微动
    armSwayTimer += deltaTime;
    const leftUpperArm = vrm.humanoid?.getNormalizedBoneNode('leftUpperArm');
    const rightUpperArm = vrm.humanoid?.getNormalizedBoneNode('rightUpperArm');
    
    // 左臂动画 - 轻微摆动
    if (leftUpperArm) {
      leftUpperArm.rotation.z = 1.4 + Math.sin(armSwayTimer * 0.5) * 0.02;
      leftUpperArm.rotation.x = 0.2 + Math.sin(armSwayTimer * 0.4) * 0.01;
    }
    
    // 右臂动画 - 轻微摆动（相位相反）
    if (rightUpperArm) {
      rightUpperArm.rotation.z = -1.4 + Math.sin(armSwayTimer * 0.5 + Math.PI) * 0.02;
      rightUpperArm.rotation.x = 0.2 + Math.sin(armSwayTimer * 0.4 + Math.PI) * 0.01;
    }
    
    // 身体轻微旋转和倾斜
    const spine = vrm.humanoid?.getNormalizedBoneNode('spine');
    if (spine) {
      spine.rotation.y = Math.sin(headRotationTimer * 0.3) * 0.05;
      spine.rotation.z = Math.sin(armSwayTimer * 0.4) * 0.03;
    }
    
    // 整体轻微旋转 - 在初始角度基础上轻微摆动
    if (props.autoRotate) {
      vrm.scene.rotation.y = props.initialRotation + Math.sin(headRotationTimer * 0.2) * 0.05;
    } else {
      // 不自动旋转时，保持初始角度
      vrm.scene.rotation.y = props.initialRotation;
    }
    
    // 表情变化 (每8-12秒换一次表情)
    expressionTimer += deltaTime;
    if (expressionTimer > 8 + Math.random() * 4) {
      expressionTimer = 0;
      changeExpression();
    }
    
    // 更新VRM (包括眼睛注视)
    vrm.update(deltaTime);
  }
  
  renderer.render(scene, camera);
}

// 表情切换函数
function changeExpression() {
  if (!vrm || !vrm.expressionManager) return;
  
  const expressions = ['happy', 'relaxed', 'neutral'];
  const randomExpression = expressions[Math.floor(Math.random() * expressions.length)];
  
  // 平滑过渡到新表情
  if (currentExpression !== randomExpression) {
    // 清除当前表情
    vrm.expressionManager.setValue(currentExpression, 0);
    
    // 设置新表情
    const intensity = randomExpression === 'happy' ? 0.7 : 
                     randomExpression === 'relaxed' ? 0.4 : 0.2;
    vrm.expressionManager.setValue(randomExpression, intensity);
    
    currentExpression = randomExpression;
  }
}

function cleanup() {
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  
  if (renderer) {
    renderer.dispose();
    if (container.value && renderer.domElement) {
      container.value.removeChild(renderer.domElement);
    }
  }
  
  if (vrm) {
    VRMUtils.deepDispose(vrm.scene);
  }
}
</script>

<style scoped>
.vrm-container {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: visible; /* 改为visible以防止裁剪 */
}

.vrm-container canvas {
  display: block;
  width: 100% !important;
  height: 100% !important;
}

.loading-indicator {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #FF69B4;
  font-size: 12px;
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid rgba(255, 105, 180, 0.2);
  border-top-color: #FF69B4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #999;
  font-size: 12px;
  text-align: center;
}
</style>
