package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 评论留言DTO(访客提交)
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章ID(为NULL表示留言) */
    private Long articleId;

    /** 父级评论ID */
    private Long parentId;

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @NotBlank(message = "内容不能为空")
    @Size(max = 1000, message = "内容长度不能超过1000")
    private String content;
}
