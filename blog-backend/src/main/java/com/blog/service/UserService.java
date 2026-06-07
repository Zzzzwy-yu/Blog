package com.blog.service;

import com.blog.dto.LoginDTO;
import com.blog.dto.PasswordDTO;
import com.blog.entity.User;
import com.blog.vo.LoginVO;

/**
 * 用户Service接口
 *
 * @author blog
 * @since 2024-06-07
 */
public interface UserService {

    /**
     * 管理员登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 根据用户名查询
     */
    User getByUsername(String username);

    /**
     * 根据ID查询
     */
    User getById(Long id);

    /**
     * 修改密码
     */
    void changePassword(String username, PasswordDTO dto);
}
