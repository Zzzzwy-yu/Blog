import { defineStore } from 'pinia'

// 用户状态管理
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('blog_token') || '',
    userInfo: JSON.parse(localStorage.getItem('blog_user') || 'null')
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('blog_token', token)
    },
    setUserInfo(user) {
      this.userInfo = user
      localStorage.setItem('blog_user', JSON.stringify(user))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('blog_token')
      localStorage.removeItem('blog_user')
    }
  }
})
