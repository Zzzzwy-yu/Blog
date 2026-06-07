<template>
  <div class="blog-layout">
    <div class="blog-main">
      <div class="card">
        <div class="card-title">👋 关于我</div>
        <div v-if="profile">
          <div style="display:flex;align-items:center;gap:24px;margin-bottom:20px;">
            <div class="profile-avatar" style="width:120px;height:120px;font-size:48px;">
              {{ profile.nickname ? profile.nickname.charAt(0).toUpperCase() : 'B' }}
            </div>
            <div>
              <h2 style="margin:0 0 8px;">{{ profile.nickname || '博主' }}</h2>
              <p style="color:#909399;margin:0 0 8px;">📧 {{ profile.email || '暂未设置' }}</p>
              <p style="color:#606266;line-height:1.8;">{{ profile.bio || '欢迎来到我的技术博客' }}</p>
            </div>
          </div>

          <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px;margin:24px 0;">
            <div style="background:#ecf5ff;padding:20px;border-radius:8px;text-align:center;">
              <div style="font-size:32px;font-weight:700;color:#409eff;">{{ profile.articleCount || 0 }}</div>
              <div style="color:#606266;font-size:14px;">文章数量</div>
            </div>
            <div style="background:#f0f9eb;padding:20px;border-radius:8px;text-align:center;">
              <div style="font-size:32px;font-weight:700;color:#67c23a;">{{ profile.commentCount || 0 }}</div>
              <div style="color:#606266;font-size:14px;">评论数量</div>
            </div>
            <div style="background:#fdf6ec;padding:20px;border-radius:8px;text-align:center;">
              <div style="font-size:32px;font-weight:700;color:#e6a23c;">{{ profile.totalView || 0 }}</div>
              <div style="color:#606266;font-size:14px;">总浏览量</div>
            </div>
          </div>

          <h3 style="margin-top:30px;">✍ 写作理念</h3>
          <p style="color:#606266;line-height:1.8;">
            记录学习与工作中的点点滴滴,分享技术文章,交流经验。
            致力于 Java 开发、前端技术、系统架构的持续探索。
          </p>
          <p style="color:#606266;line-height:1.8;">
            技术改变世界,分享让世界更美好。欢迎通过留言板与我交流!
          </p>
        </div>
      </div>
    </div>

    <aside class="blog-side">
      <div class="card">
        <div class="card-title">📌 快速导航</div>
        <el-button type="primary" link @click="$router.push('/')">→ 返回首页</el-button><br />
        <el-button type="success" link @click="$router.push('/message')">→ 去留言板</el-button>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProfile } from '@/api'

const profile = ref({})

onMounted(() => {
  getProfile().then(res => {
    profile.value = res || {}
  })
})
</script>
