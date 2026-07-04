package com.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论查询DTO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    /** 文章ID */
    private Long articleId;

    /** 仅留言(articleId为null) */
    private Boolean onlyMsg;

    /** 状态 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;
}
