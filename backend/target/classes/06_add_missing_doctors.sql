-- =================================================================================================
-- 为缺失的科室添加医生数据
-- =================================================================================================
-- 说明：口腔科（dpt-006）和妇科（dpt-007）是新添加的科室，需要添加对应的医生
-- 否则点击"口腔检查"和"妇科检查"时会显示0位医生

-- =================================================================================================
-- 添加口腔科医生
-- =================================================================================================
-- 1. 添加用户
INSERT IGNORE INTO users (id, phone, password_hash, role, status) VALUES
('usr-doc-006', '13800000006', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE');

-- 2. 添加医生
INSERT IGNORE INTO doctors (id, user_id, name, employee_id, title, department_id, specialty, license_number, introduction, approved_at, approved_by) VALUES
('doc-006', 'usr-doc-006', '口腔科医生', 'D0006', 'ATTENDING', 'dpt-006', '口腔疾病诊治、牙齿修复', 'LIC20230006', '专业口腔科医生，擅长口腔疾病诊治和牙齿修复。', NOW(), 'adm-001');

-- =================================================================================================
-- 添加妇科医生
-- =================================================================================================
-- 1. 添加用户
INSERT IGNORE INTO users (id, phone, password_hash, role, status) VALUES
('usr-doc-007', '13800000007', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'DOCTOR', 'ACTIVE');

-- 2. 添加医生
INSERT IGNORE INTO doctors (id, user_id, name, employee_id, title, department_id, specialty, license_number, introduction, approved_at, approved_by) VALUES
('doc-007', 'usr-doc-007', '妇科医生', 'D0007', 'ATTENDING', 'dpt-007', '妇科疾病诊治、妇科常规检查', 'LIC20230007', '专业妇科医生，擅长妇科疾病诊治和常规检查。', NOW(), 'adm-001');

-- =================================================================================================
-- 验证数据
-- =================================================================================================
-- 检查各科室的医生数量
SELECT 
    d.id,
    d.name as 科室名称,
    COUNT(doc.id) as 医生数量,
    GROUP_CONCAT(doc.name) as 医生姓名
FROM departments d
LEFT JOIN doctors doc ON d.id = doc.department_id
WHERE d.id IN ('dpt-001', 'dpt-003', 'dpt-005', 'dpt-006', 'dpt-007')
GROUP BY d.id, d.name
ORDER BY d.id;

-- 检查每个检查项目能查到多少医生
SELECT 
    ei.id,
    ei.name as 检查项目,
    COUNT(DISTINCT doc.id) as 可用医生数,
    GROUP_CONCAT(DISTINCT CONCAT(d.name, ':', doc.name)) as 科室和医生
FROM examination_items ei
LEFT JOIN examination_item_departments eid ON ei.id = eid.examination_item_id
LEFT JOIN departments d ON eid.department_id = d.id
LEFT JOIN doctors doc ON d.id = doc.department_id
LEFT JOIN users u ON doc.user_id = u.id
WHERE ei.enabled = 1 
  AND (u.status = 'ACTIVE' OR u.status IS NULL)
GROUP BY ei.id, ei.name
ORDER BY ei.id;

