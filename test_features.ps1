# 博客系统 - 10 项功能自动化测试脚本
# 使用 PowerShell 调用 localhost:8081 接口

$base = "http://localhost:8081/api"
$totalPass = 0
$totalFail = 0

function Test-Endpoint($name, $scriptBlock) {
    Write-Host "=== 测试: $name ===" -ForegroundColor Cyan
    for ($i = 1; $i -le 3; $i++) {
        try {
            $result = & $scriptBlock
            if ($result) {
                Write-Host "  轮次 $i PASS" -ForegroundColor Green
            } else {
                Write-Host "  轮次 $i FAIL" -ForegroundColor Red
                $script:totalFail++
            }
        } catch {
            Write-Host "  轮次 $i FAIL (异常: $($_.Exception.Message))" -ForegroundColor Red
            $script:totalFail++
        }
    }
}

# 1. 首页文章列表 + 分页
Test-Endpoint "1. 首页文章列表 + 分页" {
    $r = Invoke-RestMethod "$base/blog/article/list?pageNum=1&pageSize=10"
    return ($r.code -eq 200 -and $r.data.total -ge 0 -and $r.data.list -is [Array])
}

# 2. 文章详情
Test-Endpoint "2. 文章详情(按ID)" {
    $r = Invoke-RestMethod "$base/blog/article/1"
    return ($r.code -eq 200 -and $r.data.title)
}

# 3. 分类列表 + 筛选
Test-Endpoint "3. 分类列表" {
    $r = Invoke-RestMethod "$base/blog/category/list"
    return ($r.code -eq 200 -and $r.data -is [Array])
}
Test-Endpoint "3b. 按分类筛选文章" {
    $r = Invoke-RestMethod "$base/blog/article/list?categoryId=1&pageNum=1&pageSize=10"
    return ($r.code -eq 200 -and $null -ne $r.data)
}

# 4. 标签列表 + 筛选
Test-Endpoint "4. 标签列表" {
    $r = Invoke-RestMethod "$base/blog/tag/list"
    return ($r.code -eq 200 -and $r.data -is [Array])
}
Test-Endpoint "4b. 按标签筛选文章" {
    $r = Invoke-RestMethod "$base/blog/article/list?tagId=1&pageNum=1&pageSize=10"
    return ($r.code -eq 200 -and $null -ne $r.data)
}

# 5. 搜索
Test-Endpoint "5. 按关键词搜索" {
    $r = Invoke-RestMethod "$base/blog/article/list?keyword=SpringBoot"
    return ($r.code -eq 200 -and $null -ne $r.data)
}

# 6. 发表评论(游客)
Test-Endpoint "6. 游客发表评论" {
    $body = @{ articleId = 1; nickname = "测试游客"; email = "test@test.com"; content = "这是一条测试评论" + (Get-Random) } | ConvertTo-Json
    $r = Invoke-RestMethod "$base/blog/comment/submit" -Method Post -Body $body -ContentType "application/json"
    return ($r.code -eq 200)
}

# 7. 个人简介
Test-Endpoint "7. 个人简介(Profile)" {
    $r = Invoke-RestMethod "$base/blog/profile"
    return ($r.code -eq 200 -and $r.data.nickname)
}

# 8. 后台登录 + 文章管理
$token = ""
Test-Endpoint "8a. 管理员登录 admin/123456" {
    $body = '{"username":"admin","password":"123456"}'
    $r = Invoke-RestMethod "$base/admin/login" -Method Post -Body $body -ContentType "application/json"
    if ($r.code -eq 200 -and $r.data.token) {
        $script:token = "Bearer " + $r.data.token
        return $true
    }
    return $false
}

Test-Endpoint "8b. 管理员获取文章列表" {
    $headers = @{ Authorization = $script:token }
    $r = Invoke-RestMethod "$base/admin/article/list?pageNum=1&pageSize=10" -Headers $headers
    return ($r.code -eq 200 -and $null -ne $r.data)
}

Test-Endpoint "8c. 管理员新建文章" {
    $headers = @{ Authorization = $script:token }
    $body = @{ title = "测试文章" + (Get-Random); summary = "摘要"; content = "内容 markdown"; categoryId = 1; status = 1 } | ConvertTo-Json
    $r = Invoke-RestMethod "$base/admin/article/save" -Method Post -Body $body -ContentType "application/json" -Headers $headers
    return ($r.code -eq 200)
}

Test-Endpoint "8d. 管理员编辑文章" {
    $headers = @{ Authorization = $script:token }
    $body = @{ id = 1; title = "SpringBoot3 入门指南 - 已更新"; summary = "更新摘要"; content = "新内容"; categoryId = 1; status = 1 } | ConvertTo-Json
    $r = Invoke-RestMethod "$base/admin/article/update" -Method Post -Body $body -ContentType "application/json" -Headers $headers
    return ($r.code -eq 200)
}

# 9. 分类/标签管理 + 评论审核
Test-Endpoint "9a. 管理分类列表" {
    $headers = @{ Authorization = $script:token }
    $r = Invoke-RestMethod "$base/admin/category/list" -Headers $headers
    return ($r.code -eq 200 -and $r.data -is [Array])
}

Test-Endpoint "9b. 新建分类" {
    $headers = @{ Authorization = $script:token }
    $body = @{ name = "测试分类" + (Get-Random); description = "desc"; sortOrder = 10; status = 1 } | ConvertTo-Json
    $r = Invoke-RestMethod "$base/admin/category/save" -Method Post -Body $body -ContentType "application/json" -Headers $headers
    return ($r.code -eq 200)
}

Test-Endpoint "9c. 新建标签" {
    $headers = @{ Authorization = $script:token }
    $body = @{ name = "测试标签" + (Get-Random); description = "desc"; status = 1 } | ConvertTo-Json
    $r = Invoke-RestMethod "$base/admin/tag/save" -Method Post -Body $body -ContentType "application/json" -Headers $headers
    return ($r.code -eq 200)
}

Test-Endpoint "9d. 审核评论(通过)" {
    $headers = @{ Authorization = $script:token }
    $list = Invoke-RestMethod "$base/admin/comment/list?pageNum=1&pageSize=10" -Headers $headers
    if ($list.code -ne 200 -or $list.data.list.Count -eq 0) { return $false }
    $id = $list.data.list[0].id
    $r = Invoke-RestMethod "$base/admin/comment/approve/$id" -Method Post -Headers $headers
    return ($r.code -eq 200)
}

# 10. 数据看板
Test-Endpoint "10. 数据看板统计" {
    $headers = @{ Authorization = $script:token }
    $r = Invoke-RestMethod "$base/admin/dashboard" -Headers $headers
    return ($r.code -eq 200 -and $null -ne $r.data.commentCount)
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Yellow
