# 实现计划

- [ ] 1. 创建数据库表和实体
  - [x] 1.1 创建科室表和实体


    - 创建 departments 表
    - 创建 Department 实体类
    - 创建 DepartmentRepository

    - _需求: 1.1, 2.1_
  - [ ] 1.2 扩展医生实体
    - 在 doctors 表添加 avatar、introduction、education、expertise、schedule_info 字段

    - 更新 Doctor 实体类
    - _需求: 4.1, 5.1_
  - [ ] 1.3 创建医生评价表和实体
    - 创建 doctor_reviews 表
    - 创建 DoctorReview 实体类
    - 创建 DoctorReviewRepository


    - _需求: 5.3_



- [ ] 2. 实现科室服务
  - [ ] 2.1 创建科室 DTO 和服务
    - 创建 DepartmentDTO、DepartmentListDTO
    - 实现 DepartmentService
    - _需求: 1.1, 1.2, 2.1_


  - [ ] 2.2 创建科室控制器
    - 实现 GET /api/departments


    - 实现 GET /api/departments/{id}
    - 实现 GET /api/departments/{id}/doctors
    - _需求: 1.1, 2.1, 2.2, 3.1_


- [ ] 3. 实现医生信息服务
  - [ ] 3.1 创建医生 DTO 和服务
    - 创建 DoctorListDTO、DoctorDetailDTO、DoctorSearchDTO


    - 实现 DoctorProfileService
    - _需求: 4.1, 5.1, 6.1_

  - [ ] 3.2 创建医生控制器
    - 实现 GET /api/doctors
    - 实现 GET /api/doctors/{id}
    - 实现 GET /api/doctors/search
    - _需求: 4.1, 5.1, 6.1, 6.2, 6.3, 6.4_
  - [ ] 3.3 实现医生在线状态
    - 实现 getDoctorOnlineStatus 方法
    - _需求: 4.3_

- [ ] 4. 实现医生评价服务
  - [ ] 4.1 创建评价 DTO 和服务
    - 创建 DoctorReviewDTO
    - 实现 DoctorReviewService
    - _需求: 5.3_
  - [ ] 4.2 创建评价控制器
    - 实现 GET /api/doctors/{id}/reviews
    - _需求: 5.3_
