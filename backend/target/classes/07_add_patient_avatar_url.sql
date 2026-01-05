-- 为患者表添加头像URL字段
-- 执行此脚本以为现有的patients表添加avatar_url字段

-- 检查字段是否存在，如果不存在则添加
SET @dbname = DATABASE();
SET @tablename = 'patients';
SET @columnname = 'avatar_url';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) COMMENT ''头像URL''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 为现有患者生成默认头像URL（可选，使用UI Avatars API）
-- 注意：这里只是示例，实际使用时可以根据需要更新
-- UPDATE patients SET avatar_url = CONCAT('https://ui-avatars.com/api/?name=', name, '&size=128&background=random&color=fff&bold=true&format=png') WHERE avatar_url IS NULL OR avatar_url = '';

