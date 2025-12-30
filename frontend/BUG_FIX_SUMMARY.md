# 医生端Bug修复总结

## 已修复的问题 ✅

### 1. Vue Router 警告 ✅
**问题：** `The route named "DoctorLayout" has a child without a name and an empty path`

**修复：** 
- 将路由配置中的 `name: 'DoctorLayout'` 从父路由移到了空路径的子路由
- 文件：`frontend/src/router/index.js`

**修改前：**
```js
{
  path: '/doctor',
  name: 'DoctorLayout',  // ❌ 在父路由
  component: () => import('@/layouts/DoctorLayout.vue'),
  children: [
    {
      path: '',  // 空路径没有name
      redirect: '/doctor/home'
    }
  ]
}
```

**修改后：**
```js
{
  path: '/doctor',
  component: () => import('@/layouts/DoctorLayout.vue'),
  children: [
    {
      path: '',
      name: 'DoctorLayout',  // ✅ 移到子路由
      redirect: '/doctor/home'
    }
  ]
}
```

---

### 2. 医生信息和科室不显示 ✅
**问题：** 下拉菜单中显示"医生"和"科室"，没有实际数据

**修复：**
1. 添加了 `getMyDoctorInfo()` API函数
2. 在 `DoctorLayout.vue` 的 `onMounted` 中调用API获取完整医生信息
3. API地址：`/api/doctor/profile`

**代码变更：**
- 文件：`frontend/src/api/doctor.js`
- 文件：`frontend/src/layouts/DoctorLayout.vue`

```js
// 在 onMounted 中获取医生信息
onMounted(async () => {
  // 先从 userStore 获取基本信息
  if (userStore.userInfo) {
    doctorInfo.value.name = userStore.userInfo.realName || '医生';
  }
  
  // 获取完整信息（包括科室）
  try {
    const data = await getMyDoctorInfo();
    if (data) {
      doctorInfo.value.name = data.name || doctorInfo.value.name;
      doctorInfo.value.departmentName = data.departmentName || '科室';
      doctorInfo.value.avatarUrl = data.avatarUrl || defaultAvatar;
    }
  } catch (error) {
    console.error('获取医生信息失败:', error);
    // 失败时使用默认值
    if (!doctorInfo.value.departmentName) {
      doctorInfo.value.departmentName = '科室';
    }
  }
});
```

---

## 需要后端检查的问题 ⚠️

### 请假管理500错误
**错误信息：** `GET /api/doctor/leaves/my?page=0&size=10 500 (Internal Server Error)`

**可能原因：**
1. 数据库中没有医生数据（Doctor表为空）
2. 当前登录用户的userId在Doctor表中找不到对应记录
3. 数据库连接问题

**后端代码位置：**
- Controller: `DoctorLeaveController.java` - Line 33-40
- Service: `DoctorLeaveService.java` - Line 65-74

**建议检查：**
```java
// DoctorLeaveService.java - Line 67
Doctor doctor = doctorRepository.findByUserId(userId)
    .orElseThrow(() -> new BusinessException("医生信息不存在"));
```

**排查步骤：**
1. 检查后端日志，看具体错误信息
2. 确认数据库中是否有医生数据
3. 确认登录的用户ID与Doctor表中的userId字段匹配

---

## 测试步骤

### 1. 测试医生信息显示
1. 以医生身份登录系统
2. 查看右上角下拉菜单
3. **预期结果：** 应显示医生姓名和科室名称

### 2. 测试请假管理
1. 点击导航栏"请假管理"
2. **如果显示错误：** 需要检查后端日志
3. **如果正常：** 应该能看到请假记录列表（可能为空）

---

## 需要的数据库数据

### 确保以下数据存在：

#### 1. User 表
```sql
SELECT * FROM users WHERE role = 'DOCTOR';
```

#### 2. Doctor 表
```sql
SELECT d.*, dept.name as department_name 
FROM doctors d 
LEFT JOIN departments dept ON d.department_id = dept.id;
```

#### 3. 数据关联
确保：
- `doctors.user_id` 对应 `users.id`
- `doctors.department_id` 对应 `departments.id`

---

## 快速修复建议

如果数据库中没有医生数据，可以：

### 1. 创建测试数据
```sql
-- 假设已有一个用户ID为 'user123'
INSERT INTO doctors (id, user_id, department_id, name, title, introduction, specialization) 
VALUES (
  'doc123', 
  'user123', 
  'dept001',  -- 需要确保这个科室存在
  '张医生', 
  '主任医师', 
  '擅长内科疾病诊治', 
  '内科,心血管'
);
```

### 2. 或者通过管理员界面创建医生

---

## 浏览器控制台检查

打开浏览器开发者工具 (F12)，查看：

### Network 标签页
- 查看 `/api/doctor/profile` 请求是否成功（应返回200）
- 查看 `/api/doctor/leaves/my` 请求错误详情

### Console 标签页
- 应该没有 Vue Router 警告了 ✅
- 如果有其他错误，记录下来

---

## 修改的文件清单

1. ✅ `frontend/src/router/index.js` - 修复路由警告
2. ✅ `frontend/src/api/doctor.js` - 添加获取医生信息API
3. ✅ `frontend/src/layouts/DoctorLayout.vue` - 获取并显示医生信息

---

## 后续优化建议

1. **错误处理：** 在前端添加更友好的错误提示
2. **Loading状态：** 在获取医生信息时显示加载动画
3. **缓存：** 考虑缓存医生信息，避免频繁请求
4. **默认头像：** 提供更好的默认头像

---

**修复完成时间：** 2025-12-29
**修复人员：** AI Assistant
**状态：** 前端修复完成，等待后端数据验证

