package com.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 博客系统启动类
 *
 * @author blog
 * @since 2024-06-07
 */
@SpringBootApplication
@MapperScan("com.blog.mapper")
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
        System.out.println("=======================================");
        System.out.println("  个人技术博客系统启动成功!");
        System.out.println("  前端访问: http://localhost:5173");
        System.out.println("  后端接口: http://localhost:8081");
        System.out.println("=======================================");
    }
}
