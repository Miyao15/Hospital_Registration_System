# Redis 安装配置指南

本项目使用 Redis 用于以下功能：
- JWT Token 黑名单管理（用户登出）
- 验证码存储（密码重置、登录验证）
- 缓存功能

## 项目 Redis 配置

项目默认配置（`application.yml`）：
```yaml
spring:
  data:
    redis:
      host: localhost        # Redis 服务器地址
      port: 6379             # Redis 端口（默认）
      password:              # Redis 密码（可选，默认无密码）
```

## Windows 安装 Redis 的几种方式

### 方式一：使用 WSL（推荐）⭐

WSL（Windows Subsystem for Linux）是最接近生产环境的方案。

#### 步骤 1：安装 WSL

1. **打开 PowerShell（管理员权限）**
   - 按 `Win + X`，选择 "Windows PowerShell (管理员)"

2. **安装 WSL 和 Ubuntu**
   ```powershell
   wsl --install
   ```
   或手动安装：
   ```powershell
   wsl --install -d Ubuntu-22.04
   ```

3. **重启计算机**（安装完成后会提示）

4. **启动 Ubuntu**
   - 在开始菜单搜索 "Ubuntu" 并打开
   - 首次启动需要设置用户名和密码

#### 步骤 2：在 WSL 中安装 Redis

1. **更新系统包**
   ```bash
   sudo apt update
   sudo apt upgrade -y
   ```

2. **安装 Redis**
   ```bash
   sudo apt install redis-server -y
   ```

3. **配置 Redis**
   ```bash
   # 编辑 Redis 配置文件
   sudo nano /etc/redis/redis.conf
   ```
   
   找到并修改以下配置：
   ```
   # 允许从任何地址连接（WSL 需要）
   bind 0.0.0.0
   
   # 设置密码（可选，但推荐）
   # requirepass your_password_here
   
   # 持久化配置（默认已开启）
   save 900 1
   save 300 10
   save 60 10000
   ```

4. **启动 Redis 服务**
   ```bash
   sudo service redis-server start
   ```

5. **设置 Redis 开机自启**
   ```bash
   sudo systemctl enable redis-server
   ```

6. **测试 Redis 连接**
   ```bash
   redis-cli ping
   ```
   应该返回：`PONG`

#### 步骤 3：配置 Windows 防火墙（如果需要）

如果 Windows 防火墙阻止连接，需要允许端口 6379：

1. 打开 "Windows Defender 防火墙"
2. 点击 "高级设置"
3. 选择 "入站规则" → "新建规则"
4. 选择 "端口" → "TCP" → 输入 `6379`
5. 允许连接

#### 步骤 4：验证连接

在 Windows PowerShell 中测试（需要先安装 Redis 客户端工具，见下方）：

```powershell
# 如果安装了 Redis for Windows 客户端
redis-cli -h localhost -p 6379 ping
```

或在项目启动后查看日志，确认 Redis 连接成功。

---

### 方式二：使用 Docker（推荐）⭐

Docker 是最简单且跨平台的方式。

#### 步骤 1：安装 Docker Desktop

1. **下载 Docker Desktop**
   - 访问：https://www.docker.com/products/docker-desktop/
   - 下载 Windows 版本并安装

2. **启动 Docker Desktop**
   - 安装完成后启动 Docker Desktop
   - 等待 Docker 引擎启动完成（系统托盘图标不再闪烁）

#### 步骤 2：运行 Redis 容器

打开 PowerShell 或命令提示符，执行：

```powershell
docker run -d `
  --name redis-hospital `
  -p 6379:6379 `
  --restart unless-stopped `
  redis:7-alpine
```

参数说明：
- `-d`: 后台运行
- `--name redis-hospital`: 容器名称
- `-p 6379:6379`: 端口映射（主机:容器）
- `--restart unless-stopped`: 自动重启
- `redis:7-alpine`: Redis 镜像（轻量级版本）

#### 步骤 3：验证 Redis 运行

```powershell
# 查看容器状态
docker ps

# 测试 Redis 连接
docker exec -it redis-hospital redis-cli ping
```

应该返回：`PONG`

#### 步骤 4：设置 Redis 密码（可选但推荐）

如果需要设置密码，使用以下命令：

```powershell
# 停止并删除现有容器
docker stop redis-hospital
docker rm redis-hospital

# 重新创建带密码的容器
docker run -d `
  --name redis-hospital `
  -p 6379:6379 `
  --restart unless-stopped `
  redis:7-alpine redis-server --requirepass your_password_here
```

然后在 `application.yml` 中配置密码：
```yaml
spring:
  data:
    redis:
      password: your_password_here
```

---

### 方式三：使用 Memurai（Windows 原生）

Memurai 是 Windows 原生的 Redis 兼容服务器。

#### 步骤 1：下载 Memurai

1. 访问：https://www.memurai.com/get-memurai
2. 下载 Windows 版本（免费版即可）

#### 步骤 2：安装 Memurai

1. 运行安装程序
2. 按照向导完成安装
3. 安装完成后，Memurai 会自动作为 Windows 服务启动

#### 步骤 3：验证安装

打开 PowerShell：

```powershell
# 如果安装了 Redis 客户端工具
redis-cli ping
```

---

### 方式四：使用 Redis for Windows（不推荐）

⚠️ **注意**：Redis 官方不再维护 Windows 版本，只提供旧版本。

如果必须使用，可以：

1. 下载旧版本：https://github.com/microsoftarchive/redis/releases
2. 解压到 `C:\Redis`
3. 在 PowerShell（管理员）中运行：
   ```powershell
   cd C:\Redis
   .\redis-server.exe
   ```

---

## 安装 Redis 客户端工具（可选）

为了方便测试和管理 Redis，可以安装客户端工具：

### RedisInsight（官方 GUI 工具）

1. 下载：https://redis.com/redis-enterprise/redis-insight/
2. 安装后连接到 `localhost:6379`

### Redis CLI（命令行工具）

#### 方式 1：通过 WSL
```bash
# 在 WSL Ubuntu 中
sudo apt install redis-tools
redis-cli
```

#### 方式 2：通过 Chocolatey
```powershell
# 安装 Chocolatey（如果未安装）
# 然后执行：
choco install redis-64 -y
```

#### 方式 3：下载预编译版本
- 访问：https://github.com/microsoftarchive/redis/releases
- 下载并解压 `redis-cli.exe`

---

## 配置项目使用 Redis

### 1. 无密码配置（默认）

如果 Redis 没有设置密码，保持 `application.yml` 默认配置即可：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:  # 留空
```

### 2. 有密码配置

如果设置了密码，修改 `application.yml`：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password_here
```

### 3. 使用环境变量（推荐）

为了安全，建议使用环境变量：

**Windows PowerShell：**
```powershell
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:REDIS_PASSWORD="your_password"
```

**Windows 系统环境变量：**
1. 右键 "此电脑" → "属性"
2. "高级系统设置" → "环境变量"
3. 在 "用户变量" 中添加：
   - `REDIS_HOST` = `localhost`
   - `REDIS_PORT` = `6379`
   - `REDIS_PASSWORD` = `your_password`

然后 `application.yml` 会自动读取这些环境变量。

---

## 验证 Redis 连接

### 方法 1：启动项目查看日志

启动 Spring Boot 应用，查看控制台输出：

```
INFO  --- [main] o.s.data.redis.connection.lettuce.LettuceConnectionFactory : Connecting to Redis at localhost:6379
```

如果没有错误，说明连接成功。

### 方法 2：使用 Redis CLI 测试

```bash
# 连接 Redis
redis-cli

# 或指定主机和端口
redis-cli -h localhost -p 6379

# 测试连接
ping
# 应该返回: PONG

# 测试写入和读取
set test "hello"
get test
# 应该返回: "hello"
```

### 方法 3：查看项目功能

1. 启动项目
2. 尝试登录功能（会使用 Redis 存储验证码）
3. 尝试登出功能（会使用 Redis 存储 token 黑名单）

---

## 常见问题排查

### 问题 1：连接被拒绝

**错误信息：**
```
Unable to connect to Redis at localhost:6379: Connection refused
```

**解决方案：**
1. 确认 Redis 服务正在运行
   - WSL: `sudo service redis-server status`
   - Docker: `docker ps`
   - Memurai: 检查 Windows 服务
2. 检查端口是否被占用：`netstat -an | findstr 6379`
3. 检查防火墙设置

### 问题 2：认证失败

**错误信息：**
```
NOAUTH Authentication required
```

**解决方案：**
1. 检查 `application.yml` 中的密码配置
2. 确认 Redis 服务器确实设置了密码
3. 使用 `redis-cli` 测试：`redis-cli -a your_password ping`

### 问题 3：WSL 中 Redis 无法从 Windows 访问

**解决方案：**
1. 确保 Redis 配置中 `bind 0.0.0.0`（不是 `127.0.0.1`）
2. 重启 Redis：`sudo service redis-server restart`
3. 检查 WSL 网络配置

### 问题 4：Docker 容器无法启动

**解决方案：**
1. 检查端口是否被占用：`netstat -an | findstr 6379`
2. 如果端口被占用，修改映射端口：
   ```powershell
   docker run -d --name redis-hospital -p 6380:6379 redis:7-alpine
   ```
   然后修改 `application.yml` 中的端口为 `6380`

---

## 推荐配置方案

### 开发环境
- **推荐**：Docker（最简单）
- **备选**：WSL + Redis

### 生产环境
- 使用专业的 Redis 服务（如 Redis Cloud、AWS ElastiCache）
- 或使用 Docker Compose 部署
- 必须设置密码和 SSL/TLS

---

## 快速启动脚本

### Docker 方式（一键启动）

创建 `start-redis.ps1`：

```powershell
# 检查容器是否已存在
$container = docker ps -a --filter "name=redis-hospital" --format "{{.Names}}"

if ($container -eq "redis-hospital") {
    Write-Host "Redis 容器已存在，正在启动..."
    docker start redis-hospital
} else {
    Write-Host "创建并启动 Redis 容器..."
    docker run -d `
      --name redis-hospital `
      -p 6379:6379 `
      --restart unless-stopped `
      redis:7-alpine
}

Write-Host "等待 Redis 启动..."
Start-Sleep -Seconds 3

Write-Host "测试 Redis 连接..."
docker exec redis-hospital redis-cli ping

Write-Host "Redis 已启动！"
```

运行：
```powershell
.\start-redis.ps1
```

---

## 总结

1. **最简单**：使用 Docker（推荐新手）
2. **最接近生产**：使用 WSL
3. **Windows 原生**：使用 Memurai

选择任一方式安装后，确保：
- Redis 服务正在运行
- 端口 6379 可访问
- 项目配置正确（`application.yml`）

安装完成后，启动 Spring Boot 项目即可自动连接 Redis！



