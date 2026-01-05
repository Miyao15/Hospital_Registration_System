-- 为 patients 表添加 avatar_url 列
-- 先检查列是否存在，不存在则添加（兼容所有 MySQL 版本）

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
  'SELECT ''列已存在，无需添加'' AS result',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) COMMENT ''头像URL''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 如果上面的方式执行失败，可以直接使用下面的简单方式（如果确定列不存在）：
-- ALTER TABLE patients ADD COLUMN avatar_url VARCHAR(255) COMMENT '头像URL';

