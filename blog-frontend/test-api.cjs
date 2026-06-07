// 博客系统 - 10 项功能自动化测试（每项 3 轮）
// 直接写文件，避免 trae PowerShell stdout 截断
const http = require('http');
const fs = require('fs');
const path = require('path');

const OUT = path.join(__dirname, '..', 'test-result.log');
const BASE = 'http://localhost:8081/api';
let adminToken = '';
let pass = 0, fail = 0;
const logs = [];
function log(s) { logs.push(s); console.log(s); }

function doGet(path2, headers = {}) {
    return new Promise((resolve) => {
        http.get(BASE + path2, { headers }, (res) => {
            let d = '';
            res.on('data', (c) => (d += c));
            res.on('end', () => { try { resolve(JSON.parse(d)); } catch (e) { resolve({ code: -1 }); } });
        }).on('error', () => resolve({ code: -1 }));
    });
}
function doPost(path2, body, headers = {}) {
    return new Promise((resolve) => {
        const data = JSON.stringify(body || {});
        const url = new URL(BASE + path2);
        const req = http.request({
            hostname: url.hostname, port: url.port, path: url.pathname + url.search,
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(data), ...headers }
        }, (res) => {
            let d = '';
            res.on('data', (c) => (d += c));
            res.on('end', () => { try { resolve(JSON.parse(d)); } catch (e) { resolve({ code: -1 }); } });
        });
        req.on('error', () => resolve({ code: -1 }));
        req.write(data); req.end();
    });
}

async function runTest(name, fn) {
    log('\n=== ' + name + ' ===');
    for (let i = 1; i <= 3; i++) {
        let ok = false;
        try { ok = await fn(); } catch (e) { ok = false; }
        if (ok) { pass++; log('  round ' + i + ' OK'); }
        else { fail++; log('  round ' + i + ' FAIL'); }
    }
}
function authH() { return { Authorization: adminToken }; }

(async () => {
    // 1
    await runTest('1. 首页文章列表+分页', async () => {
        const r = await doGet('/blog/article/list?pageNum=1&pageSize=10');
        return r.code === 200 && r.data && Array.isArray(r.data.list);
    });
    // 2
    await runTest('2. 文章详情', async () => {
        const r = await doGet('/blog/article/1');
        return r.code === 200 && r.data && r.data.title;
    });
    // 3a
    await runTest('3a. 分类列表', async () => {
        const r = await doGet('/blog/category/list');
        return r.code === 200 && Array.isArray(r.data);
    });
    // 3b
    await runTest('3b. 按分类筛选文章', async () => {
        const r = await doGet('/blog/article/list?categoryId=1');
        return r.code === 200 && r.data;
    });
    // 4a
    await runTest('4a. 标签列表', async () => {
        const r = await doGet('/blog/tag/list');
        return r.code === 200 && Array.isArray(r.data);
    });
    // 4b
    await runTest('4b. 按标签筛选文章', async () => {
        const r = await doGet('/blog/article/list?tagId=1');
        return r.code === 200 && r.data;
    });
    // 5
    await runTest('5. 关键词搜索', async () => {
        const r = await doGet('/blog/article/list?keyword=SpringBoot');
        return r.code === 200 && r.data;
    });
    // 6
    await runTest('6. 游客发表评论', async () => {
        const r = await doPost('/blog/comment/submit', {
            articleId: 1, nickname: 'v_' + Math.random().toString(36).slice(2, 8),
            email: 'v@test.com', content: 'hello ' + Math.random()
        });
        return r.code === 200;
    });
    // 7
    await runTest('7. 个人简介 profile', async () => {
        const r = await doGet('/blog/profile');
        return r.code === 200 && r.data && r.data.nickname;
    });
    // 8a
    await runTest('8a. 管理员登录 admin/123456', async () => {
        const r = await doPost('/admin/login', { username: 'admin', password: '123456' });
        if (r.code === 200 && r.data && r.data.token) { adminToken = 'Bearer ' + r.data.token; return true; }
        return false;
    });
    // 8b
    await runTest('8b. 管理员文章列表', async () => {
        const r = await doGet('/admin/article/list?pageNum=1&pageSize=10', authH());
        return r.code === 200 && r.data;
    });
    // 8c
    await runTest('8c. 管理员新建文章', async () => {
        const r = await doPost('/admin/article/save', { title: 't_' + Date.now(), summary: 's', content: 'c', categoryId: 1, status: 1 }, authH());
        return r.code === 200;
    });
    // 8d
    await runTest('8d. 管理员编辑文章', async () => {
        const r = await doPost('/admin/article/update', { id: 1, title: 'UPDATED ' + Date.now(), summary: 's', content: 'c', categoryId: 1, status: 1 }, authH());
        return r.code === 200;
    });
    // 8e
    await runTest('8e. 管理员下架/上架文章', async () => {
        const r = await doPost('/admin/article/status/1/0', {}, authH());
        // 接口可能返回 200 或 code=200/number，用 code === 200 校验
        return r.code === 200;
    });
    // 9a
    await runTest('9a. 管理员分类列表', async () => {
        const r = await doGet('/admin/category/list', authH());
        return r.code === 200 && Array.isArray(r.data);
    });
    // 9b
    await runTest('9b. 管理员新建分类', async () => {
        const r = await doPost('/admin/category/save', { name: 'c_' + Date.now(), description: 'd', sortOrder: 10, status: 1 }, authH());
        return r.code === 200;
    });
    // 9c
    await runTest('9c. 管理员新建标签', async () => {
        const r = await doPost('/admin/tag/save', { name: 't_' + Date.now(), description: 'd', status: 1 }, authH());
        return r.code === 200;
    });
    // 9d
    await runTest('9d. 管理员审核评论', async () => {
        const list = await doGet('/admin/comment/list?pageNum=1&pageSize=10', authH());
        if (list.code !== 200 || !list.data || !list.data.list || list.data.list.length === 0) return false;
        const id = list.data.list[0].id;
        const r = await doPost('/admin/comment/approve/' + id, {}, authH());
        return r.code === 200;
    });
    // 10
    await runTest('10. 数据看板', async () => {
        const r = await doGet('/admin/dashboard', authH());
        return r.code === 200 && r.data && r.data.commentCount !== undefined;
    });

    log('\n============= 汇总 =============');
    log('通过: ' + pass + '  失败: ' + fail);

    fs.writeFileSync(OUT, logs.join('\n'), 'utf8');
    process.exit(0);
})();
