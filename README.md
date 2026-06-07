# 个人技术博客系统

前后端分离的轻量级博客系统：

- **后端**：Spring Boot 3.2 + MyBatis-Plus 3.5 + JJWT + Spring Security Crypto (BCrypt)
- **前端**：Vue 3 + Vite 5 + Element Plus + Pinia + Vue Router 4 + Markdown-it
- **数据库**：MySQL 8.0（utf8mb4）

---

## 一、功能一览

| 模块 | 功能 |
|---|---|
| 前台首页 | 文章分页、按分类/标签筛选、关键字模糊搜索、Markdown 渲染、评论提交(待审核) |
| 留言板 | 访客留言（默认待审核，审核通过后展示） |
| 关于我 | 博主资料、文章总数、评论总数、总浏览量 |
| 后台登录 | 管理员账号密码登录，颁发 JWT Token |
| 文章管理 | 新增/编辑/删除、上下架、分类与标签维护 |
| 分类/标签管理 | 增删改查、重名校验、文章关联校验 |
| 留言/评论管理 | 审核通过 / 拒绝 / 删除 |
| 密码修改 | 原密码校验 + BCrypt 加密 |
| 数据看板 | 文章总数、分类总数、标签总数、评论总数、待审核数、今日发布、总浏览量 |

---

## 二、目录结构

```
Blog/
├── sql/
│   └── blog_db.sql              # 数据库初始化脚本（含示例数据）
├── blog-backend/                # 后端工程 (Maven)
│   ├── pom.xml
│   └── src/main/resources/
│       └── application.yml
├── blog-frontend/               # 前端工程 (Vite)
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
└── README.md
```

---

## 三、环境要求

- JDK 17+
- Maven 3.8+
- Node.js 16+（建议 18 LTS）
- MySQL 8.0+（用户名 root，密码 123456，地址 localhost:3306；可在 application.yml 修改）

---

## 四、快速启动

### 4.1 初始化数据库

```bash
# 方式一：命令行
mysql -uroot -p123456 < sql/blog_db.sql

# 方式二：Navicat / MySQL Workbench
# 新建查询 → 粘贴 sql/blog_db.sql 内容 → 执行
```

脚本会：

- 创建 `blog_db` 数据库（utf8mb4）
- 创建 6 张表：`t_user / t_category / t_tag / t_article / t_article_tag / t_comment`
- 插入示例分类、标签、文章、评论
- **自动预留 admin 账号**：`admin / 123456`（由后端 `AdminInitializer` 首次启动时以 BCrypt 加密写入）

### 4.2 启动后端

```bash
cd blog-backend
mvn clean spring-boot:run
```

服务地址：http://localhost:8080

可访问：`GET /api/blog/article/list`、`POST /api/admin/login` 等。

### 4.3 启动前端

```bash
cd blog-frontend
npm install
npm run dev
```

访问 http://localhost:5173 即可进入博客前台。

**后台登录地址**：http://localhost:5173/#/admin/login

- 用户名：`admin`
- 密码：`123456`

---

## 五、API 契约一览

> 所有接口统一返回格式：`{ code, message, data, timestamp }`；所有列表接口返回 `{ pageNum, pageSize, total, pages, list }`

### 前台（无需登录）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/blog/article/list?pageNum=1&pageSize=5&keyword=&categoryId=&tagId=` | 文章分页列表 |
| GET | `/api/blog/article/{id}` | 文章详情（浏览量 +1） |
| GET | `/api/blog/category/list` | 全部分类 |
| GET | `/api/blog/tag/list` | 全部标签 |
| GET | `/api/blog/comment/list?articleId=` | 文章评论分页 |
| GET | `/api/blog/message/list` | 留言分页 |
| POST | `/api/blog/comment/submit` | 提交评论/留言（body: nickname, email, content, articleId, parentId） |
| GET | `/api/blog/profile` | 博主简介 & 统计 |

### 后台（需 JWT）

登录后请求头需带：`Authorization: Bearer <token>`

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/admin/login` | 登录 (username, password) |
| POST | `/api/admin/logout` | 登出 |
| GET | `/api/admin/info` | 取当前用户信息 |
| POST | `/api/admin/password` | 修改密码 (oldPassword, newPassword, confirmPassword) |
| GET | `/api/admin/dashboard` | 数据看板 |
| GET/POST | `/api/admin/article/*` | 文章管理 (list/detail/save/update/delete/status) |
| GET/POST | `/api/admin/category/*` | 分类管理 |
| GET/POST | `/api/admin/tag/*` | 标签管理 |
| GET/POST | `/api/admin/comment/*` | 留言/评论审核/删除 |

---

## 六、默认数据库连接

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: "123456"
    driver-class-name: com.mysql.cj.jdbc.Driver
```

可修改 `blog-backend/src/main/resources/application.yml`。

---

## 七、核心机制

### 7.1 鉴权机制（JWT）

- 登录成功后颁发 `Header.Payload.Signature` 三段式 Token（默认 1 天过期）
- `request.js` 请求拦截器自动添加 `Authorization: Bearer xxx`
- `AuthInterceptor` 拦截所有 `/api/admin/**`，Token 无效 / 缺失会返回 401
- 前端识别到 401 后自动清除本地 Token 并跳转至登录页

### 7.2 密码加密

- 采用 Spring Security Crypto 的 `BCryptPasswordEncoder`（cost=10，随机 salt）
- 启动时 `AdminInitializer` 会检查 `admin` 账号是否存在；不存在则插入，存在且非 BCrypt 则重置为 `123456`

### 7.3 分页与防刷

- 后端 MyBatis-Plus 分页插件配置：`maxLimit=500`，防止一次拉取全表
- 关键字搜索使用 MySQL `LIKE '%keyword%'`，且只在标题+摘要范围内检索
- 留言/评论默认 `status=0`（待审核），需后台审核通过后展示

### 7.4 全局异常

- `BusinessException`：业务异常（登录失败、数据不存在等）
- `MethodArgumentNotValidException`：入参校验异常（@Valid + javax/jakarta validation）
- `Exception`：兜底，统一返回 code=500，服务端打印堆栈

---

## 八、常见问题

1. **后端启动报端口占用？**  
   修改 `application.yml` 中 `server.port`，或执行：
   ```bash
   # Windows 查 8080 占用
   netstat -ano | findstr :8080
   # 强制杀进程
   taskkill /F /PID <PID>
   ```

2. **登录失败（用户名或密码错误）？**  
   确认数据库执行过 `blog_db.sql`，且后端启动日志出现 `AdminInitializer ... 创建/重置 admin 账号`。默认账号密码为 admin/123456。

3. **前端请求后端 CORS 报错？**  
   前端 `vite.config.js` 已配置代理 `/api` 转发至 `http://localhost:8080`；后端也通过 `WebMvcConfig.addCorsMappings` 放行所有来源。若通过其他端口部署，请同步调整。

4. **修改前端 API 基础地址？**  
   编辑 `blog-frontend/src/utils/request.js` 中的 `baseURL`。

---

## 九、技术栈汇总

| 分类 | 技术 | 版本 |
|---|---|---|
| 基础框架 | Spring Boot | 3.2.x |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL + mysql-connector-j | 8.0.x |
| 安全 | Spring Security Crypto（BCryptPasswordEncoder） | 6.x |
| Token | JJWT | 0.11.5 |
| 前端框架 | Vue | 3.4.x |
| 构建工具 | Vite | 5.x |
| UI 组件库 | Element Plus | 2.7.x |
| 状态管理 | Pinia | 2.1.x |
| 路由 | Vue Router | 4.3.x |
| Markdown 渲染 | markdown-it | 14.x |

Enjoy coding ✍️
