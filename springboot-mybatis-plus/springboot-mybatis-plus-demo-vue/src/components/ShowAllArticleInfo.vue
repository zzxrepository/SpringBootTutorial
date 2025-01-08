<template>
  <div class="user-info">
    <h3 class="section-title">方式1</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreByPagination.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreByPagination.currentPage"
        :page-size="articleStoreByPagination.pageSize"
        :total="articleStoreByPagination.total"
        layout="prev, pager, next"
        @current-change="loadPageByPagination"
        background
        size="small"
        class="custom-pagination mt-4"
    />

    <h3 class="section-title">方式2</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreByParam.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreByParam.currentPage"
        :page-size="articleStoreByParam.pageSize"
        :total="articleStoreByParam.total"
        layout="prev, pager, next"
        @current-change="loadPageByParam"
        background
        size="small"
        class="custom-pagination mt-4"
    />

    <h3 class="section-title">方式3</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreBySelf.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreBySelf.currentPage"
        :page-size="articleStoreBySelf.pageSize"
        :total="articleStoreBySelf.total"
        layout="prev, pager, next"
        @current-change="loadPageBySelf"
        background
        size="small"
        class="custom-pagination mt-4"
    />

    <h3 class="section-title">方式4</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreByPageHelper.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreByPageHelper.currentPage"
        :page-size="articleStoreByPageHelper.pageSize"
        :total="articleStoreByPageHelper.total"
        layout="prev, pager, next"
        @current-change="loadPageByPageHelper"
        background
        size="small"
        class="custom-pagination mt-4"
    />

  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useArticleStoreByPagination,useArticleStoreByParam,useArticleStoreBySelf, useArticleStoreByPageHelper } from "../store/articleStore";

// 使用 Pinia store
const articleStoreByPagination= useArticleStoreByPagination();
const articleStoreByParam = useArticleStoreByParam();
const articleStoreBySelf = useArticleStoreBySelf();
const articleStoreByPageHelper = useArticleStoreByPageHelper();

// 加载指定页的数据
const loadPageByPagination = (page) => {
  articleStoreByPagination.fetchArticleInfo(page);  // 更新当前页并获取数据
};

// 加载指定页的数据
const loadPageByParam = (page) => {
  articleStoreByParam.fetchArticleInfo(page);  // 更新当前页并获取数据
};

// 加载指定页的数据
const loadPageBySelf = (page) => {
  articleStoreBySelf.fetchArticleInfo(page);  // 更新当前页并获取数据
};

// 加载指定页的数据
const loadPageByPageHelper = (page) => {
  articleStoreByPageHelper.fetchArticleInfo(page);  // 更新当前页并获取数据
};



// 在组件挂载时获取第一页数据
onMounted(() => {
  loadPageByPagination();  // 初始加载第一页数据
  loadPageByParam(1);  // 初始加载第一页数据
  loadPageBySelf(1);  // 初始加载第一页数据
  loadPageByPageHelper(1);  // 初始加载第一页数据
});
</script>

<style scoped>
.user-info {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  max-width: 1200px;
  margin: 40px auto;
}

.section-title {
  text-align: center;
  font-size: 24px;
  color: #333333;
  margin-bottom: 20px;
  font-weight: bold;
}

.article-table {
  width: 100%;
  margin-bottom: 20px;
}

.article-table th {
  background-color: #f5f7fa;
  color: #333333;
  font-weight: 500;
  text-align: center;
}

.article-table td {
  text-align: center;
  padding: 12px 8px; /* 增加内边距以提升可读性 */
}

.article-table tr:hover {
  background-color: #f1f1f1; /* 添加悬停效果 */
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}

.custom-pagination {
  /* 自定义分页样式 */
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.custom-pagination .el-pager li.active {
  background-color: #409EFF; /* 激活页码背景色 */
}

.custom-pagination .el-pagination__prev,
.custom-pagination .el-pagination__next,
.custom-pagination .el-pagination__jump,
.custom-pagination .el-pagination__total {
  color: #606266;
}

.custom-pagination .el-pagination__jump input {
  width: 60px;
  text-align: center;
}

.mt-4 {
  margin-top: 1rem; /* 添加额外的顶部间距 */
}

@media (max-width: 768px) {
  .user-info {
    padding: 15px;
    margin: 20px auto;
  }

  .section-title {
    font-size: 20px;
    margin-bottom: 15px;
  }

  .article-table th,
  .article-table td {
    padding: 10px 5px;
    font-size: 14px;
  }

  .pagination-container {
    padding: 8px 0;
  }
}
</style>


