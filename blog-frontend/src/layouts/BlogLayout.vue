<template>
  <div>
    <header class="blog-header">
      <div class="blog-header-inner">
        <div style="display:flex;align-items:center;gap:10px;">
          <div style="font-size:24px;">📝</div>
          <h1 class="blog-title">个人技术博客</h1>
        </div>
        <nav class="blog-nav">
          <router-link to="/">首页</router-link>
          <router-link to="/message">留言板</router-link>
          <router-link to="/about">关于我</router-link>
          <router-link to="/admin/login" style="margin-left:10px;background:#fff;color:#667eea;padding:6px 14px;border-radius:4px;">
            后台管理
          </router-link>
        </nav>
      </div>
    </header>
    <div class="blog-container">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </div>
    <footer style="text-align:center;padding:24px;color:#909399;font-size:13px;">
      © 2024 个人技术博客 · Powered by SpringBoot + Vue3
    </footer>
  </div>
</template>

<script setup>
</script>
