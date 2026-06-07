package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 通用 Bean 配置
 *
 * @author blog
 * @since 2024-06-07
 */
@Configuration
public class BeanConfig {

    /**
     * BCrypt 密码加密器 - 使用固定版本 + 固定随机盐
     * 注: SpringBoot 3 + Spring Security Crypto 6.x 已原生支持 BCrypt
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
