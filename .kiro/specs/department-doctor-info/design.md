# 设计文档

## 概述

科室与医生信息展示模块提供医院科室和医生信息的查询、展示功能。采用 Spring Boot 后端 + Vue.js 前端架构，支持科室列表、科室详情、医生列表、医生详情、多条件搜索等功能。

## 架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端 (Vue.js)                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ 科室列表页  │  │ 医生列表页  │  │ 医生详情页          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    后端 (Spring Boot)                       │
│  ┌─────────────────┐  ┌─────────────────────────────────┐   │
│  │ DepartmentCtrl  │  │ DoctorProfileController         │   │
│  └─────────────────┘  └─────────────────────────────────┘   │
│  ┌─────────────────┐  ┌─────────────────────────────────┐   │
│  │ DepartmentSvc   │  │ DoctorProfileService            │   │
│  └─────────────────┘  └─────────────────────────────────┘   │
│  ┌─────────────────┐  ┌─────────────────────────────────┐   │
│  │ DepartmentRepo  │  │ DoctorProfileRepository         │   │
│  └─────────────────┘  └─────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    数据库 (MySQL)                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ departments │  │ doctors     │  │ doctor_reviews      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 组件和接口

### REST API 接口

#### 科室接口
- `GET /api/departments` - 获取科室列表
- `GET /api/departments/{id}` - 获取科室详情
- `GET /api/departments/{id}/doctors` - 获取科室下的医生列表

#### 医生接口
- `GET /api/doctors` - 获取医生列表（支持分页和筛选）
- `GET /api/doctors/{id}` - 获取医生详情
- `GET /api/doctors/{id}/reviews` - 获取医生评价列表
- `GET /api/doctors/search` - 搜索医生

### 服务层

#### DepartmentService
- `getAllDepartments()` - 获取所有科室
- `getDepartmentById(Long id)` - 根据ID获取科室
- `getDepartmentsByCategory(String category)` - 按类别获取科室

#### DoctorProfileService
- `getDoctorsByDepartment(Long departmentId)` - 获取科室下的医生
- `getDoctorById(Long id)` - 获取医生详情
- `searchDoctors(DoctorSearchDTO criteria)` - 搜索医生
- `getDoctorOnlineStatus(Long doctorId)` - 获取医生在线状态

## 数据模型

### Department 实体
```java
public class Department {
    Long id;
    String name;           // 科室名称
    String category;       // 科室类别（内科系/外科系/其他）
    String description;    // 科室简介
    String location;       // 科室位置
    String phone;          // 联系电话
    Long directorId;       // 科室主任ID
    Integer sortOrder;     // 排序顺序
    Boolean enabled;       // 是否启用
}
```

### DoctorProfile 扩展字段
```java
// 在现有 Doctor 实体基础上扩展
public class Doctor {
    // ... 现有字段
    String avatar;         // 头像URL
    String introduction;   // 个人简介
    String education;      // 教育背景
    String expertise;      // 擅长领域
    String scheduleInfo;   // 出诊时间概要
}
```

### DoctorReview 实体
```java
public class DoctorReview {
    Long id;
    Long doctorId;         // 医生ID
    Long patientId;        // 患者ID
    Integer rating;        // 评分(1-5)
    String content;        // 评价内容
    LocalDateTime createdAt;
}
```

### DTO 类
```java
public class DepartmentDTO {
    Long id;
    String name;
    String category;
    String description;
    String location;
    String phone;
    DoctorSimpleDTO director;
    Integer doctorCount;
}

public class DoctorListDTO {
    Long id;
    String name;
    String title;
    String departmentName;
    String avatar;
    String expertise;
    String scheduleInfo;
    String onlineStatus;  // AVAILABLE/FULL/REST
}

public class DoctorDetailDTO {
    Long id;
    String name;
    String title;
    String departmentName;
    String avatar;
    String introduction;
    String education;
    String expertise;
    String scheduleInfo;
    Double avgRating;
    Integer reviewCount;
}

public class DoctorSearchDTO {
    String keyword;        // 姓名关键字
    Long departmentId;     // 科室ID
    String title;          // 职称
    Integer page;
    Integer size;
}
```

## 正确性属性

*属性是系统在所有有效执行中应保持为真的特征或行为——本质上是关于系统应该做什么的形式化陈述。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*

由于用户要求先开发不测试，此处省略正确性属性部分。

## 错误处理

- 科室不存在: 返回 404 Not Found
- 医生不存在: 返回 404 Not Found
- 参数无效: 返回 400 Bad Request
- 服务器错误: 返回 500 Internal Server Error

## 测试策略

用户要求先开发不测试，测试策略暂缓实施。
