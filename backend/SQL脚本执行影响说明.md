# SQL脚本执行影响说明

## ⚠️ 重要提示

**执行SQL脚本会修改数据库数据！** 请务必在执行前备份数据库。

## 脚本执行顺序及影响

### 1. 创建关联表脚本 (`02_create_examination_item_departments.sql`)

**影响：** ✅ **安全** - 只创建新表，不删除或修改现有数据

**执行后：**
- 新增表：`examination_item_departments`
- 现有数据：**不受影响**
- 可以随时执行，不会破坏现有数据

---

### 2. 添加缺失科室脚本 (`04_add_missing_departments.sql`)

**影响：** ✅ **安全** - 只添加新数据，不删除或修改现有数据

**执行后：**
- 新增科室：
  - `dpt-006`: 口腔科
  - `dpt-007`: 妇科
- 现有数据：**不受影响**
- 使用 `WHERE NOT EXISTS` 确保不会重复插入

**可以安全执行多次，不会产生重复数据**

---

### 3. 更新检查项目数据脚本 (`03_update_examination_items_data.sql`)

**影响：** ⚠️ **会删除和修改数据！**

#### 执行后会发生的改变：

##### 步骤1：删除关联关系
```sql
DELETE FROM examination_item_departments WHERE 1=1;
```
- **影响：** 清空 `examination_item_departments` 表的所有数据
- **风险：** 如果之前有手动添加的关联关系，会被删除

##### 步骤2：删除旧的检查项目
```sql
DELETE FROM examination_items WHERE 1=1;
```
- **影响：** 清空 `examination_items` 表的所有数据
- **风险：** ⚠️ **所有现有的检查项目数据都会被删除！**
  - 包括：exam-001（血常规）、exam-002（尿常规）等所有数据
  - 如果已有预约关联了这些检查项目，可能会出现问题

##### 步骤3：插入新的健康检查推荐项目
```sql
INSERT INTO examination_items ...
```
- **影响：** 插入5个新的健康检查推荐项目
  - item-001: 年度体检
  - item-002: 皮肤检查
  - item-003: 牙齿清洁
  - item-004: 眼科检查
  - item-005: 年度妇科检查

##### 步骤4：建立关联关系
```sql
INSERT INTO examination_item_departments ...
```
- **影响：** 建立检查项目与科室的关联关系

---

## 📊 执行前后数据对比

### 执行前（假设有初始数据）

**examination_items 表：**
| id | name | category | department_id |
|---|---|---|---|
| exam-001 | 血常规 | 血液检查 | NULL |
| exam-002 | 尿常规 | 尿液检查 | NULL |
| exam-003 | 肝功能检查 | 生化检查 | NULL |
| ... | ... | ... | ... |

**examination_item_departments 表：**
- 可能为空（如果关联表刚创建）

### 执行后

**examination_items 表：**
| id | name | category | department_id |
|---|---|---|---|
| item-001 | 年度体检 | 常规检查 | NULL |
| item-002 | 皮肤检查 | 专科检查 | NULL |
| item-003 | 牙齿清洁 | 专科检查 | NULL |
| item-004 | 眼科检查 | 专科检查 | NULL |
| item-005 | 年度妇科检查 | 专科检查 | NULL |

**examination_item_departments 表：**
| id | examination_item_id | department_id |
|---|---|---|
| uuid-1 | item-001 | dpt-001 |
| uuid-2 | item-001 | dpt-003 |
| uuid-3 | item-002 | dpt-003 |
| uuid-4 | item-003 | dpt-006 |
| uuid-5 | item-004 | dpt-005 |
| uuid-6 | item-005 | dpt-007 |

---

## 🔍 检查现有数据

在执行脚本前，建议先检查：

### 1. 检查现有检查项目
```sql
SELECT COUNT(*) as total_items FROM examination_items;
SELECT * FROM examination_items;
```

### 2. 检查是否有预约关联了检查项目
```sql
SELECT COUNT(*) as appointments_with_items 
FROM appointments 
WHERE medical_item_id IS NOT NULL;

SELECT a.id, a.appointment_no, a.medical_item_id, ei.name as item_name
FROM appointments a
LEFT JOIN examination_items ei ON a.medical_item_id = ei.id
WHERE a.medical_item_id IS NOT NULL;
```

### 3. 检查关联关系
```sql
SELECT COUNT(*) as total_relations 
FROM examination_item_departments;
```

---

## 📋 安全执行步骤

### 方案1：完全替换（推荐用于新系统或测试环境）

1. **备份数据库**
   ```sql
   -- 备份检查项目表
   CREATE TABLE examination_items_backup AS SELECT * FROM examination_items;
   
   -- 备份关联表（如果存在）
   CREATE TABLE examination_item_departments_backup AS SELECT * FROM examination_item_departments;
   
   -- 备份预约表（如果有关联）
   CREATE TABLE appointments_backup AS SELECT * FROM appointments;
   ```

2. **执行脚本**
   ```sql
   -- 1. 创建关联表
   -- 执行：02_create_examination_item_departments.sql
   
   -- 2. 添加缺失科室
   -- 执行：04_add_missing_departments.sql
   
   -- 3. 更新检查项目数据
   -- 执行：03_update_examination_items_data.sql
   ```

3. **验证数据**
   ```sql
   -- 查看新的检查项目
   SELECT * FROM examination_items;
   
   -- 查看关联关系
   SELECT 
       ei.name as 检查项目,
       GROUP_CONCAT(d.name) as 关联科室
   FROM examination_items ei
   LEFT JOIN examination_item_departments eid ON ei.id = eid.examination_item_id
   LEFT JOIN departments d ON eid.department_id = d.id
   GROUP BY ei.id, ei.name;
   ```

### 方案2：保留现有数据（如果已有重要数据）

如果您的系统已经在使用，并且有重要的检查项目数据，建议：

1. **手动迁移数据**
   - 不要执行 `03_update_examination_items_data.sql` 中的 DELETE 语句
   - 手动插入新的健康检查推荐项目（使用新的ID，如 item-001）
   - 手动建立关联关系

2. **或者创建新的表**
   ```sql
   -- 创建新的表用于健康检查推荐项目
   CREATE TABLE health_check_items LIKE examination_items;
   CREATE TABLE health_check_item_departments LIKE examination_item_departments;
   ```

---

## ⚠️ 注意事项

1. **预约数据影响**
   - 如果已有预约使用了旧的检查项目ID（如 exam-001），执行脚本后这些预约的 `medical_item_id` 会指向不存在的记录
   - 建议先处理这些预约数据

2. **外键约束**
   - `appointments` 表的 `medical_item_id` 有外键约束
   - 删除检查项目前，需要先处理关联的预约数据

3. **数据恢复**
   - 如果执行后发现问题，可以从备份表恢复：
   ```sql
   DELETE FROM examination_items;
   INSERT INTO examination_items SELECT * FROM examination_items_backup;
   ```

---

## ✅ 执行后验证

执行完所有脚本后，运行以下查询验证：

```sql
-- 1. 检查项目数量（应该是5个）
SELECT COUNT(*) FROM examination_items WHERE enabled = 1;
-- 预期结果：5

-- 2. 检查关联关系数量（应该是6个）
SELECT COUNT(*) FROM examination_item_departments;
-- 预期结果：6

-- 3. 检查科室数量（应该是7个）
SELECT COUNT(*) FROM departments WHERE enabled = 1;
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

---

## 📝 总结

| 脚本 | 影响 | 风险等级 | 可逆性 |
|------|------|---------|--------|
| `02_create_examination_item_departments.sql` | 创建新表 | ✅ 低 | 可删除表 |
| `04_add_missing_departments.sql` | 添加科室 | ✅ 低 | 可删除记录 |
| `03_update_examination_items_data.sql` | **删除并重建数据** | ⚠️ **高** | **需要备份恢复** |

**建议：** 在生产环境执行前，务必在测试环境先验证！

