package com.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(min = 2, max = 50, message = "昵称长度需在2-50个字符之间")
    private String nickname;

    private String avatar;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    @Size(max = 500, message = "个人简介不能超过500个字符")
    private String bio;
}