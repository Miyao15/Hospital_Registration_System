-- 添加头像数据字段（Base64存储）

-- 为患者表添加 avatar_data 字段
ALTER TABLE patients ADD COLUMN avatar_data MEDIUMTEXT;

-- 为医生表添加 avatar_data 字段  
ALTER TABLE doctors ADD COLUMN avatar_data MEDIUMTEXT;
