-- 添加示例医院数据
-- 这些是天津的一些主要医院

-- 先删除可能存在的测试数据（可选，根据需要取消注释）
-- DELETE FROM hospitals WHERE name LIKE '%测试%' OR name LIKE '%示例%';

-- 天津医科大学总医院（如果已存在则跳过）
INSERT IGNORE INTO hospitals (id, name, address, province, city, district, postal_code, region, phone, longitude, latitude, description, enabled, created_at, updated_at)
VALUES 
(
    'hosp-001',
    '天津医科大学总医院',
    '天津市和平区鞍山道154号',
    '天津市',
    '天津市',
    '和平区',
    '300052',
    '华北',
    '022-60362222',
    117.195907,
    39.104027,
    '天津医科大学总医院是一所集医疗、教学、科研、预防于一体的综合性三级甲等医院。',
    TRUE,
    NOW(),
    NOW()
);

-- 天津市第一中心医院
INSERT IGNORE INTO hospitals (id, name, address, province, city, district, postal_code, region, phone, longitude, latitude, description, enabled, created_at, updated_at)
VALUES 
(
    'hosp-002',
    '天津市第一中心医院',
    '天津市南开区复康路24号',
    '天津市',
    '天津市',
    '南开区',
    '300192',
    '华北',
    '022-23626000',
    117.178862,
    39.109009,
    '天津市第一中心医院是一所集医疗、教学、科研、预防为一体的现代化三级甲等综合医院。',
    TRUE,
    NOW(),
    NOW()
);

-- 天津市肿瘤医院
INSERT IGNORE INTO hospitals (id, name, address, province, city, district, postal_code, region, phone, longitude, latitude, description, enabled, created_at, updated_at)
VALUES 
(
    'hosp-003',
    '天津市肿瘤医院',
    '天津市河西区体院北环湖西路',
    '天津市',
    '天津市',
    '河西区',
    '300060',
    '华北',
    '022-23340123',
    117.184345,
    39.085234,
    '天津市肿瘤医院是集医疗、教学、科研、预防为一体的三级甲等肿瘤专科医院。',
    TRUE,
    NOW(),
    NOW()
);

-- 天津市人民医院
INSERT IGNORE INTO hospitals (id, name, address, province, city, district, postal_code, region, phone, longitude, latitude, description, enabled, created_at, updated_at)
VALUES 
(
    'hosp-004',
    '天津市人民医院',
    '天津市红桥区芥园道190号',
    '天津市',
    '天津市',
    '红桥区',
    '300121',
    '华北',
    '022-87729595',
    117.154321,
    39.148765,
    '天津市人民医院是一所集医疗、教学、科研、预防、保健为一体的三级甲等综合医院。',
    TRUE,
    NOW(),
    NOW()
);

-- 天津市第三中心医院
INSERT IGNORE INTO hospitals (id, name, address, province, city, district, postal_code, region, phone, longitude, latitude, description, enabled, created_at, updated_at)
VALUES 
(
    'hosp-005',
    '天津市第三中心医院',
    '天津市河东区津塘路83号',
    '天津市',
    '天津市',
    '河东区',
    '300170',
    '华北',
    '022-84112000',
    117.243567,
    39.124098,
    '天津市第三中心医院是一所集医疗、教学、科研、预防为一体的三级甲等综合医院。',
    TRUE,
    NOW(),
    NOW()
);

-- 天津医学高等专科学校附属天和医院
INSERT IGNORE INTO hospitals (id, name, address, province, city, district, postal_code, region, phone, longitude, latitude, description, enabled, created_at, updated_at)
VALUES 
(
    'hosp-006',
    '天津医学高等专科学校附属天和医院',
    '天津市河西区马场道188号',
    '天津市',
    '天津市',
    '河西区',
    '300074',
    '华北',
    '022-60362158',
    117.210876,
    39.098765,
    '天津医学高等专科学校附属天和医院是一所集医疗、教学、科研为一体的综合性医院。',
    TRUE,
    NOW(),
    NOW()
);

-- 天津市中西医结合医院
INSERT IGNORE INTO hospitals (id, name, address, province, city, district, postal_code, region, phone, longitude, latitude, description, enabled, created_at, updated_at)
VALUES 
(
    'hosp-007',
    '天津市中西医结合医院',
    '天津市南开区鞍山西道314号',
    '天津市',
    '天津市',
    '南开区',
    '300193',
    '华北',
    '022-27456699',
    117.167432,
    39.112345,
    '天津市中西医结合医院是一所集医疗、教学、科研为一体的三级甲等中西医结合医院。',
    TRUE,
    NOW(),
    NOW()
);

