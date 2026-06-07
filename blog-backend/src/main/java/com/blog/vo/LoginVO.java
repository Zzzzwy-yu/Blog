package com.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功返回VO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Token */
    private String token;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;
}
