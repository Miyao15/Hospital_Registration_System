# Redis 停止脚本（Docker 方式）
# 使用方法：在 PowerShell 中执行 .\stop-redis.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Redis 服务停止脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查容器是否存在
$container = docker ps -a --filter "name=redis-hospital" --format "{{.Names}}"

if ($container -eq "redis-hospital") {
    $running = docker ps --filter "name=redis-hospital" --format "{{.Names}}"
    if ($running -eq "redis-hospital") {
        Write-Host "正在停止 Redis 容器..." -ForegroundColor Yellow
        docker stop redis-hospital | Out-Null
        Write-Host "✓ Redis 容器已停止" -ForegroundColor Green
    } else {
        Write-Host "Redis 容器未运行" -ForegroundColor Yellow
    }
} else {
    Write-Host "Redis 容器不存在" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "完成" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""



