<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <strong>分类管理</strong>
        <el-button type="primary" @click="openDialog(null)">
          <el-icon><Plus /></el-icon> 新建分类
        </el-button>
      </div>
    </template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="180">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="current.id ? '编辑分类' : '新建分类'" width="480px">
    <el-form :model="current" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="current.name" maxlength="50" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="current.description" type="textarea" :rows="2" maxlength="255" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="current.sortOrder" :min="0" :max="9999" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="current.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAdminCategoryList, saveCategory, updateCategory, deleteCategory } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const current = reactive({ id: null, name: '', description: '', sortOrder: 0, status: 1 })
const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

const fetchList = () => {
  loading.value = true
  getAdminCategoryList().then(res => {
    list.value = res || []
  }).finally(() => {
    loading.value = false
  })
}

const openDialog = row => {
  if (row) {
    current.id = row.id
    current.name = row.name
    current.description = row.description
    current.sortOrder = row.sortOrder
    current.status = row.status
  } else {
    current.id = null
    current.name = ''
    current.description = ''
    current.sortOrder = 0
    current.status = 1
  }
  dialogVisible.value = true
}

const submit = () => {
  formRef.value.validate(valid => {
    if (!valid) return
    loading.value = true
    const api = current.id ? updateCategory : saveCategory
    api(current).then(() => {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      fetchList()
    }).finally(() => {
      loading.value = false
    })
  })
}

const remove = row => {
  ElMessageBox.confirm('确认删除分类《' + row.name + '》?', '提示', { type: 'warning' })
    .then(() => {
      deleteCategory(row.id).then(() => {
        ElMessage.success('已删除')
        fetchList()
      })
    }).catch(() => {})
}

onMounted(fetchList)
</script>
