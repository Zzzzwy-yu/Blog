USE blog_db;

CREATE TABLE IF NOT EXISTS t_image (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_ext VARCHAR(20) COMMENT '文件扩展名',
    content_type VARCHAR(100) COMMENT 'MIME类型',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    data LONGBLOB NOT NULL COMMENT '图片二进制数据',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片存储表';
