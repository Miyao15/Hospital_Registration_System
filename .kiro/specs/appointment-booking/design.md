# 设计文档

## 概述

患者预约挂号模块实现核心预约流程，包括查看可预约日期、选择时间段、提交预约、号源管理等功能。采用乐观锁机制防止并发超额预约。

## 架构

```
┌─────────────────────────────────────────────────────────────┐
│                    预约流程                                  │
│  选择医生 -> 选择日期 -> 选择时间段 -> 填写信息 -> 提交预约   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    后端服务                                  │
│  ┌─────────────────┐  ┌─────────────────────────────────┐   │
│  │ AppointmentCtrl │  │ ScheduleController              │   │
│  └─────────────────┘  └─────────────────────────────────┘   │
│  ┌─────────────────┐  ┌─────────────────────────────────┐   │
│  │ AppointmentSvc  │  │ ScheduleService                 │   │
│  └─────────────────┘  └─────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    数据库                                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ schedules   │  │ time_slots  │  │ appointments        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 组件和接口

### REST API 接口

#### 排班接口
- `GET /api/schedules/doctor/{doctorId}/dates` - 获取医生可预约日期
- `GET /api/schedules/doctor/{doctorId}/slots` - 获取某日期的时间段和号源

#### 预约接口
- `POST /api/appointments` - 创建预约
- `GET /api/appointments/{id}` - 获取预约详情
- `GET /api/appointments/my` - 获取我的预约列表

## 数据模型

### Schedule 实体（医生排班）
```java
public class Schedule {
    String id;
    String doctorId;
    LocalDate date;
    Boolean isWorking;      // 是否工作日
    String remark;          // 备注（如：临时停诊）
}
```

### TimeSlot 实体（时间段号源）
```java
public class TimeSlot {
    String id;
    String scheduleId;
    String period;          // MORNING/AFTERNOON/EVENING
    LocalTime startTime;
    LocalTime endTime;
    Integer totalSlots;     // 总号源
    Integer remainingSlots; // 剩余号源
    Integer version;        // 乐观锁版本号
}
```

### Appointment 实体（预约记录）
```java
public class Appointment {
    String id;
    String appointmentNo;   // 预约单号
    String patientId;
    String doctorId;
    String departmentId;
    String timeSlotId;
    LocalDate appointmentDate;
    String period;
    String patientName;     // 就诊人姓名
    String patientPhone;    // 就诊人电话
    String symptomDesc;     // 病情描述
    String status;          // PENDING/COMPLETED/CANCELLED/EXPIRED
    LocalDateTime createdAt;
}
```

### DTO 类
```java
public class AvailableDateDTO {
    LocalDate date;
    String dayOfWeek;
    Boolean available;
    Integer totalSlots;
    Integer remainingSlots;
}

public class TimeSlotDTO {
    String id;
    String period;
    String periodName;
    String timeRange;
    Integer totalSlots;
    Integer remainingSlots;
    Boolean available;
}

public class CreateAppointmentDTO {
    String doctorId;
    String timeSlotId;
    String patientName;
    String patientPhone;
    String symptomDesc;
}

public class AppointmentDetailDTO {
    String id;
    String appointmentNo;
    String doctorName;
    String doctorTitle;
    String departmentName;
    String departmentLocation;
    LocalDate appointmentDate;
    String period;
    String timeRange;
    String status;
    String symptomDesc;
    LocalDateTime createdAt;
}
```

## 错误处理

- 号源不足: 返回 400 Bad Request，提示"该时间段号源已满"
- 重复预约: 返回 400 Bad Request，提示"您已预约该医生该时段"
- 医生不存在: 返回 404 Not Found
- 并发冲突: 返回 409 Conflict，提示"预约失败，请重试"

## 并发控制

使用乐观锁（version字段）防止号源超额预约：
1. 查询时间段获取当前version
2. 更新时检查version是否匹配
3. 不匹配则抛出并发异常，提示用户重试
