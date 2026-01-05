-- =================================================================================================
-- 添加缺失的科室：口腔科和妇科
-- =================================================================================================
-- 说明：为了支持"牙齿清洁"和"年度妇科检查"这两个健康检查推荐项目，需要添加对应的科室

-- 检查并添加口腔科（如果不存在）
INSERT INTO departments (id, name, category, description, location, phone, sort_order, enabled) 
SELECT 'dpt-006', '口腔科', '其他', '诊治口腔、牙齿、牙龈及相关疾病的专科。', '门诊大楼一层D区', '010-12345676', 6, 1
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 'dpt-006' OR name = '口腔科');

-- 检查并添加妇科（如果不存在）
INSERT INTO departments (id, name, category, description, location, phone, sort_order, enabled) 
SELECT 'dpt-007', '妇科', '其他', '为女性提供妇科疾病诊断、治疗和预防保健服务。', '门诊大楼二层C区', '010-12345677', 7, 1
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 'dpt-007' OR name = '妇科');

-- 验证新增的科室
SELECT * FROM departments WHERE id IN ('dpt-006', 'dpt-007');

