package com.blog.common;

import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author blog
 * @since 2024-06-07
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(405, "参数校验失败"),
    ERROR(500, "服务器内部错误"),
    LOGIN_ERROR(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "账号已禁用"),
    TOKEN_INVALID(1003, "Token无效或已过期"),
    DATA_NOT_EXIST(1004, "数据不存在"),
    DATA_EXIST(1005, "数据已存在"),
    OPERATION_FAIL(1006, "操作失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
