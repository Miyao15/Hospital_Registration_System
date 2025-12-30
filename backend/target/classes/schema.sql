-- 医院门诊预约挂号系统 - 用户认证模块数据库表结构
-- 注意：Spring Boot JPA 会自动创建表，此文件仅作为参考和手动初始化使用

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY COMMENT '用户ID (UUID)',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    role ENUM('PATIENT', 'DOCTOR', 'ADMIN') NOT NULL COMMENT '用户角色',
    status ENUM('ACTIVE', 'PENDING', 'LOCKED', 'DISABLED') NOT NULL DEFAULT 'ACTIVE' COMMENT '账户状态',
    login_failures INT DEFAULT 0 COMMENT '连续登录失败次数',
    locked_until DATETIME NULL COMMENT '锁定截止时间',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_users_phone (phone),
    INDEX idx_users_role (role),
    INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 患者表
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(36) PRIMARY KEY COMMENT '患者ID (UUID)',
    user_id VARCHAR(36) NOT NULL UNIQUE COMMENT '关联用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    gender ENUM('MALE', 'FEMALE') NOT NULL COMMENT '性别',
    birth_date DATE NOT NULL COMMENT '出生日期',
    medical_history TEXT COMMENT '病史',
    allergy_history TEXT COMMENT '过敏史',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_patients_id_card (id_card),
    INDEX idx_patients_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者表';

-- 医师表
CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(36) PRIMARY KEY COMMENT '医师ID (UUID)',
    user_id VARCHAR(36) NOT NULL UNIQUE COMMENT '关联用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    employee_id VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    title ENUM('RESIDENT', 'ATTENDING', 'ASSOCIATE_CHIEF', 'CHIEF') NOT NULL COMMENT '职称',
    department_id VARCHAR(36) NOT NULL COMMENT '科室ID',
    specialty TEXT COMMENT '专长',
    license_number VARCHAR(50) NOT NULL UNIQUE COMMENT '医师资格证号',
    introduction TEXT COMMENT '简介',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    approved_at DATETIME NULL COMMENT '审批时间',
    approved_by VARCHAR(36) NULL COMMENT '审批人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_doctors_employee_id (employee_id),
    INDEX idx_doctors_license_number (license_number),
    INDEX idx_doctors_department_id (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医师表';

-- 管理员表
CREATE TABLE IF NOT EXISTS admins (
    id VARCHAR(36) PRIMARY KEY COMMENT '管理员ID (UUID)',
    user_id VARCHAR(36) NOT NULL UNIQUE COMMENT '关联用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    employee_id VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_admins_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 科室表
CREATE TABLE IF NOT EXISTS departments (
    id VARCHAR(36) PRIMARY KEY COMMENT '科室ID (UUID)',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '科室名称',
    category VARCHAR(20) NOT NULL COMMENT '科室类别(内科系/外科系/其他)',
    description TEXT COMMENT '科室简介',
    location VARCHAR(100) COMMENT '科室位置',
    phone VARCHAR(20) COMMENT '联系电话',
    director_id VARCHAR(36) NULL COMMENT '科室主任ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_departments_category (category),
    INDEX idx_departments_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科室表';

-- 医生评价表
CREATE TABLE IF NOT EXISTS doctor_reviews (
    id VARCHAR(36) PRIMARY KEY COMMENT '评价ID (UUID)',
    doctor_id VARCHAR(36) NOT NULL COMMENT '医生ID',
    patient_id VARCHAR(36) NOT NULL COMMENT '患者ID',
    rating INT NOT NULL COMMENT '评分(1-5)',
    content TEXT COMMENT '评价内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_doctor_reviews_doctor_id (doctor_id),
    INDEX idx_doctor_reviews_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生评价表';


-- 医生排班表
CREATE TABLE IF NOT EXISTS schedules (
    id VARCHAR(36) PRIMARY KEY COMMENT '排班ID (UUID)',
    doctor_id VARCHAR(36) NOT NULL COMMENT '医生ID',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    is_working BOOLEAN DEFAULT TRUE COMMENT '是否工作',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    UNIQUE KEY uk_doctor_date (doctor_id, schedule_date),
    INDEX idx_schedules_date (schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生排班表';

-- 时间段号源表
CREATE TABLE IF NOT EXISTS time_slots (
    id VARCHAR(36) PRIMARY KEY COMMENT '时间段ID (UUID)',
    schedule_id VARCHAR(36) NOT NULL COMMENT '排班ID',
    period ENUM('MORNING', 'AFTERNOON', 'EVENING') NOT NULL COMMENT '时间段',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    total_slots INT NOT NULL DEFAULT 20 COMMENT '总号源',
    remaining_slots INT NOT NULL DEFAULT 20 COMMENT '剩余号源',
    version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE,
    UNIQUE KEY uk_schedule_period (schedule_id, period),
    INDEX idx_time_slots_remaining (remaining_slots)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='时间段号源表';

-- 预约挂号表
CREATE TABLE IF NOT EXISTS appointments (
    id VARCHAR(36) PRIMARY KEY COMMENT '预约ID (UUID)',
    appointment_no VARCHAR(20) NOT NULL UNIQUE COMMENT '预约单号',
    patient_id VARCHAR(36) NOT NULL COMMENT '患者ID',
    doctor_id VARCHAR(36) NOT NULL COMMENT '医生ID',
    department_id VARCHAR(36) NOT NULL COMMENT '科室ID',
    time_slot_id VARCHAR(36) NOT NULL COMMENT '时间段ID',
    medical_item_id VARCHAR(36) COMMENT '检查项目ID',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    period ENUM('MORNING', 'AFTERNOON', 'EVENING') NOT NULL COMMENT '时间段',
    patient_name VARCHAR(50) NOT NULL COMMENT '就诊人姓名',
    patient_phone VARCHAR(20) NOT NULL COMMENT '就诊人电话',
    symptom_desc TEXT COMMENT '病情描述',
    status ENUM('PENDING', 'CHECKED_IN', 'COMPLETED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    FOREIGN KEY (time_slot_id) REFERENCES time_slots(id) ON DELETE CASCADE,
    FOREIGN KEY (medical_item_id) REFERENCES examination_items(id) ON DELETE SET NULL,
    INDEX idx_appointments_patient (patient_id),
    INDEX idx_appointments_doctor (doctor_id),
    INDEX idx_appointments_date (appointment_date),
    INDEX idx_appointments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约挂号表';


-- 消息通知表
CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(36) PRIMARY KEY COMMENT '通知ID (UUID)',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    type ENUM('APPOINTMENT', 'REMINDER', 'SYSTEM', 'CANCEL') NOT NULL COMMENT '通知类型',
    related_id VARCHAR(36) COMMENT '关联ID（如预约ID）',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (is_read),
    INDEX idx_notifications_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';


-- 检查项目表（健康检查推荐项目）
CREATE TABLE IF NOT EXISTS examination_items (
    id VARCHAR(36) PRIMARY KEY COMMENT '检查项目ID (UUID)',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '项目名称',
    description TEXT COMMENT '项目描述',
    price DECIMAL(10, 2) NOT NULL COMMENT '项目价格',
    category VARCHAR(50) NOT NULL COMMENT '项目类别 (如: 常规检查, 专科检查)',
    department_id VARCHAR(36) NULL COMMENT '关联科室ID (可选，保留字段，实际使用关联表)',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    INDEX idx_examination_items_category (category),
    INDEX idx_examination_items_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检查项目表（健康检查推荐项目）';

-- 检查项目与科室关联表（多对多关系）
CREATE TABLE IF NOT EXISTS examination_item_departments (
    id VARCHAR(36) PRIMARY KEY COMMENT '关联ID (UUID)',
    examination_item_id VARCHAR(36) NOT NULL COMMENT '检查项目ID',
    department_id VARCHAR(36) NOT NULL COMMENT '科室ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (examination_item_id) REFERENCES examination_items(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    UNIQUE KEY uk_item_dept (examination_item_id, department_id),
    INDEX idx_examination_item_id (examination_item_id),
    INDEX idx_department_id (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检查项目与科室关联表';
