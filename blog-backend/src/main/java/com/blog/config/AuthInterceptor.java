package com.blog.config;

import com.blog.common.Result;
import com.blog.common.ResultCode;
import com.blog.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 权限拦截器: 校验管理员JWT Token
 *
 * @author blog
 * @since 2024-06-07
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${token.header:Authorization}")
    private String header;

    @Value("${token.prefix:Bearer }")
    private String prefix;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        // 放行OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(header);
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        if (token == null || token.isEmpty()) {
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 移除前缀 Bearer
        String realPrefix = prefix.trim();
        if (token.startsWith(realPrefix)) {
            token = token.substring(realPrefix.length()).trim();
        }

        if (!jwtUtil.validateToken(token)) {
            writeError(response, ResultCode.TOKEN_INVALID);
            return false;
        }

        // 将用户信息放到请求属性中,方便后续使用
        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("currentUser", username);
        return true;
    }

    private void writeError(HttpServletResponse response, ResultCode code) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(Result.error(code)));
        writer.flush();
        writer.close();
    }
}
