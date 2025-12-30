# 手动执行SQL脚本步骤

## 📋 为什么需要手动执行？

您的配置文件中设置了：
- `ddl-auto: none` - Hibernate 不会自动创建表
- `spring.sql.init.mode: never` - Spring Boot 不会自动执行 SQL 脚本

因此，**必须手动在 Navicat 中执行 SQL 脚本**来创建表和更新数据。

---

## 🚀 执行步骤（在 Navicat 中）

### 步骤1：打开 Navicat 并连接到数据库

1. 打开 Navicat
2. 连接到 `hospital_registration` 数据库
3. 点击"查询"按钮，新建查询窗口

### 步骤2：创建关联表

**执行文件：** `02_create_examination_item_departments.sql`

**或者直接复制以下 SQL 执行：**

```sql
-- 创建检查项目与科室的关联表
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
```

**验证：** 执行后，刷新表列表，应该能看到 `examination_item_departments` 表。

---

### 步骤3：添加缺失的科室

**执行文件：** `04_add_missing_departments.sql`

**或者直接复制以下 SQL 执行：**

```sql
-- 添加口腔科
INSERT INTO departments (id, name, category, description, location, phone, sort_order, enabled) 
SELECT 'dpt-006', '口腔科', '其他', '诊治口腔、牙齿、牙龈及相关疾病的专科。', '门诊大楼一层D区', '010-12345676', 6, 1
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 'dpt-006' OR name = '口腔科');

-- 添加妇科
INSERT INTO departments (id, name, category, description, location, phone, sort_order, enabled) 
SELECT 'dpt-007', '妇科', '其他', '为女性提供妇科疾病诊断、治疗和预防保健服务。', '门诊大楼二层C区', '010-12345677', 7, 1
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 'dpt-007' OR name = '妇科');
```

**验证：** 执行后，查询科室表：
```sql
SELECT * FROM departments ORDER BY sort_order;
```
应该能看到 7 个科室（包括新增的口腔科和妇科）。

---

### 步骤4：备份现有数据（重要！）

在执行更新脚本前，**强烈建议先备份**：

```sql
-- 备份检查项目表
CREATE TABLE IF NOT EXISTS examination_items_backup AS SELECT * FROM examination_items;

-- 备份关联表（如果已有数据）
CREATE TABLE IF NOT EXISTS examination_item_departments_backup AS SELECT * FROM examination_item_departments;

-- 检查是否有预约使用了检查项目
SELECT COUNT(*) as appointments_with_items 
FROM appointments 
WHERE medical_item_id IS NOT NULL;
```

---

### 步骤5：更新检查项目数据

**执行文件：** `03_update_examination_items_data.sql`

**⚠️ 警告：这个脚本会删除所有现有的检查项目数据！**

**或者分步执行：**

#### 5.1 清空关联关系
```sql
DELETE FROM examination_item_departments WHERE 1=1;
```

#### 5.2 删除旧的检查项目
```sql
DELETE FROM examination_items WHERE 1=1;
```

#### 5.3 插入新的健康检查推荐项目
```sql
INSERT INTO examination_items (id, name, description, price, category, department_id, enabled, created_at) VALUES
('item-001', '年度体检', 'CDC建议每年进行一次常规体检，及早发现健康问题。', 0.00, '常规检查', NULL, 1, NOW()),
('item-002', '皮肤检查', '根据美国癌症协会的建议，预防和早期发现是抗击癌症的第一步。', 0.00, '专科检查', NULL, 1, NOW()),
('item-003', '牙齿清洁', '美国牙科协会建议定期清洁以预防牙龈疾病，并降低潜在致命的心脏病和中风风险。', 0.00, '专科检查', NULL, 1, NOW()),
('item-004', '眼科检查', '美国眼科学会建议佩戴隐形眼镜的患者每年进行视力筛查。所有成年人都需要定期进行视力筛查。', 0.00, '专科检查', NULL, 1, NOW()),
('item-005', '年度妇科检查', '美国妇产科学会建议即使不需要进行宫颈癌筛查，也应每年看一次妇科医生进行年度妇科检查。此检查通常包括盆腔检查（有或没有巴氏涂片）。', 0.00, '专科检查', NULL, 1, NOW());
```

#### 5.4 建立关联关系
```sql
-- 年度体检：关联心血管内科和皮肤科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-001', 'dpt-001', NOW()), -- 心血管内科
(UUID(), 'item-001', 'dpt-003', NOW()); -- 皮肤科

-- 皮肤检查：关联皮肤科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-002', 'dpt-003', NOW()); -- 皮肤科

-- 牙齿清洁：关联口腔科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-003', 'dpt-006', NOW()); -- 口腔科

-- 眼科检查：关联眼科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-004', 'dpt-005', NOW()); -- 眼科

-- 年度妇科检查：关联妇科
INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-005', 'dpt-007', NOW()); -- 妇科
```

---

### 步骤6：验证数据

执行以下查询验证数据是否正确：

```sql
-- 1. 检查检查项目数量（应该是5个）
SELECT COUNT(*) as total_items FROM examination_items WHERE enabled = 1;
-- 预期结果：5

-- 2. 检查关联关系数量（应该是6个）
SELECT COUNT(*) as total_relations FROM examination_item_departments;
-- 预期结果：6

-- 3. 检查科室数量（应该是7个）
SELECT COUNT(*) as total_departments FROM departments WHERE enabled = 1;
-- 预期结果：7

-- 4. 查看完整的关联关系
SELECT 
    ei.id,
    ei.name as 检查项目,
    ei.category as 类别,
    GROUP_CONCAT(d.name ORDER BY d.name) as 关联科室
FROM examination_items ei
LEFT JOIN examination_item_departments eid ON ei.id = eid.examination_item_id
LEFT JOIN departments d ON eid.department_id = d.id
WHERE ei.enabled = 1
GROUP BY ei.id, ei.name, ei.category
ORDER BY ei.id;
```

**预期结果：**
| id | 检查项目 | 类别 | 关联科室 |
|---|---|---|---|
| item-001 | 年度体检 | 常规检查 | 皮肤科,心血管内科 |
| item-002 | 皮肤检查 | 专科检查 | 皮肤科 |
| item-003 | 牙齿清洁 | 专科检查 | 口腔科 |
| item-004 | 眼科检查 | 专科检查 | 眼科 |
| item-005 | 年度妇科检查 | 专科检查 | 妇科 |

---

## ✅ 执行完成后的检查清单

- [ ] `examination_item_departments` 表已创建
- [ ] 科室表中有 7 个科室（包括口腔科和妇科）
- [ ] 检查项目表中有 5 个健康检查推荐项目
- [ ] 关联关系表中有 6 条关联记录
- [ ] 所有关联关系都正确建立

---

## 🔄 如果需要恢复数据

如果执行后发现问题，可以从备份恢复：

```sql
-- 恢复检查项目数据
DELETE FROM examination_items;
INSERT INTO examination_items SELECT * FROM examination_items_backup;

-- 恢复关联关系（如果有备份）
DELETE FROM examination_item_departments;
INSERT INTO examination_item_departments SELECT * FROM examination_item_departments_backup;
```

---

## 📝 注意事项

1. **UUID() 函数**：MySQL 的 `UUID()` 函数会生成 UUID 字符串，确保每次执行都生成唯一的 ID
2. **外键约束**：如果删除检查项目，关联的预约数据可能会受影响（因为有外键约束）
3. **数据一致性**：确保科室 ID（dpt-001 到 dpt-007）在 `departments` 表中都存在

---

## 🎯 快速执行（一键脚本）

如果您想一次性执行所有步骤，可以创建一个完整的脚本：

```sql
-- ============================================
-- 完整执行脚本（请先备份！）
-- ============================================

-- 1. 创建关联表
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

-- 2. 添加缺失科室
INSERT INTO departments (id, name, category, description, location, phone, sort_order, enabled) 
SELECT 'dpt-006', '口腔科', '其他', '诊治口腔、牙齿、牙龈及相关疾病的专科。', '门诊大楼一层D区', '010-12345676', 6, 1
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 'dpt-006' OR name = '口腔科');

INSERT INTO departments (id, name, category, description, location, phone, sort_order, enabled) 
SELECT 'dpt-007', '妇科', '其他', '为女性提供妇科疾病诊断、治疗和预防保健服务。', '门诊大楼二层C区', '010-12345677', 7, 1
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 'dpt-007' OR name = '妇科');

-- 3. 备份现有数据
CREATE TABLE IF NOT EXISTS examination_items_backup AS SELECT * FROM examination_items;
CREATE TABLE IF NOT EXISTS examination_item_departments_backup AS SELECT * FROM examination_item_departments;

-- 4. 清空并更新数据
DELETE FROM examination_item_departments WHERE 1=1;
DELETE FROM examination_items WHERE 1=1;

INSERT INTO examination_items (id, name, description, price, category, department_id, enabled, created_at) VALUES
('item-001', '年度体检', 'CDC建议每年进行一次常规体检，及早发现健康问题。', 0.00, '常规检查', NULL, 1, NOW()),
('item-002', '皮肤检查', '根据美国癌症协会的建议，预防和早期发现是抗击癌症的第一步。', 0.00, '专科检查', NULL, 1, NOW()),
('item-003', '牙齿清洁', '美国牙科协会建议定期清洁以预防牙龈疾病，并降低潜在致命的心脏病和中风风险。', 0.00, '专科检查', NULL, 1, NOW()),
('item-004', '眼科检查', '美国眼科学会建议佩戴隐形眼镜的患者每年进行视力筛查。所有成年人都需要定期进行视力筛查。', 0.00, '专科检查', NULL, 1, NOW()),
('item-005', '年度妇科检查', '美国妇产科学会建议即使不需要进行宫颈癌筛查，也应每年看一次妇科医生进行年度妇科检查。此检查通常包括盆腔检查（有或没有巴氏涂片）。', 0.00, '专科检查', NULL, 1, NOW());

INSERT INTO examination_item_departments (id, examination_item_id, department_id, created_at) VALUES
(UUID(), 'item-001', 'dpt-001', NOW()),
(UUID(), 'item-001', 'dpt-003', NOW()),
(UUID(), 'item-002', 'dpt-003', NOW()),
(UUID(), 'item-003', 'dpt-006', NOW()),
(UUID(), 'item-004', 'dpt-005', NOW()),
(UUID(), 'item-005', 'dpt-007', NOW());

-- 5. 验证
SELECT 
    ei.id,
    ei.name as 检查项目,
    GROUP_CONCAT(d.name ORDER BY d.name) as 关联科室
FROM examination_items ei
LEFT JOIN examination_item_departments eid ON ei.id = eid.examination_item_id
LEFT JOIN departments d ON eid.department_id = d.id
WHERE ei.enabled = 1
GROUP BY ei.id, ei.name
ORDER BY ei.id;
```

---

执行完成后，重启后端服务，新的功能就可以使用了！

