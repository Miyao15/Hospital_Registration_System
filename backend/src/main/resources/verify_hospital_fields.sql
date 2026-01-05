-- 验证 hospitals 表是否包含所有必需的字段
-- 执行此脚本可以检查字段是否存在

USE hospital_registration;

-- 检查 hospitals 表是否存在
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN 'hospitals 表存在'
        ELSE 'hospitals 表不存在！'
    END AS table_status
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
  AND table_name = 'hospitals';

-- 检查所有必需的字段
SELECT 
    COLUMN_NAME AS '字段名',
    DATA_TYPE AS '数据类型',
    CHARACTER_MAXIMUM_LENGTH AS '最大长度',
    IS_NULLABLE AS '是否可空',
    COLUMN_DEFAULT AS '默认值',
    CASE 
        WHEN COLUMN_NAME IN ('province', 'city', 'district', 'postal_code', 'region') 
        THEN '✓ 新添加的字段'
        ELSE ''
    END AS '备注'
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND table_name = 'hospitals'
ORDER BY ORDINAL_POSITION;

-- 检查缺失的字段
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✓ 所有新字段都已存在'
        ELSE CONCAT('✗ 缺失以下字段: ', GROUP_CONCAT(missing_field SEPARATOR ', '))
    END AS field_status
FROM (
    SELECT 'province' AS missing_field
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = DATABASE() 
          AND table_name = 'hospitals' 
          AND column_name = 'province'
    )
    UNION ALL
    SELECT 'city' AS missing_field
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = DATABASE() 
          AND table_name = 'hospitals' 
          AND column_name = 'city'
    )
    UNION ALL
    SELECT 'district' AS missing_field
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = DATABASE() 
          AND table_name = 'hospitals' 
          AND column_name = 'district'
    )
    UNION ALL
    SELECT 'postal_code' AS missing_field
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = DATABASE() 
          AND table_name = 'hospitals' 
          AND column_name = 'postal_code'
    )
    UNION ALL
    SELECT 'region' AS missing_field
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = DATABASE() 
          AND table_name = 'hospitals' 
          AND column_name = 'region'
    )
) AS missing_fields;



