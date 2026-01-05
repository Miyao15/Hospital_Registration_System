-- 添加最后登录时间字段
-- 如果字段已存在，执行此脚本会报错，可以忽略

ALTER TABLE users 
ADD COLUMN last_login_at DATETIME NULL COMMENT '最后登录时间';

