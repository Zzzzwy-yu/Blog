package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
@TableName("t_article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** 内容(Markdown) */
    private String content;

    /** 封面图片 */
    private String coverImage;

    /** 分类ID */
    private Long categoryId;

    /** 作者 */
    private String author;

    /** 浏览量 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 评论数 */
    private Integer commentCount;

    /** 状态 0-下架 1-上架 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /** 标签列表(非表字段) */
    @TableField(exist = false)
    private List<Tag> tagList;

    /** 标签ID列表(非表字段) */
    @TableField(exist = false)
    private List<Long> tagIds;

    /** 分类名称(非表字段) */
    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private Integer deleted;
}
