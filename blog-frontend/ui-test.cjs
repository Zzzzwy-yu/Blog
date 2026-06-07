// 博客系统 - 14 步 UI/功能测试
const http = require('http');
const fs = require('fs');
const path = require('path');

const FRONT_BASE = 'http://localhost:5173';
const API_BASE = 'http://localhost:8081/api';
const OUT = path.join(__dirname, 'ui-test-result.log');

let adminToken = '';
const results = [];
const logs = [];

function log(s) {
    logs.push(s);
    console.log(s);
}

// HTTP GET (不发送 hash)
function httpGet(url, headers) {
    return new Promise((resolve) => {
        const u = new URL(url);
        const h = headers || {};
        const reqPath = u.pathname + u.search;
        const req = http.request({ hostname: u.hostname, port: u.port, path: reqPath, method: 'GET', headers: h }, (res) => {
            let body = '';
            res.on('data', (c) => (body += c));
            res.on('end', () => resolve({ status: res.statusCode, body }));
        });
        req.on('error', () => resolve({ status: 0, body: '' }));
        req.end();
    });
}

function apiGet(path2, headers) {
    return new Promise((resolve) => {
        const u = new URL(API_BASE + path2);
        const h = headers || {};
        const req = http.request({ hostname: u.hostname, port: u.port, path: u.pathname + u.search, method: 'GET', headers: h }, (res) => {
            let d = '';
            res.on('data', (c) => (d += c));
            res.on('end', () => { try { resolve(JSON.parse(d)); } catch (e) { resolve({ code: -1, _raw: d }); } });
        });
        req.on('error', () => resolve({ code: -1 }));
        req.end();
    });
}

function apiPost(path2, body, headers) {
    return new Promise((resolve) => {
        const data = JSON.stringify(body || {});
        const u = new URL(API_BASE + path2);
        const allHeaders = { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(data) };
        if (headers) { for (const k in headers) allHeaders[k] = headers[k]; }
        const req = http.request({ hostname: u.hostname, port: u.port, path: u.pathname + u.search, method: 'POST', headers: allHeaders }, (res) => {
            let d = '';
            res.on('data', (c) => (d += c));
            res.on('end', () => { try { resolve(JSON.parse(d)); } catch (e) { resolve({ code: -1, _raw: d }); } });
        });
        req.on('error', () => resolve({ code: -1 }));
        req.write(data);
        req.end();
    });
}

function record(step, name, ok, detail) {
    results.push({ step, name, ok, detail });
    log('Step ' + step + ' [' + (ok ? 'SUCCESS' : 'FAIL') + '] ' + name + ' - ' + detail);
}

function authH() { return { Authorization: adminToken }; }

(async () => {
    log('===== Blog System 14 Steps UI/Feature Test =====');
    log('Time: ' + new Date().toLocaleString());
    log('Frontend: ' + FRONT_BASE);
    log('Backend: ' + API_BASE);
    log('');

    // --- Step 1: Home Page Layout ---
    log('--- Step 1: Home Page ---');
    const homeRes = await httpGet(FRONT_BASE + '/');
    const articles = await apiGet('/blog/article/list?pageNum=1&pageSize=5');
    const cats = await apiGet('/blog/category/list');
    const tags = await apiGet('/blog/tag/list');
    const profile = await apiGet('/blog/profile');
    const step1Ok = homeRes.status === 200
        && articles.code === 200 && Array.isArray(articles.data && articles.data.list)
        && cats.code === 200 && Array.isArray(cats.data)
        && tags.code === 200 && Array.isArray(tags.data)
        && profile.code === 200 && profile.data && profile.data.nickname;
    record(1, 'Home Page Layout', step1Ok,
        'HTTP=' + homeRes.status + ', Articles=' + (articles.data && articles.data.list ? articles.data.list.length : 0) + ', Categories=' + (cats.data ? cats.data.length : 0) + ', Tags=' + (tags.data ? tags.data.length : 0) + ', Author=' + (profile.data ? profile.data.nickname : '-'));

    // --- Step 2: Article Detail ---
    log('--- Step 2: Article Detail ---');
    let firstArticleId = null;
    let firstArticleTitle = '';
    if (articles.data && articles.data.list && articles.data.list.length > 0) {
        firstArticleId = articles.data.list[0].id;
        firstArticleTitle = articles.data.list[0].title;
    }
    let step2Ok = false;
    let step2Detail = 'No articles available';
    if (firstArticleId) {
        const detail = await apiGet('/blog/article/' + firstArticleId);
        step2Ok = detail.code === 200 && detail.data && detail.data.title && detail.data.content;
        const detailHtml = await httpGet(FRONT_BASE + '/#/article/' + firstArticleId);
        step2Detail = 'ArticleID=' + firstArticleId + ', Title=' + (detail.data ? detail.data.title : firstArticleTitle) + ', PageHTTP=' + detailHtml.status;
    }
    record(2, 'Click First Article Detail', step2Ok, step2Detail);

    // --- Step 3: Search Spring ---
    log('--- Step 3: Search Spring ---');
    const searchRes = await apiGet('/blog/article/list?keyword=Spring');
    const searchAllRes = await apiGet('/blog/article/list?pageNum=1&pageSize=100');
    const searchList = (searchRes.data && searchRes.data.list) ? searchRes.data.list : [];
    let allMatchSpring = true;
    for (const a of searchList) {
        const title = a.title ? a.title.toLowerCase() : '';
        const summary = a.summary ? a.summary.toLowerCase() : '';
        if (!title.includes('spring') && !summary.includes('spring')) {
            allMatchSpring = false;
            break;
        }
    }
    const step3Ok = searchRes.code === 200 && searchRes.data && allMatchSpring && (searchRes.data.total <= (searchAllRes.data ? searchAllRes.data.total : 0));
    record(3, 'Search Spring', step3Ok,
        'Results=' + searchList.length + '/' + (searchRes.data ? searchRes.data.total : 0) + ', Total=' + (searchAllRes.data ? searchAllRes.data.total : 0));

    // --- Step 4: Filter by Category ---
    log('--- Step 4: Filter by Category ---');
    let step4Ok = false;
    let step4Detail = 'No categories available';
    if (cats.data && cats.data.length > 0) {
        const firstCat = cats.data[0];
        const byCat = await apiGet('/blog/article/list?categoryId=' + firstCat.id);
        step4Ok = byCat.code === 200 && byCat.data;
        const catList = (byCat.data && byCat.data.list) ? byCat.data.list : [];
        let allMatchCat = true;
        for (const a of catList) {
            if (a.categoryId !== firstCat.id && a.categoryName !== firstCat.name) {
                allMatchCat = false;
                break;
            }
        }
        step4Ok = step4Ok && (catList.length === 0 || allMatchCat);
        step4Detail = 'Category=' + firstCat.name + '(id=' + firstCat.id + '), Results=' + catList.length + ', AllMatch=' + (catList.length === 0 || allMatchCat);
    }
    record(4, 'Filter by Category', step4Ok, step4Detail);

    // --- Step 5: Filter by Tag ---
    log('--- Step 5: Filter by Tag ---');
    let step5Ok = false;
    let step5Detail = 'No tags available';
    if (tags.data && tags.data.length > 0) {
        const firstTag = tags.data[0];
        const byTag = await apiGet('/blog/article/list?tagId=' + firstTag.id);
        step5Ok = byTag.code === 200 && byTag.data;
        const tagList = (byTag.data && byTag.data.list) ? byTag.data.list : [];
        step5Detail = 'Tag=' + firstTag.name + '(id=' + firstTag.id + '), Results=' + tagList.length;
    }
    record(5, 'Filter by Tag', step5Ok, step5Detail);

    // --- Step 6: Message Board ---
    log('--- Step 6: Message Board ---');
    const msgPage = await httpGet(FRONT_BASE + '/#/message');
    const msgList = await apiGet('/blog/message/list?pageNum=1&pageSize=5');
    const step6Ok = msgPage.status === 200 && msgList.code === 200 && msgList.data;
    record(6, 'Message Board Page', step6Ok, 'PageHTTP=' + msgPage.status + ', Messages=' + (msgList.data && msgList.data.list ? msgList.data.list.length : 0));

    // --- Step 7: About Me ---
    log('--- Step 7: About Me ---');
    const aboutPage = await httpGet(FRONT_BASE + '/#/about');
    const aboutProfile = await apiGet('/blog/profile');
    const step7Ok = aboutPage.status === 200 && aboutProfile.code === 200 && aboutProfile.data;
    record(7, 'About Me Page', step7Ok, 'PageHTTP=' + aboutPage.status + ', Author=' + (aboutProfile.data ? aboutProfile.data.nickname : '-'));

    // --- Step 8: Admin Login Page ---
    log('--- Step 8: Admin Login Page ---');
    const loginPage = await httpGet(FRONT_BASE + '/#/admin/login');
    const step8Ok = loginPage.status === 200;
    record(8, 'Admin Login Page', step8Ok, 'PageHTTP=' + loginPage.status);

    // --- Step 9: Admin Login admin/123456 ---
    log('--- Step 9: Admin Login ---');
    const loginRes = await apiPost('/admin/login', { username: 'admin', password: '123456' });
    const step9Ok = loginRes.code === 200 && loginRes.data && loginRes.data.token;
    if (step9Ok) adminToken = 'Bearer ' + loginRes.data.token;
    record(9, 'Admin Login admin/123456', step9Ok, 'code=' + loginRes.code + ', hasToken=' + !!(loginRes.data && loginRes.data.token));

    // --- Step 10: Dashboard ---
    log('--- Step 10: Dashboard ---');
    const dash = adminToken ? await apiGet('/admin/dashboard', authH()) : { code: -1 };
    const step10Ok = dash.code === 200 && dash.data && dash.data.articleCount !== undefined && dash.data.commentCount !== undefined;
    record(10, 'Dashboard Stats', step10Ok,
        'Articles=' + (dash.data ? dash.data.articleCount : 0) + ', Comments=' + (dash.data ? dash.data.commentCount : 0) + ', Categories=' + (dash.data ? dash.data.categoryCount : 0) + ', Tags=' + (dash.data ? dash.data.tagCount : 0));

    // --- Step 11: Admin Article List ---
    log('--- Step 11: Admin Article List ---');
    const adminArticles = adminToken ? await apiGet('/admin/article/list?pageNum=1&pageSize=10', authH()) : { code: -1 };
    const step11Ok = adminArticles.code === 200 && adminArticles.data && Array.isArray(adminArticles.data.list);
    record(11, 'Admin Article List', step11Ok, 'Count=' + (adminArticles.data && adminArticles.data.list ? adminArticles.data.list.length : 0) + '/' + (adminArticles.data ? adminArticles.data.total : 0));

    // --- Step 12: Admin Category List ---
    log('--- Step 12: Admin Category List ---');
    const adminCats = adminToken ? await apiGet('/admin/category/list', authH()) : { code: -1 };
    const step12Ok = adminCats.code === 200 && Array.isArray(adminCats.data);
    record(12, 'Admin Category List', step12Ok, 'Count=' + (adminCats.data ? adminCats.data.length : 0));

    // --- Step 13: Admin Tag List ---
    log('--- Step 13: Admin Tag List ---');
    const adminTags = adminToken ? await apiGet('/admin/tag/list', authH()) : { code: -1 };
    const step13Ok = adminTags.code === 200 && Array.isArray(adminTags.data);
    record(13, 'Admin Tag List', step13Ok, 'Count=' + (adminTags.data ? adminTags.data.length : 0));

    // --- Step 14: Admin Comment List ---
    log('--- Step 14: Admin Comment List ---');
    const adminComments = adminToken ? await apiGet('/admin/comment/list?pageNum=1&pageSize=10', authH()) : { code: -1 };
    const step14Ok = adminComments.code === 200 && adminComments.data && Array.isArray(adminComments.data.list);
    record(14, 'Admin Comment List', step14Ok, 'Count=' + (adminComments.data && adminComments.data.list ? adminComments.data.list.length : 0) + '/' + (adminComments.data ? adminComments.data.total : 0));

    // --- Summary ---
    log('');
    log('===== Test Summary =====');
    let passCount = 0, failCount = 0;
    for (const r of results) {
        if (r.ok) passCount++;
        else failCount++;
        log((r.ok ? 'PASS' : 'FAIL') + ' Step ' + r.step + ': ' + r.name + ' - ' + r.detail);
    }
    log('');
    log('Total: PASS=' + passCount + ', FAIL=' + failCount + ', Steps=' + results.length);

    try {
        fs.writeFileSync(OUT, logs.join('\n'), 'utf8');
        console.log('\nResult saved to: ' + OUT);
    } catch (e) {
        console.log('Write failed: ' + e.message);
    }
})();
