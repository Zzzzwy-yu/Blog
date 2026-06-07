<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <strong>{{ form.id ? '编辑文章' : '新建文章' }}</strong>
        <el-button @click="$router.push('/admin/article')">← 返回列表</el-button>
      </div>
    </template>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="可选 - 简短描述" maxlength="500" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:300px;">
          <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-button link type="primary" @click="$router.push('/admin/category')" style="margin-left:8px;">管理分类</el-button>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tagIds" multiple placeholder="请选择标签" style="width:100%;">
          <el-option v-for="t in tagList" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
        <el-button link type="primary" @click="$router.push('/admin/tag')" style="margin-left:8px;">管理标签</el-button>
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">上架</el-radio>
          <el-radio :value="0">下架(草稿)</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="16"
          placeholder="支持 Markdown 语法..."
        />
        <div style="color:#909399;font-size:12px;margin-top:4px;">
          💡 支持标准 Markdown 语法,可在前台预览效果
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">保存</el-button>
        <el-button @click="$router.push('/admin/article')">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  saveArticle, updateArticle, getAdminArticleDetail,
  getAdminCategoryList, getAdminTagList
} from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const articleId = route.query.id

const form = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  categoryId: null,
  tagIds: [],
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const categoryList = ref([])
const tagList = ref([])

const loadCategories = () => {
  getAdminCategoryList().then(res => {
    categoryList.value = res || []
  })
}

const loadTags = () => {
  getAdminTagList().then(res => {
    tagList.value = res || []
  })
}

const loadArticle = () => {
  if (!articleId) return
  getAdminArticleDetail(articleId).then(res => {
    const a = res || {}
    form.id = a.id
    form.title = a.title
    form.summary = a.summary
    form.content = a.content
    form.categoryId = a.categoryId
    form.status = a.status
    form.tagIds = (a.tagList || []).map(t => t.id)
  })
}

const submit = () => {
  formRef.value.validate(valid => {
    if (!valid) return
    loading.value = true
    const api = form.id ? updateArticle : saveArticle
    api(form).then(() => {
      ElMessage.success('保存成功')
      router.push('/admin/article')
    }).finally(() => {
      loading.value = false
    })
  })
}

onMounted(() => {
  loadCategories()
  loadTags()
  loadArticle()
})
</script>
