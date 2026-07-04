-- =============================================
-- 个人博客系统 - 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 注意: 使用 CREATE TABLE IF NOT EXISTS 保留已有数据
-- =============================================

-- 1. 创建数据库(如果不存在)
CREATE DATABASE IF NOT EXISTS blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE blog_db;

-- =============================================
-- 2. 用户表 (管理员账号)
-- =============================================
CREATE TABLE IF NOT EXISTS t_user (
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
-- 使用 INSERT IGNORE 避免重复插入
INSERT IGNORE INTO t_user(username, password, nickname, bio, role, status)
VALUES ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Zzzzwy',
        '欢迎来到我的博客!', 2, 1);

-- =============================================
-- 3. 文章分类表
-- =============================================
CREATE TABLE IF NOT EXISTS t_category (
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



-- =============================================
-- 4. 文章标签表
-- =============================================
CREATE TABLE IF NOT EXISTS t_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '标签描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签表';



-- =============================================
-- 5. 博客文章表
-- =============================================
CREATE TABLE IF NOT EXISTS t_article (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    content LONGTEXT NOT NULL COMMENT '文章内容(Markdown)',
    content_type VARCHAR(20) DEFAULT 'markdown' COMMENT '内容类型',
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

-- =============================================
-- 6. 文章-标签多对多关联表
-- =============================================
CREATE TABLE IF NOT EXISTS t_article_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_tag (article_id, tag_id),
    KEY idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- =============================================
-- 7. 留言评论表
-- =============================================
CREATE TABLE IF NOT EXISTS t_comment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    article_id BIGINT DEFAULT NULL COMMENT '文章ID(为NULL则为留言)',
    parent_id BIGINT DEFAULT NULL COMMENT '父级评论ID(用于回复)',
    nickname VARCHAR(50) NOT NULL COMMENT '评论者昵称',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-待审核 1-已通过 2-已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_article_id (article_id),
    KEY idx_parent_id (parent_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言评论表';

-- =============================================
-- 8. 评论点赞表
-- =============================================
CREATE TABLE IF NOT EXISTS t_comment_like (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_comment_user (comment_id, user_id),
    KEY idx_comment_id (comment_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- =============================================
-- 9. 文章点赞表
-- =============================================
CREATE TABLE IF NOT EXISTS t_article_like (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_user (article_id, user_id),
    KEY idx_article_id (article_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞表';


