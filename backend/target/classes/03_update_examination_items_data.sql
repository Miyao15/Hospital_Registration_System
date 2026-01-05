-- =================================================================================================
-- 更新检查项目数据：从具体检查项目改为健康检查推荐项目
-- =================================================================================================
-- 说明：
-- 1. 此脚本将清空现有的检查项目数据（具体检查项目如血常规、尿常规等）
-- 2. 插入新的健康检查推荐项目（如年度体检、皮肤检查等）
-- 3. 建立检查项目与科室的关联关系
-- 
-- 注意：如果已有预约数据关联了旧的检查项目，请先备份或处理这些数据

-- =================================================================================================
-- 步骤1：备份现有数据（可选，建议先执行）
-- =================================================================================================
-- CREATE TABLE examination_items_backup AS SELECT * FROM examination_items;
-- CREATE TABLE examination_item_departments_backup AS SELECT * FROM examination_item_departments;

-- =================================================================================================
-- 步骤2：清空现有数据
-- =================================================================================================
-- 先删除关联关系（如果存在）
DELETE FROM examination_item_departments WHERE 1=1;

-- 删除旧的检查项目数据
DELETE FROM examination_items WHERE 1=1;

-- =================================================================================================
-- 步骤3：插入新的健康检查推荐项目（符合中国医院实际情况）
-- =================================================================================================
-- 插入符合中国医院实际情况的健康检查推荐项目
INSERT INTO examination_items (id, name, description, price, category, department_id, enabled, created_at) VALUES
-- 健康体检：常规体检项目，可关联多个科室
('item-001', '健康体检', '定期健康体检有助于及早发现潜在疾病，建议每年进行一次全面体检，关注身体各项指标变化。', 0.00, '常规检查', NULL, 1, NOW()),

-- 皮肤检查：皮肤科专科检查
('item-002', '皮肤检查', '定期进行皮肤检查可早期发现皮肤病变，特别是有皮肤疾病家族史或经常暴露在阳光下的人群。', 0.00, '专科检查', NULL, 1, NOW()),

-- 口腔检查：口腔科专科检查
('item-003', '口腔检查', '定期口腔检查可预防龋齿、牙周病等口腔疾病，建议每半年到一年进行一次口腔检查和洁牙。', 0.00, '专科检查', NULL, 1, NOW()),

-- 眼科检查：眼科专科检查
('item-004', '眼科检查', '定期眼科检查可早期发现近视、远视、青光眼、白内障等眼部疾病，建议每年进行一次视力检查。', 0.00, '专科检查', NULL, 1, NOW()),

-- 妇科检查：妇科专科检查
('item-005', '妇科检查', '定期妇科检查是女性健康管理的重要组成部分，建议每年进行一次妇科常规检查，包括妇科内诊、宫颈检查等。', 0.00, '专科检查', NULL, 1, NOW());

-- =================================================================================================
-- 步骤4：建立检查项目与科室的关联关系
-- =================================================================================================
-- 年度体检：可以关联多个科室（根据医院实际情况调整）
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-001', 'dpt-001', NOW()), -- 心血管内科
(UUID(), 'item-001', 'dpt-003', NOW()); -- 皮肤科（可选）

-- 皮肤检查：主要关联皮肤科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-002', 'dpt-003', NOW()); -- 皮肤科

-- 牙齿清洁：关联口腔科
-- 注意：如果医院没有口腔科，请先执行 04_add_missing_departments.sql 添加口腔科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-003', 'dpt-006', NOW()); -- 口腔科

-- 眼科检查：关联眼科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-004', 'dpt-005', NOW()); -- 眼科

-- 年度妇科检查：关联妇科
-- 注意：如果医院没有妇科，请先执行 04_add_missing_departments.sql 添加妇科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-005', 'dpt-007', NOW()); -- 妇科

-- =================================================================================================
-- 验证数据
-- =================================================================================================
-- SELECT ei.id, ei.name, ei.description, ei.category, 
--        GROUP_CONCAT(d.name) as departments
-- FROM examination_items ei
-- LEFT JOIN examination_item_departments eid ON ei.id = eid.examination_item_id
-- LEFT JOIN departments d ON eid.department_id = d.id
-- WHERE ei.enabled = 1
-- GROUP BY ei.id, ei.name, ei.description, ei.category;

