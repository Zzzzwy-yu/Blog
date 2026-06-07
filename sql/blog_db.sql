-- =============================================
-- 个人技术博客系统 - 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- =============================================

-- 1. 创建数据库
DROP DATABASE IF EXISTS blog_db;
CREATE DATABASE blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE blog_db;

-- =============================================
-- 2. 用户表 (管理员账号)
-- =============================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    bio VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    role TINYINT DEFAULT 1 COMMENT '角色: 1-普通用户 2-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始化管理员账号 (密码: 123456, BCrypt加密值)
INSERT INTO t_user(username, password, nickname, bio, role, status)
VALUES ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '博客管理员',
        '欢迎来到我的技术博客，专注分享Java开发、前端技术、架构设计等内容。', 2, 1);

-- =============================================
-- 3. 文章分类表
-- =============================================
DROP TABLE IF EXISTS t_category;
CREATE TABLE t_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

INSERT INTO t_category(name, description, sort_order) VALUES
('Java开发', 'Java基础、Spring、SpringBoot等相关内容', 1),
('前端技术', 'Vue、React、JavaScript等前端内容', 2),
('架构设计', '系统架构、微服务、高并发设计', 3),
('数据库', 'MySQL、Redis、MongoDB等数据库相关', 4),
('工具资源', '开发工具、效率软件、资源分享', 5);

-- =============================================
-- 4. 文章标签表
-- =============================================
DROP TABLE IF EXISTS t_tag;
CREATE TABLE t_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '标签描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签表';

INSERT INTO t_tag(name) VALUES
('SpringBoot'),('Spring'),('MyBatis'),('Vue3'),('ElementPlus'),
('MySQL'),('Redis'),('Java8'),('TypeScript'),('Vite');

-- =============================================
-- 5. 博客文章表
-- =============================================
DROP TABLE IF EXISTS t_article;
CREATE TABLE t_article (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    content LONGTEXT NOT NULL COMMENT '文章内容(Markdown)',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    author VARCHAR(50) DEFAULT 'admin' COMMENT '作者',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架(草稿) 1-上架(发布)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time),
    FULLTEXT KEY idx_ft_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客文章表';

-- 示例文章数据
INSERT INTO t_article(title, summary, content, category_id, status) VALUES
('SpringBoot3入门指南', '介绍SpringBoot3的新特性和快速开始方法',
 '# SpringBoot3入门指南\n\nSpringBoot3基于Spring Framework 6，要求Java 17+。\n\n## 主要特性\n- 支持Jakarta EE 9+ API\n- 增强的AOT处理与GraalVM原生镜像\n- 更现代化的依赖', 1, 1),
('Vue3组合式API详解', '深入理解Vue3的Composition API',
 '# Vue3组合式API\n\n组合式API是Vue3的核心亮点，通过setup函数组织逻辑。\n\n## 核心API\n- ref / reactive\n- computed\n- watch / watchEffect', 2, 1),
('MySQL索引优化实战', '分享MySQL索引使用的最佳实践',
 '# MySQL索引优化\n\n合理使用索引可以极大提升查询性能。\n\n## 索引类型\n- 主键索引\n- 唯一索引\n- 普通索引\n- 全文索引', 4, 1),
('Element Plus组件库使用', 'Element Plus组件库快速上手',
 '# Element Plus\n\nElement Plus 是基于 Vue 3 的组件库。\n\n```bash\nnpm install element-plus\n```', 2, 1);

-- =============================================
-- 6. 文章-标签多对多关联表
-- =============================================
DROP TABLE IF EXISTS t_article_tag;
CREATE TABLE t_article_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_tag (article_id, tag_id),
    KEY idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

INSERT INTO t_article_tag(article_id, tag_id) VALUES
(1,1),(1,2),(1,8),
(2,4),(2,9),
(3,6),
(4,4),(4,5),(2,5);

-- =============================================
-- 7. 留言评论表
-- =============================================
DROP TABLE IF EXISTS t_comment;
CREATE TABLE t_comment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    article_id BIGINT DEFAULT NULL COMMENT '文章ID(为NULL则为留言)',
    parent_id BIGINT DEFAULT NULL COMMENT '父级评论ID(用于回复)',
    nickname VARCHAR(50) NOT NULL COMMENT '评论者昵称',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-待审核 1-已通过 2-已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_article_id (article_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言评论表';

INSERT INTO t_comment(article_id, nickname, content, status) VALUES
(NULL, '访客A', '博主的文章写得真好，学到很多！', 1),
(1, '开发者小王', 'SpringBoot3 确实好用，期待更多内容。', 1),
(2, '前端爱好者', '组合式API真香！', 1),
(NULL, '路人甲', '这博客做得真不错，支持一下。', 0);
