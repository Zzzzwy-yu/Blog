package com.blog.service;

import com.blog.dto.LoginDTO;
import com.blog.dto.PasswordDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.UserInfoDTO;
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
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 普通用户登录
     */
    LoginVO userLogin(LoginDTO dto);

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

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, UserInfoDTO dto);

    /**
     * 分页查询用户列表
     */
    com.blog.common.PageResult<User> pageList(com.blog.dto.PageQueryDTO dto);

    /**
     * 更新用户状态
     */
    void updateStatus(Long id, Integer status);
}
