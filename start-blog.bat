@echo off
chcp 65001
title 启动博客前后端

:: 1. 启动 MySQL 服务（如果是本地安装的）
echo 正在启动 MySQL 服务...
net start MySQL80
echo MySQL 服务启动完成！

:: 2. 启动后端 SpringBoot
echo 正在启动后端服务...
start "Blog-Backend" cmd /k "cd /d C:\Users\Administrator\Documents\trae_projects\Blog\blog-backend && mvn spring-boot:run"

:: 3. 等待3秒，让后端先启动
timeout /t 3 /nobreak >nul

:: 4. 启动前端 Vite
echo 正在启动前端服务...
start "Blog-Frontend" cmd /k "cd /d C:\Users\Administrator\Documents\trae_projects\Blog\blog-frontend && npm run dev"

echo 博客服务已启动！浏览器访问 http://localhost:5173 即可打开。
pause