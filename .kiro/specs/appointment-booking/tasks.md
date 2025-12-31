# 实现计划

- [ ] 1. 创建数据库表和实体
  - [x] 1.1 创建排班和时间段表


    - 创建 schedules 表
    - 创建 time_slots 表
    - 创建对应实体类和 Repository
    - _需求: 1.1, 2.1_

  - [ ] 1.2 创建预约表和实体
    - 创建 appointments 表
    - 创建 Appointment 实体类
    - 创建 AppointmentRepository
    - _需求: 3.1, 3.2_



- [x] 2. 实现排班服务


  - [ ] 2.1 创建排班 DTO 和服务
    - 创建 AvailableDateDTO、TimeSlotDTO
    - 实现 ScheduleService
    - _需求: 1.1, 1.2, 2.1, 2.2_


  - [ ] 2.2 创建排班控制器
    - 实现 GET /api/schedules/doctor/{doctorId}/dates



    - 实现 GET /api/schedules/doctor/{doctorId}/slots
    - _需求: 1.1, 2.1_

- [ ] 3. 实现预约服务
  - [ ] 3.1 创建预约 DTO 和服务
    - 创建 CreateAppointmentDTO、AppointmentDetailDTO
    - 实现 AppointmentService（含乐观锁）
    - _需求: 3.1, 3.2, 3.3, 5.1, 5.2_
  - [ ] 3.2 创建预约控制器
    - 实现 POST /api/appointments
    - 实现 GET /api/appointments/{id}
    - 实现 GET /api/appointments/my
    - _需求: 3.1, 6.1, 6.2_
