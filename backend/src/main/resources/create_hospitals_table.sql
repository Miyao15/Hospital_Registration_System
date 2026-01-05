-- 创建 hospitals 表（如果不存在）
-- 此脚本用于初始化 hospitals 表，包含所有必需的字段

USE hospital_registration;

CREATE TABLE IF NOT EXISTS hospitals (
    id VARCHAR(36) PRIMARY KEY COMMENT '医院ID (UUID)',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '医院名称',
    address VARCHAR(255) COMMENT '医院地址',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    postal_code VARCHAR(10) COMMENT '邮编',
    region VARCHAR(50) COMMENT '地区 (如：华东、华北)',
    phone VARCHAR(20) COMMENT '联系电话',
    longitude DOUBLE(10, 7) COMMENT '经度',
    latitude DOUBLE(10, 7) COMMENT '纬度',
    description TEXT COMMENT '医院简介',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hospitals_city (city),
    INDEX idx_hospitals_region (region),
    INDEX idx_hospitals_province (province),
    INDEX idx_hospitals_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医院表';



