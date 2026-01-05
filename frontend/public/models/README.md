# VRM 模型使用说明

## 当前模型

当前使用的模型文件：`4821756309702791141.vrm`

## 如何更换 VRM 模型

### 步骤 1: 下载 VRM 模型

从 VRoid Hub 下载模型：
1. 访问你选择的模型页面（例如：https://hub.vroid.com/）
2. 点击 "Download" 按钮
3. 下载 `.vrm` 文件到本地

### 步骤 2: 放置模型文件

将下载的 `.vrm` 文件放到这个文件夹（`public/models/`）中

例如：
```
public/
  models/
    4821756309702791141.vrm  <- 当前模型
    sakura.vrm                <- 你的新模型文件
```

### 步骤 3: 更新代码

打开 `src/components/AIAssistant.vue`，找到这一行：

```javascript
const vrmModelUrl = ref('/models/4821756309702791141.vrm');
```

将文件名替换为你的新文件名：

```javascript
const vrmModelUrl = ref('/models/sakura.vrm');
```

### 步骤 4: 重启开发服务器

```bash
npm run dev
```

## 推荐的 VRM 模型来源

- **VRoid Hub**: https://hub.vroid.com/
- **VRoid Studio**: https://vroid.com/studio (创建自己的模型)
- **Booth**: https://booth.pm/ (购买专业模型)

## 注意事项

1. VRM 文件通常较大（5-20MB），首次加载可能需要几秒钟
2. 确保模型文件名不包含中文或特殊字符（建议使用英文或数字）
3. 推荐使用 VRM 1.0 格式的模型以获得最佳兼容性
4. 如果模型加载失败，请检查浏览器控制台的错误信息
