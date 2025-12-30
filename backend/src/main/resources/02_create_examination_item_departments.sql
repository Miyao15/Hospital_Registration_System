-- =================================================================================================
-- 创建检查项目与科室的关联表（多对多关系）
-- =================================================================================================
-- 此表用于实现健康检查推荐项目与科室的多对多关系
-- 一个检查项目可以关联多个科室，一个科室也可以关联多个检查项目

CREATE TABLE IF NOT EXISTS examination_item_departments (
    id VARCHAR(36) PRIMARY KEY COMMENT '关联ID (UUID)',
    examination_item_id VARCHAR(36) NOT NULL COMMENT '检查项目ID',
    department_id VARCHAR(36) NOT NULL COMMENT '科室ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (examination_item_id) REFERENCES examination_items(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    UNIQUE KEY uk_item_dept (examination_item_id, department_id),
    INDEX idx_examination_item_id (examination_item_id),
    INDEX idx_department_id (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检查项目与科室关联表';

