<script setup>

import CategoryPanel from "#/components/categoryPanel/CategoryPanel.vue";
import {onMounted, ref} from "vue";
import {getLlmBrandList} from "#/api/ai/llm.js";
import HeaderSerch from "#/components/headerSearch/HeaderSerch.vue";
import { Plus, Edit, Delete, Download, Upload, Refresh } from '@element-plus/icons-vue'

const brandListData = ref([])
onMounted(() =>{
  getLlmBrandList().then(res => {
    console.log('res')
    console.log(res)
    brandListData.value = res.data
  })
})

const handleCategoryClick = (category) => {
  console.log('category')
  console.log(category.key)
}

// 按钮配置
const headerButtons = ref([
  {
    key: 'add',
    text: '新增',
    icon: Plus,
    type: 'primary',
    data: { action: 'create' }
  },
  {
    key: 'edit',
    text: '编辑',
    icon: Edit,
    data: { action: 'update' }
  },
  {
    key: 'delete',
    text: '删除',
    icon: Delete,
    type: 'danger',
    disabled: false,
    data: { action: 'delete' }
  },
  {
    key: 'export',
    text: '导出',
    icon: Download,
    data: { format: 'excel' }
  },
  {
    key: 'import',
    text: '导入',
    icon: Upload,
    data: { format: 'excel' }
  },
  {
    key: 'refresh',
    text: '刷新',
    icon: Refresh,
    data: { timestamp: Date.now() }
  }
])

// 处理搜索事件
const handleSearch = (searchValue) => {
  console.log('搜索内容:', searchValue)
  // 执行搜索逻辑
}
// 处理按钮点击事件
const handleButtonClick = (event) => {
  console.log('按钮点击事件:', event)

  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'add':
      handleAdd()
      break
    case 'edit':
      handleEdit()
      break
    case 'delete':
      handleDelete()
      break
    case 'export':
      handleExport()
      break
    case 'import':
      handleImport()
      break
    case 'refresh':
      handleRefresh()
      break
  }
}
</script>

<template>
  <div class="llm-container">
    <div class="llm-header">
      <HeaderSerch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>

    <CategoryPanel :categories="brandListData" title-key="title" :use-img-for-svg="true" :expandWidth="150" @click="handleCategoryClick"/>

  </div>
</template>

<style scoped>
.llm-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}
.llm-header{
  margin-bottom: 20px;
}
</style>
