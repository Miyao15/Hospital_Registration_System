/*
 Navicat Premium Dump SQL

 Source Server         : hospital_registration
 Source Server Type    : MySQL
 Source Server Version : 80023 (8.0.23)
 Source Host           : localhost:3306
 Source Schema         : hospital_registration

 Target Server Type    : MySQL
 Target Server Version : 80023 (8.0.23)
 File Encoding         : 65001

 Date: 30/12/2025 20:19:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admins
-- ----------------------------
DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绠＄悊鍛業D (UUID)',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍏宠仈鐢ㄦ埛ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '濮撳悕',
  `employee_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '宸ュ彿',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `employee_id`(`employee_id` ASC) USING BTREE,
  INDEX `idx_admins_employee_id`(`employee_id` ASC) USING BTREE,
  CONSTRAINT `admins_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '绠＄悊鍛樿〃' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admins
-- ----------------------------
INSERT INTO `admins` VALUES ('241b355b-d76c-4bbb-81aa-d929367cb55f', '4bddc762-b2b2-4795-b04b-e06eba14730e', 'adm003', '777777', '2025-12-30 10:29:38', '2025-12-30 10:29:38');
INSERT INTO `admins` VALUES ('8b0baf44-e7c8-4d15-9ecf-fa9ec309e8e9', '30ea84bb-6023-4d1a-ba71-2fb3f1711c86', '张包', '111111', '2025-12-30 08:49:38', '2025-12-30 08:49:38');
INSERT INTO `admins` VALUES ('adm-001', 'usr-adm-001', '王管理', 'A0001', '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `admins` VALUES ('d458510e-d542-43c7-8202-3793e9ea3a3f', 'ec934f66-6b05-4ae8-b44c-a75148744558', '张包', '000000', '2025-12-30 10:28:47', '2025-12-30 10:28:47');

-- ----------------------------
-- Table structure for appointments
-- ----------------------------
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '棰勭害ID (UUID)',
  `appointment_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '棰勭害鍗曞彿',
  `patient_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎮ｈ�匢D',
  `doctor_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍖荤敓ID',
  `department_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绉戝ID',
  `time_slot_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏃堕棿娈礗D',
  `appointment_date` date NOT NULL COMMENT '棰勭害鏃ユ湡',
  `period` enum('MORNING','AFTERNOON','EVENING') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏃堕棿娈�',
  `patient_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '灏辫瘖浜哄鍚�',
  `patient_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '灏辫瘖浜虹數璇�',
  `symptom_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '鐥呮儏鎻忚堪',
  `status` enum('PENDING','CHECKED_IN','COMPLETED','CANCELLED','EXPIRED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '鐘舵��',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '鍙栨秷鍘熷洜',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `medical_item_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检查项目ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `appointment_no`(`appointment_no` ASC) USING BTREE,
  INDEX `department_id`(`department_id` ASC) USING BTREE,
  INDEX `time_slot_id`(`time_slot_id` ASC) USING BTREE,
  INDEX `idx_appointments_patient`(`patient_id` ASC) USING BTREE,
  INDEX `idx_appointments_doctor`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_appointments_date`(`appointment_date` ASC) USING BTREE,
  INDEX `idx_appointments_status`(`status` ASC) USING BTREE,
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `appointments_ibfk_3` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `appointments_ibfk_4` FOREIGN KEY (`time_slot_id`) REFERENCES `time_slots` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '棰勭害鎸傚彿琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointments
-- ----------------------------
INSERT INTO `appointments` VALUES ('0b27d050-2641-424b-baca-8c8fa53db5b8', 'GH20251229895429', 'a3c6dd79-4f0e-4ca0-927e-50dc62bc6111', 'doc-005', 'dpt-005', 'ts-007', '2025-12-31', 'MORNING', '李想', '13344445555', '', 'CANCELLED', '用户主动取消', '2025-12-29 18:56:11', '2025-12-29 19:01:16', NULL);
INSERT INTO `appointments` VALUES ('37fe4ec2-78b0-48ad-8086-9a635fea39f7', 'GH20251229543016', 'a3c6dd79-4f0e-4ca0-927e-50dc62bc6111', 'doc-003', 'dpt-003', 'ts-006', '2025-12-30', 'AFTERNOON', '李想', '13344445555', '', 'PENDING', NULL, '2025-12-29 18:11:31', '2025-12-29 18:11:31', NULL);
INSERT INTO `appointments` VALUES ('6988c9c1-83c3-46b4-9fd5-58c3c9af33d2', 'GH20251230958508', 'a3c6dd79-4f0e-4ca0-927e-50dc62bc6111', '67d5fa84-268b-4125-8000-306d35c34245', 'dpt-004', 'ts-007', '2025-12-31', 'MORNING', '李想', '13344445555', '', 'PENDING', NULL, '2025-12-30 13:47:42', '2025-12-30 13:47:42', NULL);
INSERT INTO `appointments` VALUES ('88ee3547-03b3-498a-8594-ebb527445991', 'GH20251229286975', 'a3c6dd79-4f0e-4ca0-927e-50dc62bc6111', 'doc-001', 'dpt-001', 'ts-001', '2025-12-30', 'MORNING', '李想', '13344445555', '', 'PENDING', NULL, '2025-12-29 18:31:19', '2025-12-29 18:31:19', NULL);
INSERT INTO `appointments` VALUES ('afb04869-7f2e-4458-9d19-58f9e9367d03', 'GH20251230587211', 'a3c6dd79-4f0e-4ca0-927e-50dc62bc6111', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', 'dpt-004', '19505606-894b-4532-816a-09ae268b7219', '2026-01-07', 'MORNING', '李想', '13344445555', '', 'CHECKED_IN', NULL, '2025-12-30 10:21:18', '2025-12-30 10:38:59', NULL);
INSERT INTO `appointments` VALUES ('apt-001', 'APT20251229001', 'pat-001', 'doc-001', 'dpt-001', 'ts-001', '2025-12-30', 'MORNING', '赵小明', '13800000004', '最近一周感到胸闷，偶尔心悸。', 'PENDING', NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `appointments` VALUES ('apt-002', 'APT20251224002', 'pat-002', 'doc-002', 'dpt-002', 'ts-005', '2025-12-24', 'AFTERNOON', '孙小美', '13800000005', '手腕扭伤复查。', 'COMPLETED', NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `appointments` VALUES ('apt-003', 'APT20251229003', 'pat-004', 'doc-001', 'dpt-001', 'ts-002', '2025-12-30', 'AFTERNOON', '吴女士', '13700000002', '临时有事，取消预约。', 'CANCELLED', NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `appointments` VALUES ('apt-004', 'APT20251229004', 'pat-003', 'doc-003', 'dpt-003', 'ts-006', '2025-12-30', 'AFTERNOON', '周先生', '13700000001', '脸上出现红疹，很痒。', 'PENDING', NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);

-- ----------------------------
-- Table structure for departments
-- ----------------------------
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绉戝ID (UUID)',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绉戝鍚嶇О',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绉戝绫诲埆(鍐呯绯�/澶栫绯�/鍏朵粬)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '绉戝绠�浠�',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '绉戝浣嶇疆',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `director_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '绉戝涓讳换ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '鎺掑簭椤哄簭',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '鏄惁鍚敤',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE,
  INDEX `idx_departments_category`(`category` ASC) USING BTREE,
  INDEX `idx_departments_enabled`(`enabled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '绉戝琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of departments
-- ----------------------------
INSERT INTO `departments` VALUES ('dpt-001', '心血管内科', '内科系', '专注于心血管系统疾病的诊断和治疗，如高血压、冠心病等。', '门诊大楼三层A区', '010-12345671', NULL, 1, 1, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `departments` VALUES ('dpt-002', '骨科', '外科系', '处理骨骼、关节、韧带、肌腱和肌肉的损伤和疾病。', '门诊大楼二层B区', '010-12345672', NULL, 2, 1, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `departments` VALUES ('dpt-003', '皮肤科', '其他', '诊治各种皮肤、指甲及毛发相关的疾病。', '门诊大楼一层C区', '010-12345673', NULL, 3, 1, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `departments` VALUES ('dpt-004', '儿科', '其他', '为婴幼儿、儿童和青少年提供全面的医疗保健服务。', '儿童医疗中心二层', '010-12345674', NULL, 4, 1, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `departments` VALUES ('dpt-005', '眼科', '外科系', '负责眼部疾病的诊断、治疗和手术。', '门诊大楼四层A区', '010-12345675', NULL, 5, 1, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `departments` VALUES ('dpt-006', '口腔科', '其他', '诊治口腔、牙齿、牙龈及相关疾病的专科。', '门诊大楼一层D区', '010-12345676', NULL, 6, 1, '2025-12-30 11:29:10', '2025-12-30 11:36:14');
INSERT INTO `departments` VALUES ('dpt-007', '妇科', '其他', '为女性提供妇科疾病诊断、治疗和预防保健服务。', '门诊大楼二层C区', '010-12345677', NULL, 7, 1, '2025-12-30 11:29:10', '2025-12-30 11:36:14');

-- ----------------------------
-- Table structure for doctor_leaves
-- ----------------------------
DROP TABLE IF EXISTS `doctor_leaves`;
CREATE TABLE `doctor_leaves`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请假记录ID (UUID)',
  `doctor_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '医生ID',
  `start_date` date NOT NULL COMMENT '请假开始日期',
  `end_date` date NOT NULL COMMENT '请假结束日期',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请假原因',
  `status` enum('PENDING','APPROVED','REJECTED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：待审批/已批准/已拒绝/已撤销',
  `approved_by` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批人ID',
  `approved_at` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_doctor_leaves_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_doctor_leaves_status`(`status` ASC) USING BTREE,
  INDEX `idx_doctor_leaves_dates`(`start_date` ASC, `end_date` ASC) USING BTREE,
  CONSTRAINT `doctor_leaves_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生请假表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor_leaves
-- ----------------------------
INSERT INTO `doctor_leaves` VALUES ('c73a6b42-9911-4c44-b489-a957afb1fac1', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2025-12-30', '2026-01-01', '家中有事', 'PENDING', NULL, NULL, NULL, '2025-12-29 22:42:10', '2025-12-29 22:42:10');
INSERT INTO `doctor_leaves` VALUES ('leave-001', 'doc-001', '2026-01-08', '2026-01-10', '参加全国心血管学术会议', 'APPROVED', 'adm-001', '2025-12-27 22:41:06', NULL, '2025-12-26 22:41:06', '2025-12-27 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-002', 'doc-001', '2026-01-18', '2026-01-20', '家中有事需要处理', 'PENDING', NULL, NULL, NULL, '2025-12-28 22:41:06', '2025-12-28 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-003', 'doc-002', '2026-01-03', '2026-01-05', '年假休息', 'APPROVED', 'adm-001', '2025-12-24 22:41:06', NULL, '2025-12-22 22:41:06', '2025-12-24 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-004', 'doc-002', '2025-12-31', '2026-01-01', '临时有事', 'REJECTED', 'adm-001', '2025-12-28 22:41:06', '该时段已安排重要手术，无法批准', '2025-12-27 22:41:06', '2025-12-28 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-005', 'doc-003', '2026-01-13', '2026-01-15', '参加皮肤科专业培训', 'PENDING', NULL, NULL, NULL, '2025-12-29 22:41:06', '2025-12-29 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-006', 'doc-003', '2026-01-06', '2026-01-07', '家人生病陪同就医', 'CANCELLED', NULL, NULL, NULL, '2025-12-26 22:41:06', '2025-12-28 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-007', 'doc-004', '2026-01-04', '2026-01-06', '孩子学校活动需要陪同', 'PENDING', NULL, NULL, NULL, '2025-12-28 22:41:06', '2025-12-28 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-008', 'doc-005', '2025-12-19', '2025-12-21', '身体不适需要休息', 'APPROVED', 'adm-001', '2025-12-17 00:00:00', NULL, '2025-12-15 00:00:00', '2025-12-17 00:00:00');
INSERT INTO `doctor_leaves` VALUES ('leave-009', 'doc-005', '2026-01-23', '2026-01-25', '参加眼科国际研讨会', 'APPROVED', 'adm-001', '2025-12-28 22:41:06', NULL, '2025-12-26 22:41:06', '2025-12-28 22:41:06');
INSERT INTO `doctor_leaves` VALUES ('leave-010', 'doc-001', '2025-11-29', '2025-12-01', '年度体检', 'APPROVED', 'adm-001', '2025-11-27 00:00:00', NULL, '2025-11-24 00:00:00', '2025-11-27 00:00:00');
INSERT INTO `doctor_leaves` VALUES ('leave-011', 'doc-002', '2025-12-09', '2025-12-10', '医院内部培训', 'APPROVED', 'adm-001', '2025-12-04 00:00:00', NULL, '2025-12-02 00:00:00', '2025-12-04 00:00:00');
INSERT INTO `doctor_leaves` VALUES ('leave-012', 'doc-003', '2026-01-01', '2026-01-02', '突发情况需要请假', 'APPROVED', '30ea84bb-6023-4d1a-ba71-2fb3f1711c86', '2025-12-30 09:51:22', NULL, '2025-12-29 22:41:06', '2025-12-30 09:51:22');

-- ----------------------------
-- Table structure for doctor_reviews
-- ----------------------------
DROP TABLE IF EXISTS `doctor_reviews`;
CREATE TABLE `doctor_reviews`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '璇勪环ID (UUID)',
  `doctor_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍖荤敓ID',
  `patient_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎮ｈ�匢D',
  `rating` int NOT NULL COMMENT '璇勫垎(1-5)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '璇勪环鍐呭',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_doctor_reviews_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_doctor_reviews_patient_id`(`patient_id` ASC) USING BTREE,
  CONSTRAINT `doctor_reviews_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `doctor_reviews_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鍖荤敓璇勪环琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor_reviews
-- ----------------------------
INSERT INTO `doctor_reviews` VALUES ('rev-001', '67d5fa84-268b-4125-8000-306d35c34245', 'pat-002', 5, '孟医生非常专业和耐心，手术很成功，恢复得很好！', '2025-12-25 00:00:00');
INSERT INTO `doctor_reviews` VALUES ('rev-002', 'doc-001', 'pat-001', 4, '李医生看诊很仔细，就是等待时间有点长。', '2025-12-19 00:00:00');

-- ----------------------------
-- Table structure for doctors
-- ----------------------------
DROP TABLE IF EXISTS `doctors`;
CREATE TABLE `doctors`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍖诲笀ID (UUID)',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍏宠仈鐢ㄦ埛ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '濮撳悕',
  `employee_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '宸ュ彿',
  `title` enum('RESIDENT','ATTENDING','ASSOCIATE_CHIEF','CHIEF') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鑱岀О',
  `department_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绉戝ID',
  `specialty` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '涓撻暱',
  `license_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍖诲笀璧勬牸璇佸彿',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '绠�浠�',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '澶村儚URL',
  `approved_at` datetime NULL DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `approved_by` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '瀹℃壒浜篒D',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `education` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学历',
  `schedule_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '排班信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `employee_id`(`employee_id` ASC) USING BTREE,
  UNIQUE INDEX `license_number`(`license_number` ASC) USING BTREE,
  INDEX `idx_doctors_employee_id`(`employee_id` ASC) USING BTREE,
  INDEX `idx_doctors_license_number`(`license_number` ASC) USING BTREE,
  INDEX `idx_doctors_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `doctors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鍖诲笀琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctors
-- ----------------------------
INSERT INTO `doctors` VALUES ('67d5fa84-268b-4125-8000-306d35c34245', '138fe1f1-228b-4f0b-a4ea-aef1d6108da7', '孟新', '55555555', 'ATTENDING', 'dpt-004', 'General', '11122223333', 'N/A', NULL, NULL, NULL, '2025-12-29 19:50:50', '2025-12-29 19:50:50', NULL, NULL);
INSERT INTO `doctors` VALUES ('bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '539a90b4-c1a0-4ab4-aadc-35f1ead13c46', '孟听', '333333', 'RESIDENT', 'dpt-004', 'General', '', 'N/A', NULL, NULL, NULL, '2025-12-29 20:04:24', '2025-12-29 22:49:30', '', NULL);
INSERT INTO `doctors` VALUES ('doc-001', 'usr-doc-001', '李医生', 'D0001', 'CHIEF', 'dpt-001', '高血压、冠心病的诊断与治疗', 'LIC20230001', '李医生是心血管内科主任医师，拥有超过20年的临床经验。', NULL, '2025-12-29 14:20:57', 'adm-001', '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL, NULL);
INSERT INTO `doctors` VALUES ('doc-002', 'usr-doc-002', '张医生', 'D0002', 'ATTENDING', 'dpt-002', '骨折、关节置换', 'LIC20230002', '张医生是骨科主治医师，擅长运动损伤和微创手术。', NULL, '2025-12-29 14:20:57', 'adm-001', '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL, NULL);
INSERT INTO `doctors` VALUES ('doc-003', 'usr-doc-003', '王医生', 'D0003', 'RESIDENT', 'dpt-003', '痤疮、湿疹、皮肤过敏', 'LIC20230003', '王医生是皮肤科的住院医师，对常见皮肤病有深入研究。', NULL, '2025-12-29 14:20:57', 'adm-001', '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL, NULL);
INSERT INTO `doctors` VALUES ('doc-004', 'usr-doc-004', '刘医生', 'D0004', 'ASSOCIATE_CHIEF', 'dpt-004', '新生儿疾病、儿童生长发育评估', 'LIC20230004', '刘医生，儿科副主任医师，经验丰富，深受家长信赖。', NULL, NULL, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL, NULL);
INSERT INTO `doctors` VALUES ('doc-005', 'usr-doc-005', '陈医生', 'D0005', 'ATTENDING', 'dpt-005', '白内障、青光眼及近视矫正', 'LIC20230005', '陈医生专攻眼科常见病及手术治疗。', NULL, '2025-12-29 14:20:57', 'adm-001', '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL, NULL);
INSERT INTO `doctors` VALUES ('doc-006', 'usr-doc-006', '口腔科医生', 'D0006', 'ATTENDING', 'dpt-006', '口腔疾病诊治、牙齿修复', 'LIC20230006', '专业口腔科医生，擅长口腔疾病诊治和牙齿修复。', NULL, '2025-12-30 12:59:39', 'adm-001', '2025-12-30 12:59:39', '2025-12-30 12:59:39', NULL, NULL);
INSERT INTO `doctors` VALUES ('doc-007', 'usr-doc-007', '妇科医生', 'D0007', 'ATTENDING', 'dpt-007', '妇科疾病诊治、妇科常规检查', 'LIC20230007', '专业妇科医生，擅长妇科疾病诊治和常规检查。', NULL, '2025-12-30 12:59:39', 'adm-001', '2025-12-30 12:59:39', '2025-12-30 12:59:39', NULL, NULL);
INSERT INTO `doctors` VALUES ('fac83766-9464-48f4-8b3d-eeda49dd69b0', 'a2250584-bb99-4c64-a542-4e6bf497ff5d', '张名', '00000000', 'ATTENDING', 'dpt-003', 'General', '13344445555', 'N/A', NULL, '2025-12-30 10:30:06', '4bddc762-b2b2-4795-b04b-e06eba14730e', '2025-12-29 17:30:06', '2025-12-30 10:30:06', NULL, NULL);

-- ----------------------------
-- Table structure for examination_item_departments
-- ----------------------------
DROP TABLE IF EXISTS `examination_item_departments`;
CREATE TABLE `examination_item_departments`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍏宠仈ID (UUID)',
  `examination_item_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '妫�鏌ラ」鐩甀D',
  `department_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绉戝ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_item_dept`(`examination_item_id` ASC, `department_id` ASC) USING BTREE,
  INDEX `idx_examination_item_id`(`examination_item_id` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `examination_item_departments_ibfk_1` FOREIGN KEY (`examination_item_id`) REFERENCES `examination_items` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `examination_item_departments_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '妫�鏌ラ」鐩笌绉戝鍏宠仈琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of examination_item_departments
-- ----------------------------
INSERT INTO `examination_item_departments` VALUES ('6b4ea5a9-e538-11f0-bfab-00ff5cc92c97', 'item-001', 'dpt-001', '2025-12-30 12:31:32');
INSERT INTO `examination_item_departments` VALUES ('6b4eddf7-e538-11f0-bfab-00ff5cc92c97', 'item-001', 'dpt-003', '2025-12-30 12:31:32');
INSERT INTO `examination_item_departments` VALUES ('6b4ee160-e538-11f0-bfab-00ff5cc92c97', 'item-002', 'dpt-003', '2025-12-30 12:31:32');
INSERT INTO `examination_item_departments` VALUES ('6b4ee324-e538-11f0-bfab-00ff5cc92c97', 'item-003', 'dpt-006', '2025-12-30 12:31:32');
INSERT INTO `examination_item_departments` VALUES ('6b4ee534-e538-11f0-bfab-00ff5cc92c97', 'item-004', 'dpt-005', '2025-12-30 12:31:32');
INSERT INTO `examination_item_departments` VALUES ('6b4ee776-e538-11f0-bfab-00ff5cc92c97', 'item-005', 'dpt-007', '2025-12-30 12:31:32');

-- ----------------------------
-- Table structure for examination_items
-- ----------------------------
DROP TABLE IF EXISTS `examination_items`;
CREATE TABLE `examination_items`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '妫�鏌ラ」鐩甀D (UUID)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '椤圭洰鍚嶇О',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '椤圭洰鎻忚堪',
  `price` decimal(10, 2) NOT NULL COMMENT '椤圭洰浠锋牸',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '椤圭洰绫诲埆 (濡�: 甯歌妫�鏌�, 琛�娑叉鏌�, 褰卞儚妫�鏌�)',
  `department_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '鍏宠仈绉戝ID (鍙��)',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '鏄惁鍚敤',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE,
  INDEX `department_id`(`department_id` ASC) USING BTREE,
  INDEX `idx_examination_items_category`(`category` ASC) USING BTREE,
  INDEX `idx_examination_items_enabled`(`enabled` ASC) USING BTREE,
  CONSTRAINT `examination_items_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '妫�鏌ラ」鐩〃' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of examination_items
-- ----------------------------
INSERT INTO `examination_items` VALUES ('item-001', '健康体检', '定期健康体检有助于及早发现潜在疾病，建议每年进行一次全面体检，关注身体各项指标变化。', 0.00, '常规检查', NULL, 1, '2025-12-30 12:31:32', '2025-12-30 12:31:32');
INSERT INTO `examination_items` VALUES ('item-002', '皮肤检查', '定期进行皮肤检查可早期发现皮肤病变，特别是有皮肤疾病家族史或经常暴露在阳光下的人群。', 0.00, '专科检查', NULL, 1, '2025-12-30 12:31:32', '2025-12-30 12:31:32');
INSERT INTO `examination_items` VALUES ('item-003', '口腔检查', '定期口腔检查可预防龋齿、牙周病等口腔疾病，建议每半年到一年进行一次口腔检查和洁牙。', 0.00, '专科检查', NULL, 1, '2025-12-30 12:31:32', '2025-12-30 12:31:32');
INSERT INTO `examination_items` VALUES ('item-004', '眼科检查', '定期眼科检查可早期发现近视、远视、青光眼、白内障等眼部疾病，建议每年进行一次视力检查。', 0.00, '专科检查', NULL, 1, '2025-12-30 12:31:32', '2025-12-30 12:31:32');
INSERT INTO `examination_items` VALUES ('item-005', '妇科检查', '定期妇科检查是女性健康管理的重要组成部分，建议每年进行一次妇科常规检查，包括妇科内诊、宫颈检查等。', 0.00, '专科检查', NULL, 1, '2025-12-30 12:31:32', '2025-12-30 12:31:32');

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閫氱煡ID (UUID)',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐢ㄦ埛ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閫氱煡鏍囬',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閫氱煡鍐呭',
  `type` enum('APPOINTMENT','REMINDER','SYSTEM','CANCEL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閫氱煡绫诲瀷',
  `related_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '鍏宠仈ID锛堝棰勭害ID锛�',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '鏄惁宸茶',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_notifications_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_notifications_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_notifications_type`(`type` ASC) USING BTREE,
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '娑堟伅閫氱煡琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notifications
-- ----------------------------
INSERT INTO `notifications` VALUES ('02686d19-40f3-42c6-9668-c1f5cf68db36', 'afb164d3-ad85-47bb-9808-34519df19116', '预约成功', '您已成功预约孟新医生，就诊时间：2025年12月31日 上午，预约单号：GH20251230958508。请按时就诊。', 'APPOINTMENT', 'GH20251230958508', 0, '2025-12-30 13:47:42');
INSERT INTO `notifications` VALUES ('456f936f-abc2-480e-9ca9-66b338c25c20', 'usr-doc-003', '请假申请已批准', '您的请假申请（2026-01-01 至 2026-01-02）已被批准', 'SYSTEM', 'leave-012', 0, '2025-12-30 09:51:22');
INSERT INTO `notifications` VALUES ('9e9de58e-c4eb-45fd-9bee-24beb7422b7e', 'afb164d3-ad85-47bb-9808-34519df19116', '预约已取消', '您的预约已取消。原因：用户主动取消', 'CANCEL', NULL, 1, '2025-12-29 19:01:16');
INSERT INTO `notifications` VALUES ('a196f2dc-b941-4414-9495-7a4e7b025c21', 'afb164d3-ad85-47bb-9808-34519df19116', '预约成功', '您已成功预约孟听医生，就诊时间：2026年01月07日 上午，预约单号：GH20251230587211。请按时就诊。', 'APPOINTMENT', 'GH20251230587211', 1, '2025-12-30 10:21:18');
INSERT INTO `notifications` VALUES ('c14160e5-e35e-468f-bd38-0bc56c4ce632', 'afb164d3-ad85-47bb-9808-34519df19116', '预约成功', '您已成功预约陈医生医生，就诊时间：2025年12月31日 上午，预约单号：GH20251229895429。请按时就诊。', 'APPOINTMENT', 'GH20251229895429', 1, '2025-12-29 18:56:11');
INSERT INTO `notifications` VALUES ('ntf-001', 'usr-pat-001', '预约成功提醒', '您已成功预约2025-12-30上午 心血管内科-李医生 的门诊，请准时就诊。', 'APPOINTMENT', 'apt-001', 0, '2025-12-29 14:20:57');
INSERT INTO `notifications` VALUES ('ntf-002', 'usr-pat-002', '就诊完成评价邀请', '您在骨科的就诊已完成，欢迎对张医生做出评价。', 'SYSTEM', 'apt-002', 1, '2025-12-29 14:20:57');
INSERT INTO `notifications` VALUES ('ntf-003', 'usr-pat-004', '预约取消确认', '您预约的 心血管内科-李医生 的门诊已成功取消。', 'CANCEL', 'apt-003', 0, '2025-12-29 14:20:57');
INSERT INTO `notifications` VALUES ('ntf-004', 'usr-pat-003', '预约成功提醒', '您已成功预约2025-12-30下午 皮肤科-王医生 的门诊，请准时就诊。', 'APPOINTMENT', 'apt-004', 0, '2025-12-29 14:20:57');
INSERT INTO `notifications` VALUES ('ntf-005', 'usr-adm-001', '新医生注册待审批', '医生“刘医生”已提交注册申请，请您尽快前往管理员后台进行审批。', 'SYSTEM', 'doc-004', 0, '2025-12-29 14:20:57');

-- ----------------------------
-- Table structure for patients
-- ----------------------------
DROP TABLE IF EXISTS `patients`;
CREATE TABLE `patients`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎮ｈ�匢D (UUID)',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍏宠仈鐢ㄦ埛ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '濮撳悕',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '韬唤璇佸彿',
  `gender` enum('MALE','FEMALE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎬у埆',
  `birth_date` date NOT NULL COMMENT '鍑虹敓鏃ユ湡',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '鐥呭彶',
  `allergy_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '杩囨晱鍙�',
  `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '绱ф�ヨ仈绯讳汉',
  `emergency_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '绱ф�ヨ仈绯荤數璇�',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card` ASC) USING BTREE,
  INDEX `idx_patients_id_card`(`id_card` ASC) USING BTREE,
  INDEX `idx_patients_name`(`name` ASC) USING BTREE,
  CONSTRAINT `patients_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鎮ｈ�呰〃' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patients
-- ----------------------------
INSERT INTO `patients` VALUES ('a3c6dd79-4f0e-4ca0-927e-50dc62bc6111', 'afb164d3-ad85-47bb-9808-34519df19116', '李想', '130684200602157028', 'FEMALE', '2006-02-15', NULL, NULL, NULL, NULL, '2025-12-29 15:16:55', '2025-12-29 15:16:55');
INSERT INTO `patients` VALUES ('pat-001', 'usr-pat-001', '赵小明', '110101199003071234', 'MALE', '1990-03-07', '无', '青霉素过敏', NULL, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `patients` VALUES ('pat-002', 'usr-pat-002', '孙小美', '110101198505154321', 'FEMALE', '1985-05-15', '高血压病史', '无', NULL, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `patients` VALUES ('pat-003', 'usr-pat-003', '周先生', '44010119880808223X', 'MALE', '1988-08-08', '2型糖尿病', '无', NULL, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `patients` VALUES ('pat-004', 'usr-pat-004', '吴女士', '310101199501201122', 'FEMALE', '1995-01-20', '无', '海鲜过敏', NULL, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `patients` VALUES ('pat-005', 'usr-pat-005', '郑小童', '440301201806013344', 'FEMALE', '2018-06-01', '曾患幼儿急疹', '无', NULL, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');

-- ----------------------------
-- Table structure for schedules
-- ----------------------------
DROP TABLE IF EXISTS `schedules`;
CREATE TABLE `schedules`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎺掔彮ID (UUID)',
  `doctor_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍖荤敓ID',
  `schedule_date` date NOT NULL COMMENT '鎺掔彮鏃ユ湡',
  `is_working` tinyint(1) NULL DEFAULT 1 COMMENT '鏄惁宸ヤ綔',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '澶囨敞',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_doctor_date`(`doctor_id` ASC, `schedule_date` ASC) USING BTREE,
  INDEX `idx_schedules_date`(`schedule_date` ASC) USING BTREE,
  CONSTRAINT `schedules_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鍖荤敓鎺掔彮琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedules
-- ----------------------------
INSERT INTO `schedules` VALUES ('6745dda0-6c7a-4b07-a087-a0dfae793d7e', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2026-01-09', 0, '管理员取消排班', '2025-12-30 09:25:48', '2025-12-30 09:51:00');
INSERT INTO `schedules` VALUES ('704f1878-5f6d-400c-bd0c-b262dbb4ea15', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2026-01-06', 1, NULL, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `schedules` VALUES ('7e1bd6d6-6a1f-4a20-b45c-c391f400a532', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2026-01-07', 1, NULL, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `schedules` VALUES ('85185276-2d1a-48c1-9efc-32369a0ccf95', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2026-01-02', 1, NULL, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `schedules` VALUES ('9759956e-5042-41f2-b9e4-e886a3eea005', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2026-01-01', 1, NULL, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `schedules` VALUES ('caa37a8d-8da4-4677-a4d5-0e0cd73dddd3', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2026-01-08', 1, NULL, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `schedules` VALUES ('db045a86-2796-4928-90aa-3c02867545ec', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2025-12-31', 1, NULL, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `schedules` VALUES ('e354462a-9eca-43bb-b9ec-997c718ff0d4', 'bf18a1fa-1e8d-4dcb-a30c-a1003228412a', '2026-01-05', 1, NULL, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `schedules` VALUES ('sch-001', 'doc-001', '2025-12-30', 1, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `schedules` VALUES ('sch-002', 'doc-001', '2025-12-31', 1, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `schedules` VALUES ('sch-004', 'doc-002', '2025-12-30', 1, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `schedules` VALUES ('sch-005', 'doc-002', '2025-12-24', 1, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `schedules` VALUES ('sch-006', 'doc-003', '2025-12-30', 1, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `schedules` VALUES ('sch-007', '67d5fa84-268b-4125-8000-306d35c34245', '2025-12-31', 1, NULL, '2025-12-29 14:20:57', '2025-12-29 22:56:27');

-- ----------------------------
-- Table structure for time_slots
-- ----------------------------
DROP TABLE IF EXISTS `time_slots`;
CREATE TABLE `time_slots`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏃堕棿娈礗D (UUID)',
  `schedule_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎺掔彮ID',
  `period` enum('MORNING','AFTERNOON','EVENING') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏃堕棿娈�',
  `start_time` time NOT NULL COMMENT '寮�濮嬫椂闂�',
  `end_time` time NOT NULL COMMENT '缁撴潫鏃堕棿',
  `total_slots` int NOT NULL DEFAULT 20 COMMENT '鎬诲彿婧�',
  `remaining_slots` int NOT NULL DEFAULT 20 COMMENT '鍓╀綑鍙锋簮',
  `version` int NOT NULL DEFAULT 0 COMMENT '涔愯閿佺増鏈彿',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_schedule_period`(`schedule_id` ASC, `period` ASC) USING BTREE,
  INDEX `idx_time_slots_remaining`(`remaining_slots` ASC) USING BTREE,
  CONSTRAINT `time_slots_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `schedules` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鏃堕棿娈靛彿婧愯〃' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of time_slots
-- ----------------------------
INSERT INTO `time_slots` VALUES ('10eaf57b-39b9-4b5c-b869-d3e92434e13d', '9759956e-5042-41f2-b9e4-e886a3eea005', 'MORNING', '08:00:00', '12:00:00', 20, 20, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('19505606-894b-4532-816a-09ae268b7219', '7e1bd6d6-6a1f-4a20-b45c-c391f400a532', 'MORNING', '08:00:00', '12:00:00', 20, 19, 1, '2025-12-30 09:25:48', '2025-12-30 10:21:17');
INSERT INTO `time_slots` VALUES ('1ff5cd20-7cf4-4346-8727-144215e1a327', 'db045a86-2796-4928-90aa-3c02867545ec', 'MORNING', '08:00:00', '12:00:00', 20, 20, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('376cc9cc-7d59-4ed6-a580-36eda549fef6', 'caa37a8d-8da4-4677-a4d5-0e0cd73dddd3', 'MORNING', '08:00:00', '12:00:00', 20, 20, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('41bb2b60-0ff1-4737-ac52-e276773e2283', 'db045a86-2796-4928-90aa-3c02867545ec', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('4c109d15-227e-47ba-8760-0ea88f39124a', '6745dda0-6c7a-4b07-a087-a0dfae793d7e', 'MORNING', '08:00:00', '12:00:00', 20, 20, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('567c10aa-03f3-43f8-b1c9-5cecfce01c28', '704f1878-5f6d-400c-bd0c-b262dbb4ea15', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('6bdb1d95-814c-4f74-b8c6-3b785de311ac', '704f1878-5f6d-400c-bd0c-b262dbb4ea15', 'MORNING', '08:00:00', '12:00:00', 20, 20, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('73897cea-72c9-4a13-a67a-4e7d71f1b997', '7e1bd6d6-6a1f-4a20-b45c-c391f400a532', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('b42c3c8d-f628-4c02-879c-2daac643c421', '85185276-2d1a-48c1-9efc-32369a0ccf95', 'MORNING', '08:00:00', '12:00:00', 20, 20, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('d02e1f83-ba15-4fd4-9dd7-d3ff7929e76c', 'e354462a-9eca-43bb-b9ec-997c718ff0d4', 'MORNING', '08:00:00', '12:00:00', 20, 20, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('d6b97f7b-eb74-4294-8370-4315f7d7199f', '85185276-2d1a-48c1-9efc-32369a0ccf95', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('e2f1136e-e2e2-42b1-8866-65556a25634b', '9759956e-5042-41f2-b9e4-e886a3eea005', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('e4705934-1812-4800-a15f-190b62120d39', 'caa37a8d-8da4-4677-a4d5-0e0cd73dddd3', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('f722f5c5-ac6a-485f-8b73-c35db30dc8ed', 'e354462a-9eca-43bb-b9ec-997c718ff0d4', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('f833a75e-5224-43fd-ae85-9ba4d5b4d9f7', '6745dda0-6c7a-4b07-a087-a0dfae793d7e', 'AFTERNOON', '14:00:00', '17:30:00', 15, 15, 0, '2025-12-30 09:25:48', '2025-12-30 09:25:48');
INSERT INTO `time_slots` VALUES ('ts-001', 'sch-001', 'MORNING', '09:00:00', '12:00:00', 20, 18, 1, '2025-12-29 14:20:57', '2025-12-29 18:31:19');
INSERT INTO `time_slots` VALUES ('ts-002', 'sch-001', 'AFTERNOON', '14:00:00', '17:00:00', 20, 20, 0, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `time_slots` VALUES ('ts-003', 'sch-002', 'MORNING', '09:00:00', '12:00:00', 15, 15, 0, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `time_slots` VALUES ('ts-004', 'sch-004', 'MORNING', '08:30:00', '11:30:00', 25, 25, 0, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `time_slots` VALUES ('ts-005', 'sch-005', 'AFTERNOON', '13:30:00', '16:30:00', 20, 0, 1, '2025-12-29 14:20:57', '2025-12-29 14:20:57');
INSERT INTO `time_slots` VALUES ('ts-006', 'sch-006', 'AFTERNOON', '14:00:00', '17:30:00', 30, 28, 1, '2025-12-29 14:20:57', '2025-12-29 18:11:30');
INSERT INTO `time_slots` VALUES ('ts-007', 'sch-007', 'MORNING', '09:00:00', '12:00:00', 10, 9, 2, '2025-12-29 14:20:57', '2025-12-30 13:47:42');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐢ㄦ埛ID (UUID)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎵嬫満鍙�',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '瀵嗙爜鍝堝笇',
  `role` enum('PATIENT','DOCTOR','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐢ㄦ埛瑙掕壊',
  `status` enum('ACTIVE','PENDING','LOCKED','DISABLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '璐︽埛鐘舵��',
  `login_failures` int NULL DEFAULT 0 COMMENT '杩炵画鐧诲綍澶辫触娆℃暟',
  `locked_until` datetime NULL DEFAULT NULL COMMENT '閿佸畾鎴鏃堕棿',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `last_login_at` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  INDEX `idx_users_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_users_role`(`role` ASC) USING BTREE,
  INDEX `idx_users_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鐢ㄦ埛琛�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('138fe1f1-228b-4f0b-a4ea-aef1d6108da7', '15566667777', '$2a$10$7ffiiWdvbuyl5s5nZc3VOux4602kZs4g7C.FAmWrJ6t6gHN.Ylxv6', 'DOCTOR', 'ACTIVE', 2, NULL, '2025-12-29 19:50:50', '2025-12-29 19:55:16', NULL);
INSERT INTO `users` VALUES ('30ea84bb-6023-4d1a-ba71-2fb3f1711c86', '13355556666', '$2a$10$Vj7DUaB6SfvjI.3Bf0DleOT3tw7J40BIpCj95EbpYfJxiuOpgbj2u', 'ADMIN', 'ACTIVE', 0, NULL, '2025-12-30 08:49:38', '2025-12-30 08:49:38', NULL);
INSERT INTO `users` VALUES ('4bddc762-b2b2-4795-b04b-e06eba14730e', '17731862570', '$2a$10$/mvmEycNhJ5Mp0N0C.hAWu/F46S3HwIo5jJ5e/cCbwJV2VgIctE.a', 'ADMIN', 'ACTIVE', 0, NULL, '2025-12-30 10:29:38', '2025-12-30 17:23:09', '2025-12-30 17:23:09');
INSERT INTO `users` VALUES ('539a90b4-c1a0-4ab4-aadc-35f1ead13c46', '13582227146', '$2a$10$W9kNXJiOCIkGHwjMTBgZGujC6JUf/wgrP0SpqKbj.lD/Gl0ZTiDEC', 'DOCTOR', 'ACTIVE', 0, NULL, '2025-12-29 20:04:24', '2025-12-30 17:21:01', '2025-12-30 17:21:01');
INSERT INTO `users` VALUES ('a2250584-bb99-4c64-a542-4e6bf497ff5d', '13388889999', '$2a$10$6RvB37SHbFKFmpotJwVv8uekuODvm1kMAMPlOBfLQqkMSTgRKWIPS', 'DOCTOR', 'ACTIVE', 0, NULL, '2025-12-29 17:30:06', '2025-12-30 10:30:06', NULL);
INSERT INTO `users` VALUES ('afb164d3-ad85-47bb-9808-34519df19116', '13344445555', '$2a$10$cu1PLG6V2AWfl2SDmEB/SO5DEOAoqH1pIi/dtbdLsTJLhlBw6Vvhe', 'PATIENT', 'ACTIVE', 0, NULL, '2025-12-29 15:16:55', '2025-12-30 10:20:57', NULL);
INSERT INTO `users` VALUES ('ec934f66-6b05-4ae8-b44c-a75148744558', '17731862571', '$2a$10$a0fQzfiAkcZk8Q7.pfMS1.h/rRp0GdI.nlfBNwFvc7U6dIiAyBOLi', 'ADMIN', 'ACTIVE', 3, NULL, '2025-12-30 10:28:47', '2025-12-30 15:06:29', NULL);
INSERT INTO `users` VALUES ('usr-adm-001', '13800000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'ADMIN', 'ACTIVE', 3, NULL, '2025-12-29 14:20:57', '2025-12-29 23:20:45', NULL);
INSERT INTO `users` VALUES ('usr-doc-001', '13800000002', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE', 1, NULL, '2025-12-29 14:20:57', '2025-12-29 20:00:00', NULL);
INSERT INTO `users` VALUES ('usr-doc-002', '13800000003', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-doc-003', '13900000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-doc-004', '13900000002', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'PENDING', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-doc-005', '13900000003', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-doc-006', '13800000006', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE', 0, NULL, '2025-12-30 12:59:39', '2025-12-30 12:59:39', NULL);
INSERT INTO `users` VALUES ('usr-doc-007', '13800000007', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE', 0, NULL, '2025-12-30 12:59:39', '2025-12-30 12:59:39', NULL);
INSERT INTO `users` VALUES ('usr-pat-001', '13800000004', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-pat-002', '13800000005', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-pat-003', '13700000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-pat-004', '13700000002', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'LOCKED', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);
INSERT INTO `users` VALUES ('usr-pat-005', '13700000003', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE', 0, NULL, '2025-12-29 14:20:57', '2025-12-29 14:20:57', NULL);

SET FOREIGN_KEY_CHECKS = 1;
