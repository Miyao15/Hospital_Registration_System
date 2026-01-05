-- 为 appointments 表添加检查项目字段
-- 执行时间：2024

-- 添加 medical_item_id 字段（如果不存在）
ALTER TABLE appointments 
ADD COLUMN IF NOT EXISTS medical_item_id VARCHAR(36) COMMENT '检查项目ID' AFTER time_slot_id;

-- 添加外键约束（如果不存在）
-- 注意：如果表中已有数据，需要先确保 examination_items 表中存在对应的记录
ALTER TABLE appointments 
ADD CONSTRAINT IF NOT EXISTS fk_appointments_medical_item 
FOREIGN KEY (medical_item_id) REFERENCES examination_items(id) ON DELETE SET NULL;

-- 添加索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_appointments_medical_item ON appointments(medical_item_id);

