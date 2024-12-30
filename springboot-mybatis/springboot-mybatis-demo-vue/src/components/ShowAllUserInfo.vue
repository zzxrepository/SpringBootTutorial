<template>
  <div class="user-info">
    <h2>用户信息</h2>
    <table class="user-table">
      <thead>
        <tr>
          <th>用户名</th>
          <th>年龄</th>
          <th>性别</th>
        </tr>
      </thead>
      <tbody>
        <!-- 使用 v-for 循环显示多个用户，每行显示一个用户的属性 -->
        <tr v-for="user in userStore.users" :key="user.id">
          <td>{{ user.username }}</td>
          <td>{{ user.age }}</td>
          <td>{{ user.gender === 1 ? '男' : '女' }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../store/userStore'  // 引入 Pinia store

// 使用 Pinia store
const userStore = useUserStore()

// 计算属性，根据 gender 字段返回对应的文字
const genderText = computed(() => {
  return userStore.users.length && userStore.users[0].gender === 1 ? '男' : '女'
})

// 在组件挂载时获取用户信息
onMounted(() => {
  userStore.fetchUserInfo()  // 获取用户信息
})
</script>

<style scoped>
.b1b {
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin: 10px;
}

.b1b:hover {
  background-color: #0056b3;
}

.user-info {
  margin-top: 30px;
  text-align: center;  /* 使整个 .user-info 区域的内容居中 */
}

.user-info h2 {
  text-align: center;  /* 使标题居中 */
}

.user-table {
  width: 80%;
  margin: 0 auto;
  border-collapse: collapse;
}

.user-table th, .user-table td {
  padding: 8px 16px;
  border: 1px solid #ccc;
  text-align: center;
}

.user-table th {
  background-color: #f4f4f4;
}

.user-table tr:nth-child(even) {
  background-color: #f9f9f9;
}
</style>
