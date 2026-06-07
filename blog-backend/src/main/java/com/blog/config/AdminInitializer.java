package com.blog.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 系统初始化器 - 启动时保证默认管理员账号存在
 * 若 t_user 中不存在 admin, 会插入一条 (密码 123456, BCrypt 加密)
 * 若已存在但密码不是合法 BCrypt(例如明文), 会重新加密覆盖
 *
 * @author blog
 * @since 2024-06-07
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, "admin");
            User admin = userMapper.selectOne(wrapper);

            // 每次启动都用当前 passwordEncoder 重新生成密码 hash 并写回
            // 避免 SQL 脚本里的占位 hash 与 passwordEncoder 版本不一致导致登录失败
            String newPassword = passwordEncoder.encode("123456");
            LocalDateTime now = LocalDateTime.now();

            if (admin == null) {
                User user = new User();
                user.setUsername("admin");
                user.setPassword(newPassword);
                user.setNickname("博客管理员");
                user.setBio("欢迎来到我的技术博客,专注分享Java开发、前端技术、架构设计等内容。");
                user.setRole(2);
                user.setStatus(1);
                user.setCreateTime(now);
                user.setUpdateTime(now);
                userMapper.insert(user);
                log.info("[AdminInitializer] 默认管理员 admin 已创建, 初始密码 123456");
            } else {
                boolean needUpdate = false;
                // 强制用 passwordEncoder 重新生成的 hash 覆盖, 保证与当前实现一致
                if (!newPassword.equals(admin.getPassword())) {
                    admin.setPassword(newPassword);
                    needUpdate = true;
                }
                if (admin.getRole() == null || admin.getRole() != 2) {
                    admin.setRole(2);
                    needUpdate = true;
                }
                if (admin.getStatus() == null || admin.getStatus() != 1) {
                    admin.setStatus(1);
                    needUpdate = true;
                }
                if (needUpdate) {
                    admin.setUpdateTime(now);
                    userMapper.updateById(admin);
                    log.info("[AdminInitializer] admin 账号已同步, 密码统一为 123456");
                }
            }
        } catch (Exception e) {
            // 若数据库还未就绪, 仅记录日志不中断启动
            String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            log.warn("[AdminInitializer] 初始化管理员失败(已忽略,不影响启动): {}", msg);
        }
    }
}
