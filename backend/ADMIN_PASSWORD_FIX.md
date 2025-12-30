# 管理员密码问题解决方案

## 🔐 管理员账户信息

根据数据库初始化SQL文件：

- **手机号**: `13800000001`
- **密码**: `password123`
- **密码哈希**: `$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q`

---

## ❗ 可能的问题

### 1. 密码哈希不匹配

BCrypt 密码哈希可能在数据库中不正确。

### 2. 数据库中没有管理员数据

可能 SQL 数据没有正确导入。

### 3. 密码输入错误

确保输入的是 `password123`（注意大小写）。

---

## ✅ 解决方案

### 方案1：重新导入SQL数据（推荐）

1. **确认SQL文件已执行**

检查数据库中是否有管理员记录：

```sql
SELECT * FROM users WHERE phone = '13800000001';
SELECT * FROM admins WHERE user_id = 'usr-adm-001';
```

2. **如果没有记录，执行以下SQL**

```sql
-- 插入管理员用户
INSERT INTO `users` (`id`, `phone`, `password_hash`, `role`, `status`) VALUES 
('usr-adm-001', '13800000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'ADMIN', 'ACTIVE');

-- 插入管理员信息
INSERT INTO `admins` (`id`, `user_id`, `name`, `employee_id`) VALUES 
('adm-001', 'usr-adm-001', '王管理', 'A0001');
```

### 方案2：更新密码哈希（如果数据存在但密码不对）

```sql
-- 更新密码为 password123
UPDATE `users` 
SET `password_hash` = '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q' 
WHERE `phone` = '13800000001';
```

### 方案3：使用简单密码（测试用）

如果上面的哈希还是不行，可以使用这个新生成的哈希：

```sql
-- 密码：admin123
UPDATE `users` 
SET `password_hash` = '$2a$10$N.zmdr9k7uOCQb0bta/OO.o9OoQ0H0hHIFKLLYLALQSHMxAXLGEgi' 
WHERE `phone` = '13800000001';
```

然后使用：
- 手机号：`13800000001`
- 密码：`admin123`

### 方案4：重置密码为最简单的（最后手段）

```sql
-- 密码：123456
UPDATE `users` 
SET `password_hash` = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG' 
WHERE `phone` = '13800000001';
```

然后使用：
- 手机号：`13800000001`
- 密码：`123456`

---

## 🔍 调试步骤

### 1. 检查数据库连接

确保后端能正常连接数据库。

### 2. 检查用户记录

在数据库中执行：

```sql
SELECT 
    u.id, 
    u.phone, 
    u.role, 
    u.status,
    u.password_hash,
    a.name,
    a.employee_id
FROM users u
LEFT JOIN admins a ON a.user_id = u.id
WHERE u.phone = '13800000001';
```

应该返回：
- id: usr-adm-001
- phone: 13800000001
- role: ADMIN
- status: ACTIVE
- password_hash: $2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q
- name: 王管理
- employee_id: A0001

### 3. 检查后端日志

登录失败时，查看后端控制台的错误信息。

### 4. 测试其他账户

尝试使用医生或患者账户登录，看是否是所有账户的问题：

**医生账户**：
- 手机号：`13800000002`
- 密码：`password123`

**患者账户**：
- 手机号：`13800000004`
- 密码：`password123`

如果其他账户能登录，说明只是管理员数据的问题。

---

## 🛠️ 快速修复脚本

如果数据库中已经有管理员数据但密码不对，直接执行：

```sql
-- 完整重置管理员账户
DELETE FROM admins WHERE user_id = 'usr-adm-001';
DELETE FROM users WHERE id = 'usr-adm-001';

-- 重新插入
INSERT INTO `users` (`id`, `phone`, `password_hash`, `role`, `status`, `created_at`, `updated_at`) VALUES 
('usr-adm-001', '13800000001', '$2a$10$f/3Jc.M29s29x2/oiMD.S.V731wV1b.H/d8H9d7E2dG0f0jG3jS.q', 'ADMIN', 'ACTIVE', NOW(), NOW());

INSERT INTO `admins` (`id`, `user_id`, `name`, `employee_id`, `created_at`, `updated_at`) VALUES 
('adm-001', 'usr-adm-001', '王管理', 'A0001', NOW(), NOW());
```

---

## 📱 登录测试

修复后，使用以下信息登录：

1. 访问：`http://localhost:3000/login`
2. 输入：
   - 手机号：`13800000001`
   - 密码：`password123`（或你设置的新密码）
3. 点击登录

如果登录成功，应该会跳转到 `/admin/home`。

---

## 💡 其他可能的问题

### JWT 密钥问题

如果登录成功但后续请求失败，可能是 JWT 密钥配置问题。

检查 `application.yml`：

```yaml
app:
  security:
    jwt-secret: your-secret-key-change-this-in-production
    jwt-expiration: 86400000
    jwt-refresh-expiration: 604800000
```

确保 `jwt-secret` 配置正确。

### Redis 连接问题

检查 Redis 是否正常运行：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

如果 Redis 没有运行，可能导致验证码等功能失败。

---

## 📞 还是不行？

如果以上方法都不行，请提供：

1. 浏览器控制台的错误信息（Network标签）
2. 后端控制台的日志
3. 数据库中管理员记录的截图

这样我可以更精确地定位问题。

---

**最可能的原因**：数据库中没有正确导入管理员数据，或者密码哈希不匹配。  
**最快的解决方法**：执行"快速修复脚本"重新创建管理员账户。

