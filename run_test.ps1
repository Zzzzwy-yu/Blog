$base = "http://localhost:8081/api"
$output = @()

function T($name, $block) {
    $output += "=== $name ==="
    for ($i=1; $i -le 3; $i++) {
        try {
            $ok = & $block
            $output += "  round $i : $(if ($ok) {'OK'} else {'FAIL'})"
        } catch {
            $output += "  round $i : FAIL ($($_.Exception.Message))"
        }
    }
}

T "1. 首页文章列表+分页" {
    $r = Invoke-RestMethod "$base/blog/article/list?pageNum=1&pageSize=10"
    $r.code -eq 200 -and $null -ne $r.data
}

T "2. 文章详情" {
    $r = Invoke-RestMethod "$base/blog/article/1"
    $r.code -eq 200 -and $r.data.title
}

T "3a. 分类列表" {
    $r = Invoke-RestMethod "$base/blog/category/list"
    $r.code -eq 200 -and $r.data -is [Array]
}
T "3b. 按分类筛选文章" {
    $r = Invoke-RestMethod "$base/blog/article/list?categoryId=1"
    $r.code -eq 200 -and $null -ne $r.data
}

T "4a. 标签列表" {
    $r = Invoke-RestMethod "$base/blog/tag/list"
    $r.code -eq 200 -and $r.data -is [Array]
}
T "4b. 按标签筛选文章" {
    $r = Invoke-RestMethod "$base/blog/article/list?tagId=1"
    $r.code -eq 200 -and $null -ne $r.data
}

T "5. 搜索关键词" {
    $r = Invoke-RestMethod "$base/blog/article/list?keyword=SpringBoot"
    $r.code -eq 200 -and $null -ne $r.data
}

T "6. 游客发表评论" {
    $body = ConvertTo-Json @{articleId=1; nickname="visitor$i"; email="v@test.com"; content="hello $i $(Get-Random)"}
    $r = Invoke-RestMethod "$base/blog/comment/submit" -Method Post -Body $body -ContentType "application/json"
    $r.code -eq 200
}

T "7. 个人简介 profile" {
    $r = Invoke-RestMethod "$base/blog/profile"
    $r.code -eq 200 -and $r.data.nickname
}

$script:token = ""
T "8a. 管理员登录" {
    $body = '{"username":"admin","password":"123456"}'
    $r = Invoke-RestMethod "$base/admin/login" -Method Post -Body $body -ContentType "application/json"
    if ($r.code -eq 200 -and $r.data.token) { $script:token = "Bearer $($r.data.token)"; return $true }
    return $false
}

T "8b. 管理员文章列表" {
    $h = @{ Authorization = $script:token }
    $r = Invoke-RestMethod "$base/admin/article/list?pageNum=1&pageSize=10" -Headers $h
    $r.code -eq 200 -and $null -ne $r.data
}

T "8c. 管理员新建文章" {
    $h = @{ Authorization = $script:token }
    $body = ConvertTo-Json @{title="api-test-$(Get-Random)"; summary="s"; content="body"; categoryId=1; status=1}
    $r = Invoke-RestMethod "$base/admin/article/save" -Method Post -Body $body -ContentType "application/json" -Headers $h
    $r.code -eq 200
}

T "8d. 管理员编辑文章" {
    $h = @{ Authorization = $script:token }
    $body = ConvertTo-Json @{id=1; title="API-UPDATED-$(Get-Random)"; summary="s"; content="body"; categoryId=1; status=1}
    $r = Invoke-RestMethod "$base/admin/article/update" -Method Post -Body $body -ContentType "application/json" -Headers $h
    $r.code -eq 200
}

T "9a. 管理员分类列表" {
    $h = @{ Authorization = $script:token }
    $r = Invoke-RestMethod "$base/admin/category/list" -Headers $h
    $r.code -eq 200 -and $r.data -is [Array]
}

T "9b. 新建分类" {
    $h = @{ Authorization = $script:token }
    $body = ConvertTo-Json @{name="cat-$(Get-Random)"; description="d"; sortOrder=10; status=1}
    $r = Invoke-RestMethod "$base/admin/category/save" -Method Post -Body $body -ContentType "application/json" -Headers $h
    $r.code -eq 200
}

T "9c. 新建标签" {
    $h = @{ Authorization = $script:token }
    $body = ConvertTo-Json @{name="tag-$(Get-Random)"; description="d"; status=1}
    $r = Invoke-RestMethod "$base/admin/tag/save" -Method Post -Body $body -ContentType "application/json" -Headers $h
    $r.code -eq 200
}

T "9d. 审核评论" {
    $h = @{ Authorization = $script:token }
    $list = Invoke-RestMethod "$base/admin/comment/list?pageNum=1&pageSize=10" -Headers $h
    if ($list.code -ne 200 -or $list.data.list.Count -eq 0) { return $false }
    $id = $list.data.list[0].id
    $r = Invoke-RestMethod "$base/admin/comment/approve/$id" -Method Post -Headers $h
    $r.code -eq 200
}

T "10. 数据看板" {
    $h = @{ Authorization = $script:token }
    $r = Invoke-RestMethod "$base/admin/dashboard" -Headers $h
    $r.code -eq 200 -and $null -ne $r.data.commentCount
}

$output | Out-File -Encoding UTF8 C:\Users\Administrator\Documents\trae_projects\Blog\test_out.txt
