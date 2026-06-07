package com.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文章查询DTO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleQueryDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private Long categoryId;

    /** 标签ID */
    private Long tagId;

    /** 状态 0-下架 1-上架 */
    private Integer status;
}
