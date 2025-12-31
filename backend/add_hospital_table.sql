-- 创建医院表
CREATE TABLE IF NOT EXISTS `hospitals` (
  `id` varchar(36) NOT NULL COMMENT '医院ID (UUID)',
  `name` varchar(100) NOT NULL COMMENT '医院名称',
  `address` varchar(255) DEFAULT NULL COMMENT '医院地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `longitude` double DEFAULT NULL COMMENT '经度',
  `latitude` double DEFAULT NULL COMMENT '纬度',
  `description` text COMMENT '医院简介',
  `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医院表';

-- 插入天津医科大学总医院数据
INSERT INTO `hospitals` (`id`, `name`, `address`, `phone`, `longitude`, `latitude`, `description`, `enabled`) VALUES
('hosp-001', '天津医科大学总医院', '天津市和平区鞍山道154号', '022-60362255', 117.195907, 39.104027, '天津医科大学总医院始建于1946年，是天津市最大的集医疗、教学、科研、预防为一体的综合性大学医院。', 1);

-- 给医生表添加医院ID字段
ALTER TABLE `doctors` ADD COLUMN `hospital_id` varchar(36) DEFAULT NULL COMMENT '医院ID' AFTER `schedule_info`;

-- 将所有现有医生关联到天津医科大学总医院
UPDATE `doctors` SET `hospital_id` = 'hosp-001' WHERE `hospital_id` IS NULL;

-- 添加外键约束（可选）
-- ALTER TABLE `doctors` ADD CONSTRAINT `fk_doctors_hospital` FOREIGN KEY (`hospital_id`) REFERENCES `hospitals` (`id`) ON DELETE SET NULL;
