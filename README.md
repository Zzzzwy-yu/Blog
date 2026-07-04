# 个人技术博客系统

一款基于前后端分离架构的轻量级个人技术博客系统，支持文章管理、分类标签、用户互动、数据看板等完整功能。

## 🏗️ 技术栈

| 分类         | 技术                              | 版本      |
| ---------- | ------------------------------- | ------- |
| 后端框架       | Spring Boot                     | 3.2.5   |
| ORM        | MyBatis-Plus                    | 3.5.5   |
| 数据库        | MySQL                           | 8.0+    |
| 密码加密       | Spring Security Crypto (BCrypt) | 6.x     |
| JWT        | JJWT                            | 0.11.5  |
| 工具库        | Hutool                          | 5.8.25  |
| 代码简化       | Lombok                          | 1.18.30 |
| 前端框架       | Vue                             | 3.4.x   |
| 构建工具       | Vite                            | 5.x     |
| UI组件库      | Element Plus                    | 2.7.x   |
| 状态管理       | Pinia                           | 2.1.x   |
| 路由         | Vue Router                      | 4.3.x   |
| Markdown渲染 | markdown-it                     | 14.x    |
| HTTP客户端    | axios                           | 1.6.x   |
| 数学公式       | Typst-TS                        | 0.7.0   |
| 图标         | Element Plus Icons              | 2.3.x   |

## ✨ 功能特性

### 前台功能

| 功能   | 描述                             |
| ---- | ------------------------------ |
| 文章列表 | 分页展示、分类/标签筛选、关键字搜索             |
| 文章详情 | Markdown渲染、浏览量统计、数学公式支持(Typst) |
| 文章点赞 | 用户登录后可点赞/取消点赞文章                |
| 评论系统 | 评论提交(待审核)、评论点赞、树形展示            |
| 留言板  | 访客留言、二级评论、树形展示                 |
| 关于作者 | 博主资料、文章总数、评论总数、总浏览量            |
| 用户注册 | 邮箱注册、昵称设置                      |
| 用户登录 | JWT认证登录                        |
| 用户中心 | 个人资料编辑                         |

### 后台功能

| 功能    | 描述                           |
| ----- | ---------------------------- |
| 管理员登录 | JWT Token认证                  |
| 数据看板  | 文章/分类/标签/评论统计、待审核数、今日发布、总浏览量 |
| 文章管理  | 新增/编辑/删除、上下架、分类与标签关联         |
| 分类管理  | 增删改查、重名校验、文章关联校验             |
| 标签管理  | 增删改查、重名校验、文章关联校验             |
| 评论管理  | 审核通过/拒绝/删除                   |
| 用户管理  | 用户列表、状态管理                    |
| 图片上传  | 富文本编辑器图片上传支持                 |
| 密码修改  | 原密码校验 + BCrypt加密             |

## 📁 项目结构

```
Blog/
├── sql/                          # 数据库脚本
│   ├── blog_db.sql               # 初始化脚本(含示例数据)
│   ├── add_comment_like.sql      # 评论点赞表
│   ├── add_image_table.sql       # 图片表
│   └── fix_content_type.sql      # 内容类型修复
├── blog-backend/                 # 后端工程(Maven)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/blog/
│       │   ├── controller/       # REST控制器
│       │   ├── service/          # 业务逻辑层
│       │   ├── mapper/           # 数据访问层
│       │   ├── entity/           # 数据库实体
│       │   ├── dto/              # 数据传输对象
│       │   ├── vo/               # 视图对象
│       │   ├── config/           # 配置类
│       │   ├── common/           # 通用组件
│       │   └── utils/            # 工具类
│       └── resources/
│           └── application.yml   # 应用配置
├── blog-frontend/                # 前端工程(Vite)
│   ├── src/
│   │   ├── views/                # 页面组件
│   │   ├── layouts/              # 布局组件
│   │   ├── router/               # 路由配置
│   │   ├── store/                # 状态管理
│   │   ├── api/                  # API接口
│   │   └── utils/                # 工具函数
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
└── README.md
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 16+ (建议 18 LTS)
- MySQL 8.0+

### 1. 初始化数据库

```bash
# 创建数据库并执行初始化脚本
mysql -uroot -p123456 < sql/blog_db.sql
mysql -uroot -p123456 < sql/add_comment_like.sql
mysql -uroot -p123456 < sql/add_image_table.sql
mysql -uroot -p123456 < sql/fix_content_type.sql
```

脚本会创建：

- `blog_db` 数据库 (utf8mb4)
- 9张表：`t_user` / `t_category` / `t_tag` / `t_article` / `t_article_tag` / `t_comment` / `t_article_like` / `t_comment_like` / `t_image`
- 示例分类、标签、文章、评论数据

### 2. 启动后端

```bash
cd blog-backend
mvn clean spring-boot:run
```

服务地址：<http://localhost:8081>

### 3. 启动前端

```bash
cd blog-frontend
npm install
npm run dev
```

访问地址：<http://localhost:5173>

### 默认账号

- **管理员**: `admin` / `123456`
- 首次启动时 `AdminInitializer` 会自动创建/重置admin账号

## 🔌 API接口

所有接口统一返回格式：`{ code, message, data, timestamp }`

### 前台接口（无需登录）

| 方法  | 路径                        | 说明          |
| --- | ------------------------- | ----------- |
| GET | `/api/blog/article/list`  | 文章分页列表      |
| GET | `/api/blog/article/{id}`  | 文章详情(浏览量+1) |
| GET | `/api/blog/category/list` | 全部分类        |
| GET | `/api/blog/tag/list`      | 全部标签        |
| GET | `/api/blog/comment/list`  | 文章评论分页      |
| GET | `/api/blog/message/list`  | 留言分页        |
| GET | `/api/blog/profile`       | 博主简介&统计     |

### 用户接口

| 方法   | 路径                                   | 说明       |
| ---- | ------------------------------------ | -------- |
| POST | `/api/blog/user/register`            | 用户注册     |
| POST | `/api/blog/user/login`               | 用户登录     |
| GET  | `/api/blog/user/info`                | 获取当前用户信息 |
| POST | `/api/blog/user/update`              | 更新用户信息   |
| POST | `/api/blog/comment/submit`           | 提交评论/留言  |
| POST | `/api/blog/article/like/{id}`        | 文章点赞/取消  |
| GET  | `/api/blog/article/like/status/{id}` | 检查文章点赞状态 |
| POST | `/api/blog/comment/like/{id}`        | 评论点赞/取消  |
| GET  | `/api/blog/comment/like/status/{id}` | 检查评论点赞状态 |

### 后台接口（需JWT）

请求头需带：`Authorization: Bearer <token>`

| 方法   | 路径                     | 说明        |
| ---- | ---------------------- | --------- |
| POST | `/api/admin/login`     | 管理员登录     |
| POST | `/api/admin/logout`    | 登出        |
| GET  | `/api/admin/info`      | 获取当前管理员信息 |
| POST | `/api/admin/password`  | 修改密码      |
| GET  | `/api/admin/dashboard` | 数据看板      |

#### 文章管理

| 方法   | 路径                                        | 说明   |
| ---- | ----------------------------------------- | ---- |
| GET  | `/api/admin/article/list`                 | 文章列表 |
| GET  | `/api/admin/article/{id}`                 | 文章详情 |
| POST | `/api/admin/article/save`                 | 保存文章 |
| POST | `/api/admin/article/update`               | 更新文章 |
| POST | `/api/admin/article/delete/{id}`          | 删除文章 |
| POST | `/api/admin/article/status/{id}/{status}` | 切换状态 |

#### 分类/标签管理

| 方法   | 路径                                | 说明   |
| ---- | --------------------------------- | ---- |
| GET  | `/api/admin/category/list`        | 分类列表 |
| POST | `/api/admin/category/save`        | 保存分类 |
| POST | `/api/admin/category/update`      | 更新分类 |
| POST | `/api/admin/category/delete/{id}` | 删除分类 |
| GET  | `/api/admin/tag/list`             | 标签列表 |
| POST | `/api/admin/tag/save`             | 保存标签 |
| POST | `/api/admin/tag/update`           | 更新标签 |
| POST | `/api/admin/tag/delete/{id}`      | 删除标签 |

#### 用户/评论管理

| 方法   | 路径                                     | 说明     |
| ---- | -------------------------------------- | ------ |
| GET  | `/api/admin/user/list`                 | 用户列表   |
| POST | `/api/admin/user/status/{id}/{status}` | 切换用户状态 |
| GET  | `/api/admin/comment/list`              | 评论列表   |
| POST | `/api/admin/comment/approve/{id}`      | 审核通过   |
| POST | `/api/admin/comment/reject/{id}`       | 拒绝审核   |
| POST | `/api/admin/comment/delete/{id}`       | 删除评论   |

#### 图片管理

| 方法   | 路径                             | 说明   |
| ---- | ------------------------------ | ---- |
| POST | `/api/admin/image/upload`      | 上传图片 |
| POST | `/api/admin/image/delete/{id}` | 删除图片 |

## ⚙️ 配置说明

### 数据库配置

默认连接信息（可在 `blog-backend/src/main/resources/application.yml` 修改）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: "123456"
```

### JWT配置

```yaml
token:
  secret: blog_system_jwt_secret_key_for_admin_auth_2025_abcdef123456
  expire: 86400
  header: Authorization
  prefix: "Bearer "
```

## 🔒 安全机制

### JWT认证

- 登录成功后颁发三段式Token（默认1天过期）
- 前端请求拦截器自动添加 `Authorization: Bearer xxx`
- `AuthInterceptor` 拦截所有 `/api/admin/**` 请求，Token无效返回401
- 前端识别到401后自动跳转至登录页

### 密码加密

- 采用 `BCryptPasswordEncoder` (cost=10, 随机salt)
- 管理员账号首次启动时自动创建并加密

### 评论审核

- 留言/评论默认状态为已通过(status=1)，无需审核直接展示

## ⚠️ 常见问题

1. **端口占用？**
   ```bash
   # Windows查看占用
   netstat -ano | findstr :8081
   # 强制杀进程
   taskkill /F /PID <PID>
   ```
2. **登录失败？**
   - 确认执行了所有SQL脚本
   - 检查后端启动日志是否包含 `AdminInitializer` 创建admin账号的信息
3. **CORS报错？**
   - 前端 `vite.config.js` 已配置代理 `/api` 转发至 `http://localhost:8081`
   - 后端通过 `WebMvcConfig.addCorsMappings` 放行所有来源
4. **修改API地址？**
   - 编辑 `blog-frontend/src/utils/request.js` 中的 `baseURL`

## 📝 开发说明

### 后端开发

```bash
# 进入后端目录
cd blog-backend

# 运行项目
mvn spring-boot:run

# 打包
mvn clean package
```

### 前端开发

```bash
# 进入前端目录
cd blog-frontend

# 开发模式
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
```

## 📄 更新日志

### v1.0.1 (2026-07-04)

- ✅ 留言板新增二级评论功能，支持树形展示
- ✅ 修复首页个人卡片昵称/头像闪烁问题
- ✅ 修复启动脚本路径错误
- ✅ 评论/留言默认已通过审核，无需后台审核

### v1.0.0 (2026-06-07)

- 🚀 项目初始化，基础功能完成

Enjoy coding ✍️
