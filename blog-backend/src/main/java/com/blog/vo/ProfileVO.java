package com.blog.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 个人简介VO(前台页面)
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class ProfileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickname;
    private String avatar;
    private String email;
    private String bio;
    private LocalDateTime createTime;

    /** 统计信息 */
    private Long articleCount;
    private Long commentCount;
    private Long totalView;
}
