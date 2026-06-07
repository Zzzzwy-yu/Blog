import request from '@/utils/request'

// 前台接口
export const getArticleList = params =>
  request.get('/blog/article/list', { params })

export const getArticleDetail = id =>
  request.get('/blog/article/' + id)

export const getCategoryList = () =>
  request.get('/blog/category/list')

export const getTagList = () =>
  request.get('/blog/tag/list')

export const getCommentList = params =>
  request.get('/blog/comment/list', { params })

export const getMessageList = params =>
  request.get('/blog/message/list', { params })

export const submitComment = data =>
  request.post('/blog/comment/submit', data)

export const getProfile = () =>
  request.get('/blog/profile')

// 后台接口
export const adminLogin = data =>
  request.post('/admin/login', data)

export const adminLogout = () =>
  request.post('/admin/logout')

export const getAdminInfo = () =>
  request.get('/admin/info')

export const changePassword = data =>
  request.post('/admin/password', data)

export const getDashboard = () =>
  request.get('/admin/dashboard')

// 文章管理
export const getAdminArticleList = params =>
  request.get('/admin/article/list', { params })

export const getAdminArticleDetail = id =>
  request.get('/admin/article/' + id)

export const saveArticle = data =>
  request.post('/admin/article/save', data)

export const updateArticle = data =>
  request.post('/admin/article/update', data)

export const deleteArticle = id =>
  request.post('/admin/article/delete/' + id)

export const switchArticleStatus = (id, status) =>
  request.post('/admin/article/status/' + id + '/' + status)

// 分类管理
export const getAdminCategoryList = () =>
  request.get('/admin/category/list')

export const saveCategory = data =>
  request.post('/admin/category/save', data)

export const updateCategory = data =>
  request.post('/admin/category/update', data)

export const deleteCategory = id =>
  request.post('/admin/category/delete/' + id)

// 标签管理
export const getAdminTagList = () =>
  request.get('/admin/tag/list')

export const saveTag = data =>
  request.post('/admin/tag/save', data)

export const updateTag = data =>
  request.post('/admin/tag/update', data)

export const deleteTag = id =>
  request.post('/admin/tag/delete/' + id)

// 评论/留言管理
export const getAdminCommentList = params =>
  request.get('/admin/comment/list', { params })

export const approveComment = id =>
  request.post('/admin/comment/approve/' + id)

export const rejectComment = id =>
  request.post('/admin/comment/reject/' + id)

export const deleteComment = id =>
  request.post('/admin/comment/delete/' + id)
