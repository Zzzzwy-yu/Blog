package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章发布/编辑DTO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class ArticleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章ID(编辑时传) */
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200")
    private String title;

    @Size(max = 500, message = "摘要长度不能超过500")
    private String summary;

    @NotBlank(message = "内容不能为空")
    private String content;

    /** 内容类型: markdown/typst */
    private String contentType = "markdown";

    private String coverImage;

    /** 分类ID */
    private Long categoryId;

    /** 标签ID列表 */
    private List<Long> tagIds;

    /** 状态 0-下架 1-上架 */
    private Integer status = 1;
}
