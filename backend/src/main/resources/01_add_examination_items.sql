-- =================================================================================================
-- Expanded SQL Data Population Script for Hospital_Registration_System
-- =================================================================================================
-- Note: Passwords are plain text for example purposes ('password123').
-- The hash is a bcrypt hash for 'password123': $2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q

-- =================================================================================================
-- 1. Departments (科室表)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误（如果数据已存在则跳过）
INSERT IGNORE INTO `departments` (`id`, `name`, `category`, `description`, `location`, `phone`, `sort_order`, `enabled`) VALUES
('dpt-001', '心血管内科', '内科系', '专注于心血管系统疾病的诊断和治疗，如高血压、冠心病等。', '门诊大楼三层A区', '010-12345671', 1, 1),
('dpt-002', '骨科', '外科系', '处理骨骼、关节、韧带、肌腱和肌肉的损伤和疾病。', '门诊大楼二层B区', '010-12345672', 2, 1),
('dpt-003', '皮肤科', '其他', '诊治各种皮肤、指甲及毛发相关的疾病。', '门诊大楼一层C区', '010-12345673', 3, 1),
('dpt-004', '儿科', '其他', '为婴幼儿、儿童和青少年提供全面的医疗保健服务。', '儿童医疗中心二层', '010-12345674', 4, 1),
('dpt-005', '眼科', '外科系', '负责眼部疾病的诊断、治疗和手术。', '门诊大楼四层A区', '010-12345675', 5, 1),
('dpt-006', '口腔科', '其他', '诊治口腔、牙齿、牙龈及相关疾病的专科。', '门诊大楼一层D区', '010-12345676', 6, 1),
('dpt-007', '妇科', '其他', '为女性提供妇科疾病诊断、治疗和预防保健服务。', '门诊大楼二层C区', '010-12345677', 7, 1);

-- =================================================================================================
-- 2. Users (用户表)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误
INSERT IGNORE INTO `users` (`id`, `phone`, `password_hash`, `role`, `status`) VALUES
-- Admin User
('usr-adm-001', '13800000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'ADMIN', 'ACTIVE'),
-- Doctor Users
('usr-doc-001', '13800000002', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE'),
('usr-doc-002', '13800000003', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE'),
('usr-doc-003', '13900000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE'),
('usr-doc-004', '13900000002', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'PENDING'), -- Pending for approval
('usr-doc-005', '13900000003', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE'),
-- Patient Users
('usr-pat-001', '13800000004', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE'),
('usr-pat-002', '13800000005', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE'),
('usr-pat-003', '13700000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE'),
('usr-pat-004', '13700000002', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'LOCKED'), -- Locked account example
('usr-pat-005', '13700000003', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'PATIENT', 'ACTIVE');


-- =================================================================================================
-- 3. Admins, Doctors, Patients (管理员, 医师, 患者表)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误
INSERT IGNORE INTO `admins` (`id`, `user_id`, `name`, `employee_id`) VALUES ('adm-001', 'usr-adm-001', '王管理', 'A0001');

INSERT IGNORE INTO `doctors` (`id`, `user_id`, `name`, `employee_id`, `title`, `department_id`, `specialty`, `license_number`, `introduction`, `approved_at`, `approved_by`) VALUES
('doc-001', 'usr-doc-001', '李医生', 'D0001', 'CHIEF', 'dpt-001', '高血压、冠心病的诊断与治疗', 'LIC20230001', '李医生是心血管内科主任医师，拥有超过20年的临床经验。', NOW(), 'adm-001'),
('doc-002', 'usr-doc-002', '张医生', 'D0002', 'ATTENDING', 'dpt-002', '骨折、关节置换', 'LIC20230002', '张医生是骨科主治医师，擅长运动损伤和微创手术。', NOW(), 'adm-001'),
('doc-003', 'usr-doc-003', '王医生', 'D0003', 'RESIDENT', 'dpt-003', '痤疮、湿疹、皮肤过敏', 'LIC20230003', '王医生是皮肤科的住院医师，对常见皮肤病有深入研究。', NOW(), 'adm-001'),
('doc-004', 'usr-doc-004', '刘医生', 'D0004', 'ASSOCIATE_CHIEF', 'dpt-004', '新生儿疾病、儿童生长发育评估', 'LIC20230004', '刘医生，儿科副主任医师，经验丰富，深受家长信赖。', NULL, NULL), -- Pending approval
('doc-005', 'usr-doc-005', '陈医生', 'D0005', 'ATTENDING', 'dpt-005', '白内障、青光眼及近视矫正', 'LIC20230005', '陈医生专攻眼科常见病及手术治疗。', NOW(), 'adm-001');

INSERT IGNORE INTO `patients` (`id`, `user_id`, `name`, `id_card`, `gender`, `birth_date`, `medical_history`, `allergy_history`) VALUES
('pat-001', 'usr-pat-001', '赵小明', '110101199003071234', 'MALE', '1990-03-07', '无', '青霉素过敏'),
('pat-002', 'usr-pat-002', '孙小美', '110101198505154321', 'FEMALE', '1985-05-15', '高血压病史', '无'),
('pat-003', 'usr-pat-003', '周先生', '44010119880808223X', 'MALE', '1988-08-08', '2型糖尿病', '无'),
('pat-004', 'usr-pat-004', '吴女士', '310101199501201122', 'FEMALE', '1995-01-20', '无', '海鲜过敏'),
('pat-005', 'usr-pat-005', '郑小童', '440301201806013344', 'FEMALE', '2018-06-01', '曾患幼儿急疹', '无');


-- =================================================================================================
-- 6. Schedules & Time Slots (排班与号源)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误
-- Dr. Li (doc-001) for next 2 days
INSERT IGNORE INTO `schedules` (`id`, `doctor_id`, `schedule_date`, `is_working`) VALUES ('sch-001', 'doc-001', CURDATE() + INTERVAL 1 DAY, 1), ('sch-002', 'doc-001', CURDATE() + INTERVAL 2 DAY, 1);
INSERT IGNORE INTO `time_slots` (`id`, `schedule_id`, `period`, `start_time`, `end_time`, `total_slots`, `remaining_slots`, `version`) VALUES
('ts-001', 'sch-001', 'MORNING', '09:00:00', '12:00:00', 20, 19, 0), -- 1 slot taken
('ts-002', 'sch-001', 'AFTERNOON', '14:00:00', '17:00:00', 20, 20, 0),
('ts-003', 'sch-002', 'MORNING', '09:00:00', '12:00:00', 15, 15, 0);

-- Dr. Zhang (doc-002) for tomorrow and a past date
INSERT IGNORE INTO `schedules` (`id`, `doctor_id`, `schedule_date`, `is_working`) VALUES ('sch-004', 'doc-002', CURDATE() + INTERVAL 1 DAY, 1), ('sch-005', 'doc-002', CURDATE() - INTERVAL 5 DAY, 1);
INSERT IGNORE INTO `time_slots` (`id`, `schedule_id`, `period`, `start_time`, `end_time`, `total_slots`, `remaining_slots`, `version`) VALUES
('ts-004', 'sch-004', 'MORNING', '08:30:00', '11:30:00', 25, 25, 0),
('ts-005', 'sch-005', 'AFTERNOON', '13:30:00', '16:30:00', 20, 0, 1); -- Assumed version incremented due to updates

-- Dr. Wang (doc-003)
INSERT IGNORE INTO `schedules` (`id`, `doctor_id`, `schedule_date`, `is_working`) VALUES ('sch-006', 'doc-003', CURDATE() + INTERVAL 1 DAY, 1);
INSERT IGNORE INTO `time_slots` (`id`, `schedule_id`, `period`, `start_time`, `end_time`, `total_slots`, `remaining_slots`, `version`) VALUES
('ts-006', 'sch-006', 'AFTERNOON', '14:00:00', '17:30:00', 30, 29, 0); -- 1 slot taken

-- Dr. Chen (doc-005)
INSERT IGNORE INTO `schedules` (`id`, `doctor_id`, `schedule_date`, `is_working`) VALUES ('sch-007', 'doc-005', CURDATE() + INTERVAL 2 DAY, 1);
INSERT IGNORE INTO `time_slots` (`id`, `schedule_id`, `period`, `start_time`, `end_time`, `total_slots`, `remaining_slots`, `version`) VALUES
('ts-007', 'sch-007', 'MORNING', '09:00:00', '12:00:00', 10, 10, 0);


-- =================================================================================================
-- 8. Appointments (预约挂号表)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误
INSERT IGNORE INTO `appointments` (`id`, `appointment_no`, `patient_id`, `doctor_id`, `department_id`, `time_slot_id`, `appointment_date`, `period`, `patient_name`, `patient_phone`, `symptom_desc`, `status`) VALUES
-- Pending appointment
('apt-001', CONCAT('APT', DATE_FORMAT(NOW(), '%Y%m%d'), '001'), 'pat-001', 'doc-001', 'dpt-001', 'ts-001', CURDATE() + INTERVAL 1 DAY, 'MORNING', '赵小明', '13800000004', '最近一周感到胸闷，偶尔心悸。', 'PENDING'),
-- Completed appointment in the past
('apt-002', CONCAT('APT', DATE_FORMAT(CURDATE() - INTERVAL 5 DAY, '%Y%m%d'), '002'), 'pat-002', 'doc-002', 'dpt-002', 'ts-005', CURDATE() - INTERVAL 5 DAY, 'AFTERNOON', '孙小美', '13800000005', '手腕扭伤复查。', 'COMPLETED'),
-- Cancelled appointment
('apt-003', CONCAT('APT', DATE_FORMAT(NOW(), '%Y%m%d'), '003'), 'pat-004', 'doc-001', 'dpt-001', 'ts-002', CURDATE() + INTERVAL 1 DAY, 'AFTERNOON', '吴女士', '13700000002', '临时有事，取消预约。', 'CANCELLED'),
-- Another pending appointment
('apt-004', CONCAT('APT', DATE_FORMAT(NOW(), '%Y%m%d'), '004'), 'pat-003', 'doc-003', 'dpt-003', 'ts-006', CURDATE() + INTERVAL 1 DAY, 'AFTERNOON', '周先生', '13700000001', '脸上出现红疹，很痒。', 'PENDING');


-- =================================================================================================
-- 9. Doctor Reviews (医生评价表)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误
INSERT IGNORE INTO `doctor_reviews` (`id`, `doctor_id`, `patient_id`, `rating`, `content`, `created_at`) VALUES
('rev-001', 'doc-002', 'pat-002', 5, '张医生非常专业和耐心，手术很成功，恢复得很好！', CURDATE() - INTERVAL 4 DAY),
('rev-002', 'doc-001', 'pat-001', 4, '李医生看诊很仔细，就是等待时间有点长。', CURDATE() - INTERVAL 10 DAY);


-- =================================================================================================
-- 10. Notifications (消息通知表)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误
INSERT IGNORE INTO `notifications` (`id`, `user_id`, `title`, `content`, `type`, `related_id`, `is_read`) VALUES
('ntf-001', 'usr-pat-001', '预约成功提醒', CONCAT('您已成功预约', CURDATE() + INTERVAL 1 DAY, '上午 心血管内科-李医生 的门诊，请准时就诊。'), 'APPOINTMENT', 'apt-001', 0),
('ntf-002', 'usr-pat-002', '就诊完成评价邀请', '您在骨科的就诊已完成，欢迎对张医生做出评价。', 'SYSTEM', 'apt-002', 1),
('ntf-003', 'usr-pat-004', '预约取消确认', '您预约的 心血管内科-李医生 的门诊已成功取消。', 'CANCEL', 'apt-003', 0),
('ntf-004', 'usr-pat-003', '预约成功提醒', CONCAT('您已成功预约', CURDATE() + INTERVAL 1 DAY, '下午 皮肤科-王医生 的门诊，请准时就诊。'), 'APPOINTMENT', 'apt-004', 0),
('ntf-005', 'usr-adm-001', '新医生注册待审批', '医生“刘医生”已提交注册申请，请您尽快前往管理员后台进行审批。', 'SYSTEM', 'doc-004', 0);


-- =================================================================================================
-- New: Examination Items (体检项目表)
-- =================================================================================================
-- 使用 INSERT IGNORE 避免重复插入错误
-- 插入示例体检项目数据
-- =================================================================================================
-- 检查项目 (Examination Items) - 使用健康检查推荐项目
-- =================================================================================================
-- 注意：已更新为健康检查推荐项目（如年度体检、皮肤检查等），不再是具体检查项目（如血常规）
-- 从根源删除所有 exam- 开头的具体检查项目，只保留 item- 开头的健康检查推荐项目
-- 先删除关联关系表中的数据（examination_item_id 为 exam- 开头的记录）
DELETE FROM examination_item_departments WHERE examination_item_id LIKE 'exam-%';
-- 删除所有 exam- 开头的检查项目
DELETE FROM examination_items WHERE id LIKE 'exam-%';

-- 插入健康检查推荐项目（item- 开头）
INSERT IGNORE INTO examination_items (id, name, description, price, category, department_id, enabled, created_at) VALUES
('item-001', '健康体检', '定期健康体检有助于及早发现潜在疾病，建议每年进行一次全面体检，关注身体各项指标变化。', 0.00, '常规检查', NULL, 1, NOW()),
('item-002', '皮肤检查', '定期进行皮肤检查可早期发现皮肤病变，特别是有皮肤疾病家族史或经常暴露在阳光下的人群。', 0.00, '专科检查', NULL, 1, NOW()),
('item-003', '口腔检查', '定期口腔检查可预防龋齿、牙周病等口腔疾病，建议每半年到一年进行一次口腔检查和洁牙。', 0.00, '专科检查', NULL, 1, NOW()),
('item-004', '眼科检查', '定期眼科检查可早期发现近视、远视、青光眼、白内障等眼部疾病，建议每年进行一次视力检查。', 0.00, '专科检查', NULL, 1, NOW()),
('item-005', '妇科检查', '定期妇科检查是女性健康管理的重要组成部分，建议每年进行一次妇科常规检查，包括妇科内诊、宫颈检查等。', 0.00, '专科检查', NULL, 1, NOW());

-- =================================================================================================
-- 检查项目与科室关联关系 (Examination Item Departments)
-- =================================================================================================
INSERT IGNORE INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-001', 'dpt-001', NOW()), -- 健康体检 -> 心血管内科
(UUID(), 'item-001', 'dpt-003', NOW()), -- 健康体检 -> 皮肤科
(UUID(), 'item-002', 'dpt-003', NOW()), -- 皮肤检查 -> 皮肤科
(UUID(), 'item-003', 'dpt-006', NOW()), -- 口腔检查 -> 口腔科
(UUID(), 'item-004', 'dpt-005', NOW()), -- 眼科检查 -> 眼科
(UUID(), 'item-005', 'dpt-007', NOW()); -- 妇科检查 -> 妇科