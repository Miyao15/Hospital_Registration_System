# AI助手3D模型显示优化说明

## 修改日期
2025年1月

## 问题描述
- AI 3D模型显示不完整
- 模型朝向不正确，没有正面朝向用户

## 解决方案

### 1. 显示完整性优化

#### VRMAvatar.vue 修改
- **相机视野角度**: 35° → 45° (增大视野)
- **相机距离**: 1.8 → 可配置 (默认2.5，悬浮按钮3.0)
- **相机高度**: 1.4 → 可配置 (默认1.3，悬浮按钮1.4)
- **容器溢出**: `overflow: hidden` → `overflow: visible` (防止裁剪)

#### AIAssistant.vue 修改
- **悬浮按钮尺寸**: 120px × 120px → 140px × 140px
- **聊天窗口头像**: 48px × 48px → 56px × 56px
- **消息头像**: 36px × 36px → 42px × 42px

### 2. 模型朝向优化

#### 正面朝向设置
```javascript
// 模型初始旋转设置为0，确保正面朝向相机
vrm.scene.rotation.y = 0;
```

#### 自动旋转调整
```javascript
// 减小自动旋转幅度，从0.1降到0.05
if (props.autoRotate) {
  vrm.scene.rotation.y = Math.sin(headRotationTimer * 0.2) * 0.05;
} else {
  vrm.scene.rotation.y = 0; // 不旋转时保持正面
}
```

#### 眼睛注视优化
```javascript
// 启用眼睛自动注视相机
if (vrm.lookAt) {
  vrm.lookAt.target = camera;
  vrm.lookAt.autoUpdate = true;
}
```

### 3. 新增可配置参数

VRMAvatar组件现在支持以下props：

```vue
<VRMAvatar 
  :model-url="vrmModelUrl"
  :auto-rotate="true"
  :scale="1.2"
  :camera-distance="3.0"  <!-- 相机距离 -->
  :camera-height="1.4"    <!-- 相机高度 -->
/>
```

## 使用场景配置

### 悬浮按钮（大尺寸，有旋转）
```vue
<VRMAvatar 
  :model-url="vrmModelUrl"
  :auto-rotate="true"
  :scale="1.2"
  :camera-distance="3.0"
  :camera-height="1.4"
  class="vrm-avatar"
/>
```

### 聊天窗口头像（中等尺寸，无旋转）
```vue
<VRMAvatar 
  :model-url="vrmModelUrl"
  :auto-rotate="false"
  :scale="1.0"
  class="header-vrm"
/>
```

### 消息头像（小尺寸，无旋转）
```vue
<VRMAvatar 
  :model-url="vrmModelUrl"
  :auto-rotate="false"
  :scale="0.8"
  class="msg-vrm"
/>
```

## 效果说明

### 显示效果
- ✅ 模型完整显示，不会被裁剪
- ✅ 头部、身体、手臂都在可视范围内
- ✅ 容器尺寸适配不同使用场景

### 朝向效果
- ✅ 模型正面朝向用户（相机）
- ✅ 眼睛注视相机方向
- ✅ 自动旋转时只是轻微摆动，保持正面朝向
- ✅ 不旋转时完全正面静止

### 动画效果
- ✅ 呼吸动画（身体上下浮动）
- ✅ 眨眼动画（3-5秒一次）
- ✅ 头部自然摆动
- ✅ 手臂轻微挥动
- ✅ 表情变化（8-12秒一次）
- ✅ 整体轻微旋转（仅在autoRotate=true时）

## 调试建议

如果模型显示还有问题，可以调整以下参数：

1. **模型太大/太小**: 调整 `:scale` 值
2. **模型被裁剪**: 增大 `:camera-distance` 值
3. **模型位置偏上/偏下**: 调整 `:camera-height` 值
4. **模型朝向不对**: 检查 `vrm.scene.rotation.y` 值（0=正面，Math.PI=背面）

## 浏览器控制台调试

打开浏览器控制台，可以看到：
- VRM模型加载状态
- 任何加载错误信息
- Three.js渲染信息

## 相关文件

- `src/components/VRMAvatar.vue` - 3D模型渲染组件
- `src/components/AIAssistant.vue` - AI助手主组件
- `public/models/4821756309702791141.vrm` - VRM模型文件
- `public/models/README.md` - 模型使用说明

## 技术栈

- **Three.js** - 3D渲染引擎
- **@pixiv/three-vrm** - VRM模型加载器
- **Vue 3** - 前端框架
