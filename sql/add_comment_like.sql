USE blog_db;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS add_comment_like_columns()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='blog_db' AND table_name='t_comment' AND column_name='like_count') THEN
        ALTER TABLE t_comment ADD COLUMN like_count INT DEFAULT 0 COMMENT '点赞数' AFTER user_agent;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema='blog_db' AND table_name='t_comment' AND index_name='idx_parent_id') THEN
        ALTER TABLE t_comment ADD INDEX idx_parent_id (parent_id);
    END IF;
END //
DELIMITER ;

CALL add_comment_like_columns();
DROP PROCEDURE IF EXISTS add_comment_like_columns;

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
