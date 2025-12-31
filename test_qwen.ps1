$headers = @{
    "Authorization" = "Bearer sk-e2402dc10eea46a696f548ab7f909696"
    "Content-Type" = "application/json"
}

$body = @{
    model = "qwen-turbo"
    messages = @(
        @{
            role = "user"
            content = "你好"
        }
    )
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-RestMethod -Uri "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions" -Method POST -Headers $headers -Body $body
    Write-Host "Success!"
    Write-Host ($response | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    if ($_.ErrorDetails.Message) {
        Write-Host "Details: $($_.ErrorDetails.Message)"
    }
}
