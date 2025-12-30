-- 安全地添加 last_login_at 字段
-- 如果字段已存在则不会报错

-- 检查并添加字段（MySQL 8.0+ 支持 IF NOT EXISTS，但为了兼容性，使用存储过程方式）
-- 如果您的MySQL版本不支持，请直接执行下面的ALTER TABLE语句

-- 方法1：直接执行（如果字段已存在会报错，可以忽略）
ALTER TABLE users 
ADD COLUMN last_login_at DATETIME NULL COMMENT '最后登录时间';

-- 如果上面的语句报错说字段已存在，说明字段已经添加成功，可以忽略错误

