# Redis 快速启动脚本（Docker 方式）
# 使用方法：在 PowerShell 中执行 .\start-redis.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Redis 服务启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Docker 是否运行
Write-Host "[1/5] 检查 Docker 状态..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "✓ Docker 正在运行" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker 未运行，请先启动 Docker Desktop" -ForegroundColor Red
    exit 1
}

# 检查容器是否已存在
Write-Host "[2/5] 检查 Redis 容器..." -ForegroundColor Yellow
$container = docker ps -a --filter "name=redis-hospital" --format "{{.Names}}"

if ($container -eq "redis-hospital") {
    $running = docker ps --filter "name=redis-hospital" --format "{{.Names}}"
    if ($running -eq "redis-hospital") {
        Write-Host "✓ Redis 容器已在运行" -ForegroundColor Green
    } else {
        Write-Host "→ 启动现有容器..." -ForegroundColor Yellow
        docker start redis-hospital | Out-Null
        Write-Host "✓ Redis 容器已启动" -ForegroundColor Green
    }
} else {
    Write-Host "→ 创建新的 Redis 容器..." -ForegroundColor Yellow
    docker run -d `
      --name redis-hospital `
      -p 6379:6379 `
      --restart unless-stopped `
      redis:7-alpine | Out-Null
    Write-Host "✓ Redis 容器已创建并启动" -ForegroundColor Green
}

# 等待 Redis 启动
Write-Host "[3/5] 等待 Redis 服务就绪..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

# 测试连接
Write-Host "[4/5] 测试 Redis 连接..." -ForegroundColor Yellow
$result = docker exec redis-hospital redis-cli ping 2>&1
if ($result -eq "PONG") {
    Write-Host "✓ Redis 连接成功" -ForegroundColor Green
} else {
    Write-Host "✗ Redis 连接失败: $result" -ForegroundColor Red
    exit 1
}

# 显示容器信息
Write-Host "[5/5] 显示容器状态..." -ForegroundColor Yellow
docker ps --filter "name=redis-hospital" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Redis 服务已就绪！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "连接信息：" -ForegroundColor Yellow
Write-Host "  主机: localhost" -ForegroundColor White
Write-Host "  端口: 6379" -ForegroundColor White
Write-Host "  密码: 无" -ForegroundColor White
Write-Host ""
Write-Host "常用命令：" -ForegroundColor Yellow
Write-Host "  查看日志: docker logs redis-hospital" -ForegroundColor White
Write-Host "  停止服务: docker stop redis-hospital" -ForegroundColor White
Write-Host "  启动服务: docker start redis-hospital" -ForegroundColor White
Write-Host "  删除容器: docker rm -f redis-hospital" -ForegroundColor White
Write-Host ""



