# Docker 部署指南

## 一、准备工作

### 1. 腾讯云服务器要求
- 系统：Ubuntu 20.04+ 或 CentOS 7+
- 配置：最低 2核4G
- 开放端口：80, 8080, 3306

### 2. 安装 Docker 和 Docker Compose

```bash
# Ubuntu
sudo apt update
sudo apt install -y docker.io docker-compose

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 添加当前用户到 docker 组（免 sudo）
sudo usermod -aG docker $USER
```

## 二、部署步骤

### 1. 上传项目到服务器

```bash
# 方式1：使用 scp
scp -r Hospital_Registration_System root@你的服务器IP:/opt/

# 方式2：使用 git
cd /opt
git clone 你的仓库地址
```

### 2. 进入项目目录

```bash
cd /opt/Hospital_Registration_System
```

### 3. 一键启动

```bash
docker-compose up -d --build
```

### 4. 查看运行状态

```bash
docker-compose ps
docker-compose logs -f
```

## 三、访问地址

- 前端：http://你的服务器IP
- 后端API：http://你的服务器IP:8080/api

## 四、常用命令

```bash
# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 重新构建并启动
docker-compose up -d --build

# 进入容器
docker exec -it hospital-backend sh
docker exec -it hospital-mysql mysql -uroot -p123456
```

## 五、数据库初始化

首次启动会自动执行 `hospital_registration.sql` 初始化数据库。

如需手动执行额外SQL：
```bash
docker exec -i hospital-mysql mysql -uroot -p123456 hospital_registration < backend/add_hospital_table.sql
```

## 六、配置域名（可选）

如果有域名，修改 `frontend/nginx.conf`：
```nginx
server_name your-domain.com;
```

然后重新构建前端：
```bash
docker-compose up -d --build frontend
```

## 七、HTTPS配置（可选）

建议使用腾讯云的免费SSL证书，配置到nginx中。

## 八、注意事项

1. 生产环境请修改数据库密码
2. API Key 建议使用环境变量配置
3. 定期备份数据库：
   ```bash
   docker exec hospital-mysql mysqldump -uroot -p123456 hospital_registration > backup.sql
   ```
