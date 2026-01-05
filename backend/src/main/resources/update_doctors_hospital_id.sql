-- 更新现有医生的hospital_id
-- 这个脚本会将现有医生分配到已有的医院中

-- 注意：此脚本需要根据实际情况调整
-- 建议：先查看现有医生和医院数据，然后手动分配

-- 示例：将所有没有hospital_id的医生分配到第一个医院（天津医科大学总医院）
-- 实际使用时，建议根据医生的科室或其他信息进行更合理的分配

-- 方法1：将所有医生分配到第一个医院（仅作为示例）
-- UPDATE doctors d
-- SET d.hospital_id = (SELECT id FROM hospitals WHERE name = '天津医科大学总医院' LIMIT 1)
-- WHERE d.hospital_id IS NULL OR d.hospital_id = '';

-- 方法2：根据医生的科室或其他信息进行分配（需要根据实际情况编写）
-- 这里提供一个模板，您可以根据实际数据调整

-- 如果您想手动分配，可以使用以下查询查看需要更新的医生：
SELECT 
    d.id,
    d.name,
    d.employee_id,
    d.department_id,
    dept.name AS department_name,
    d.hospital_id,
    h.name AS hospital_name
FROM doctors d
LEFT JOIN departments dept ON d.department_id = dept.id
LEFT JOIN hospitals h ON d.hospital_id = h.id
WHERE d.hospital_id IS NULL OR d.hospital_id = '';

-- 查看所有可用的医院：
SELECT id, name, city, district FROM hospitals WHERE enabled = TRUE ORDER BY city, district;

-- 更新示例（请根据实际情况修改）：
-- 将某个医生分配到指定医院（使用易读的ID格式）

-- 示例1：将医生分配到天津医科大学总医院
-- UPDATE doctors 
-- SET hospital_id = 'hosp-001' 
-- WHERE id = '医生ID';

-- 示例2：将所有没有医院的医生分配到天津医科大学总医院（默认）
-- UPDATE doctors 
-- SET hospital_id = 'hosp-001'
-- WHERE hospital_id IS NULL OR hospital_id = '';

-- 示例3：根据医院名称更新（推荐，更直观）
-- UPDATE doctors d
-- SET d.hospital_id = (SELECT id FROM hospitals WHERE name = '天津医科大学总医院' LIMIT 1)
-- WHERE d.hospital_id IS NULL OR d.hospital_id = '';

-- 医院ID对应表：
-- hosp-001 = 天津医科大学总医院
-- hosp-002 = 天津市第一中心医院
-- hosp-003 = 天津市肿瘤医院
-- hosp-004 = 天津市人民医院
-- hosp-005 = 天津市第三中心医院
-- hosp-006 = 天津医学高等专科学校附属天和医院
-- hosp-007 = 天津市中西医结合医院

