# 医生评价和评分系统说明

## 📊 系统架构

### 1. 医生评分如何工作？

医生**没有**固定的评分字段，评分是**动态计算**的！

```
医生评分 = doctor_reviews 表中所有评价的平均分
评价数量 = doctor_reviews 表中该医生的评价总数
```

### 2. 数据库表结构

#### `doctor_reviews` 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 评价ID |
| doctor_id | VARCHAR(36) | 医生ID（外键） |
| patient_id | VARCHAR(36) | 患者ID（外键） |
| rating | INT | 评分（1-5星） |
| content | TEXT | 评价内容 |
| created_at | DATETIME | 创建时间 |

---

## 🔧 数据库设置

### 步骤1: 创建 `doctor_reviews` 表

```sql
CREATE TABLE IF NOT EXISTS `doctor_reviews` (
    `id` VARCHAR(36) PRIMARY KEY COMMENT '评价ID',
    `doctor_id` VARCHAR(36) NOT NULL COMMENT '医生ID',
    `patient_id` VARCHAR(36) NOT NULL COMMENT '患者ID',
    `rating` INT NOT NULL COMMENT '评分(1-5)',
    `content` TEXT COMMENT '评价内容',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (`doctor_id`) REFERENCES `doctors`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`patient_id`) REFERENCES `patients`(`id`) ON DELETE CASCADE,
    INDEX `idx_doctor_reviews_doctor_id` (`doctor_id`),
    INDEX `idx_doctor_reviews_patient_id` (`patient_id`),
    INDEX `idx_doctor_reviews_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生评价表';
```

### 步骤2: 插入测试数据

```sql
-- 插入测试评价（根据你的医生ID调整）
INSERT INTO `doctor_reviews` (`id`, `doctor_id`, `patient_id`, `rating`, `content`, `created_at`) VALUES
-- 李医生 (doc-001) 的评价 - 平均分 4.67
('rev-001', 'doc-001', 'pat-001', 5, '李医生非常专业，对病情分析很透彻，态度也很好。', CURDATE() - INTERVAL 5 DAY),
('rev-002', 'doc-001', 'pat-002', 4, '医生很有经验，但是等待时间有点长。', CURDATE() - INTERVAL 10 DAY),
('rev-003', 'doc-001', 'pat-003', 5, '非常满意，医生很耐心地解答了我的所有问题。', CURDATE() - INTERVAL 15 DAY),

-- 张医生 (doc-002) 的评价 - 平均分 4.67
('rev-004', 'doc-002', 'pat-002', 5, '张医生手术技术精湛，术后恢复很好，非常感谢！', CURDATE() - INTERVAL 4 DAY),
('rev-005', 'doc-002', 'pat-004', 5, '医生很细心，详细讲解了注意事项。', CURDATE() - INTERVAL 8 DAY),
('rev-006', 'doc-002', 'pat-001', 4, '整体不错，就是有些匆忙。', CURDATE() - INTERVAL 12 DAY),

-- 王医生 (doc-003) 的评价 - 平均分 4.5
('rev-007', 'doc-003', 'pat-003', 4, '医生很专业，开的药很有效。', CURDATE() - INTERVAL 6 DAY),
('rev-008', 'doc-003', 'pat-005', 5, '孩子的皮肤问题解决了，非常感谢王医生！', CURDATE() - INTERVAL 9 DAY),

-- 陈医生 (doc-005) 的评价 - 平均分 4.67
('rev-009', 'doc-005', 'pat-004', 5, '眼科检查很仔细，医生很负责。', CURDATE() - INTERVAL 3 DAY),
('rev-010', 'doc-005', 'pat-001', 5, '手术很成功，术后视力恢复得很好。', CURDATE() - INTERVAL 7 DAY),
('rev-011', 'doc-005', 'pat-002', 4, '医生技术好，就是预约比较难。', CURDATE() - INTERVAL 11 DAY);
```

### 步骤3: 验证数据

```sql
-- 查看各医生的评分统计
SELECT 
    d.id,
    d.name,
    dept.name as department,
    ROUND(AVG(dr.rating), 1) as avg_rating,
    COUNT(dr.id) as review_count
FROM doctors d
LEFT JOIN doctor_reviews dr ON d.id = dr.doctor_id
LEFT JOIN departments dept ON d.department_id = dept.id
GROUP BY d.id, d.name, dept.name
ORDER BY avg_rating DESC;
```

---

## 👥 患者如何评价医生？

### 完整流程

#### 1. 患者端操作
```
登录 → 我的预约 → 找到"已完成"的预约 → 点击"评价"按钮
```

#### 2. 评价窗口
- **选择星级**: 1-5星（1星=非常差，5星=非常满意）
- **填写评价内容**: 分享就诊体验
- **提交评价**: 保存到数据库

#### 3. 限制条件
- 只有**已完成**状态的预约才能评价
- 每个预约只能评价一次
- 必须选择星级（1-5）
- 必须填写评价内容

### 前端代码位置
```
文件：frontend/src/views/patient/MyAppointments.vue
功能：包含完整的评价弹窗和提交逻辑
```

---

## 🩺 医生端如何查看评价？

### 查看评价的三个地方

#### 1. 工作台首页
```
路径：/doctor/home
显示：最近3条评价
```

#### 2. 我的评价页面
```
路径：/doctor/reviews
显示：所有评价列表 + 评分统计
```

#### 3. 个人资料页面
```
路径：/doctor/profile
显示：平均评分 + 总评价数
```

---

## 🔍 评分计算逻辑

### 后端实现

#### Repository层 (查询)
```java
// 文件：DoctorReviewRepository.java
@Query("SELECT AVG(r.rating) FROM DoctorReview r WHERE r.doctorId = :doctorId")
Double getAverageRatingByDoctorId(@Param("doctorId") String doctorId);

@Query("SELECT COUNT(r) FROM DoctorReview r WHERE r.doctorId = :doctorId")
Integer getReviewCountByDoctorId(@Param("doctorId") String doctorId);
```

#### Service层 (计算)
```java
// 文件：DoctorReviewService.java
public Map<String, Object> getDoctorReviewStatsByUserId(String userId) {
    // 1. 获取医生ID
    Doctor doctor = doctorRepository.findByUserId(userId)...
    
    // 2. 计算平均分
    Double avgRating = doctorReviewRepository.getAverageRatingByDoctorId(doctor.getId());
    
    // 3. 计算总数
    Integer totalCount = doctorReviewRepository.getReviewCountByDoctorId(doctor.getId());
    
    // 4. 返回统计数据
    return Map.of(
        "averageRating", avgRating != null ? avgRating : 5.0,
        "totalCount", totalCount != null ? totalCount : 0
    );
}
```

---

## 📋 后端API接口

### 患者端API

#### 1. 提交评价
```
POST /api/reviews
Body: {
  "doctorId": "医生ID",
  "appointmentId": "预约ID",
  "rating": 5,
  "content": "评价内容"
}
```

#### 2. 查看医生评价
```
GET /api/doctors/{doctorId}/reviews?page=0&size=10
```

### 医生端API

#### 1. 获取我的评价
```
GET /api/doctor/reviews/my?page=0&size=10
```

#### 2. 获取评价统计
```
GET /api/doctor/reviews/stats
Response: {
  "averageRating": 4.5,
  "totalCount": 10,
  "distribution": {
    "1": 0,
    "2": 1,
    "3": 2,
    "4": 3,
    "5": 4
  }
}
```

---

## ✅ 测试步骤

### 1. 创建测试数据
```sql
-- 执行上面的 CREATE TABLE 和 INSERT 语句
```

### 2. 重启后端服务
```bash
# 确保后端加载新的表结构
```

### 3. 测试患者端评价
1. 以患者身份登录（例如：手机号 `13800000004`，密码 `password123`）
2. 进入"我的预约"
3. 找到"已完成"的预约
4. 点击"评价"按钮
5. 选择星级并填写内容
6. 提交评价

### 4. 验证医生端
1. 以医生身份登录（例如：手机号 `13800000002`，密码 `password123`）
2. 进入"工作台" - 查看最近评价
3. 进入"我的评价" - 查看所有评价
4. 进入"个人资料" - 查看平均评分

---

## 🐛 常见问题

### Q1: 医生基本信息显示"N/A"或空白？
**A:** 
1. 确认数据库中 `doctors` 表有该医生数据
2. 确认 `doctors.user_id` 与登录的 `users.id` 匹配
3. 检查后端日志是否有错误

### Q2: 评分显示5.0但没有评价？
**A:** 
- 默认评分是5.0（没有评价时的初始值）
- 患者提交评价后会动态计算真实平均分

### Q3: 患者端找不到"评价"按钮？
**A:** 
- 只有**已完成**状态的预约才有评价按钮
- 需要医生先标记预约为"已完成"

### Q4: 评价提交失败？
**A:** 
1. 检查 `doctor_reviews` 表是否存在
2. 检查外键约束是否正确
3. 查看后端日志错误信息

---

## 📝 代码修改总结

### 已修改的文件

#### 后端
1. ✅ `DoctorDetailDTO.java` - 添加 `employeeId`, `licenseNumber`, `rating` 字段
2. ✅ `DoctorProfileService.java` - 在 `convertToDetailDTO` 中设置所有字段

#### 前端
- 无需修改，代码已经完整

---

## 🎯 预期效果

### 医生个人资料页面显示
```
姓名: 李医生
工号: D0001
职称: 主任医师
科室: 心血管内科
评分: 4.7 ⭐⭐⭐⭐⭐ (14条评价)
```

### 患者评价后
- 医生的平均评分会实时更新
- 评价数量会增加
- 评价会显示在医生的评价列表中

---

**创建日期:** 2025-12-29
**状态:** ✅ 完成
**需要执行:** 创建 `doctor_reviews` 表 + 插入测试数据

