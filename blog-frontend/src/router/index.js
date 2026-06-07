import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  // 前台
  {
    path: '/',
    component: () => import('@/layouts/BlogLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/blog/Home.vue'), meta: { title: '首页' } },
      { path: 'article/:id', name: 'articleDetail', component: () => import('@/views/blog/ArticleDetail.vue'), meta: { title: '文章详情' } },
      { path: 'message', name: 'message', component: () => import('@/views/blog/Message.vue'), meta: { title: '留言板' } },
      { path: 'about', name: 'about', component: () => import('@/views/blog/About.vue'), meta: { title: '关于我' } }
    ]
  },
  // 后台登录
  { path: '/admin/login', name: 'adminLogin', component: () => import('@/views/admin/Login.vue'), meta: { title: '后台登录' } },
  // 后台
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: '数据看板' } },
      { path: 'article', name: 'adminArticle', component: () => import('@/views/admin/ArticleList.vue'), meta: { title: '文章管理' } },
      { path: 'article/edit', name: 'articleEdit', component: () => import('@/views/admin/ArticleEdit.vue'), meta: { title: '编辑文章' } },
      { path: 'category', name: 'adminCategory', component: () => import('@/views/admin/CategoryList.vue'), meta: { title: '分类管理' } },
      { path: 'tag', name: 'adminTag', component: () => import('@/views/admin/TagList.vue'), meta: { title: '标签管理' } },
      { path: 'comment', name: 'adminComment', component: () => import('@/views/admin/CommentList.vue'), meta: { title: '留言评论管理' } },
      { path: 'password', name: 'adminPassword', component: () => import('@/views/admin/Password.vue'), meta: { title: '修改密码' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = (to.meta?.title || '博客') + ' - 个人技术博客'
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('blog_token')
    if (!token) {
      next('/admin/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
