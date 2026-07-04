package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论留言实体类
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
@TableName("t_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章ID,为NULL表示为留言 */
    private Long articleId;

    /** 父级评论ID(用于回复) */
    private Long parentId;

    /** 评论者昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 内容 */
    private String content;

    /** IP地址 */
    private String ip;

    /** 用户代理 */
    private String userAgent;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /** 文章标题(非表字段) */
    @TableField(exist = false)
    private String articleTitle;

    /** 子评论列表(非表字段) */
    @TableField(exist = false)
    private java.util.List<Comment> children;

    /** 被点赞状态(非表字段) */
    @TableField(exist = false)
    private Boolean liked;

    @TableField(exist = false)
    private Integer deleted;
}
