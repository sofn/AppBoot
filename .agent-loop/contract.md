# Agent Loop Contract: 完善 example-task + SysUser 富领域重构

## Goal
1. 将 `example/example-task` 从简单示例完善为一个功能完整、可前后端联调的 **任务管理示例模块**。
2. 在 `../ArchForgeAdmin` 中实现对应的任务管理前端页面，接入动态路由，并用 Chrome 验证页面可用。
3. 将 `domain/admin-user-api` 中的 `SysUser` 从贫血实体重构为富领域实体，把散落在应用服务中的状态变更逻辑收敛到实体内部。

## In Scope
- 后端 (`example/example-task`)
  - `Task` 实体增加 `createTime/updateTime/deleted` 审计字段（继承 `BaseEntity`），保留并增强状态机。
  - 新增 `TaskStatus` 中文标签与状态转换校验。
  - 新增 DTO：`TaskResponse`、`TaskPageResult`、`TaskListRequest`、`TaskCreateRequest`、`TaskUpdateRequest`、`TaskDeleteRequest`、`TaskActionRequest`。
  - 重写 `TaskController`：统一返回可被 `ResultValueWrapper` 包装的 `TaskPageResult`。
    - `POST /task` 分页列表（支持 title/status 过滤）
    - `POST /task/create`
    - `PUT /task/update`
    - `POST /task/delete`
    - `POST /task/start`
    - `POST /task/complete`
    - `POST /task/cancel`
  - 扩展 `TaskService` 与 `TaskDao`：支持分页、过滤、状态动作、逻辑删除。
  - 更新 `InitDbMockServer` 重置序列包含 `task_id_seq`。
- 后端 (`domain/admin-user-api` 与 `server-admin`)
  - 在 `SysUser` 中新增富领域方法：`createNew`, `updateProfile`, `changePassword`, `recordLogin`, `assignRole`, `assignDept`, `updateStatus`, `activate`, `disable`, `markDeleted`, `validateCanLogin`。
  - 保持现有 `@Getter` 可用，将应用层 (`SysUserServiceImpl`, `UserServiceImpl`) 中对 `SysUser` 的裸 `setXxx` 调用迁移到实体方法。
  - 更新 `AdminUserDetailsService` 使用 `user.canLogin()` / `user.isActive()`。
- 数据种子
  - 向 `db/migration/V3__init_menu_data.sql` 与 `domain/admin-user/src/main/resources/sql/data-admin-user.sql` 添加“任务管理”菜单及按钮权限。
  - 如需要，补充 Flyway 迁移文件（`V8__add_task_menu.sql`）。
- 前端 (`../ArchForgeAdmin`)
  - 新增 `src/api/task.ts`。
  - 新增 `src/views/example/task/index.vue`、`form.vue`、`utils/hook.tsx`、`utils/types.ts`、`utils/rule.ts`。
  - 实现列表搜索、新增/编辑弹窗、删除确认、状态标签、操作按钮（开始/完成/取消）。
  - 使用 `v-perms` 控制按钮显隐。
- 验证
  - `./gradlew build` 通过。
  - `./gradlew server-admin:bootRun` 在 dev 环境正常启动。
  - 启动 `pnpm dev`，用 Chrome（chrome-devtools MCP）登录并访问任务管理页面，截图验证无报错。

## Out of Scope
- 不改动现有用户/角色/菜单的核心业务语义（仅将 `SysUser` 的变更封装收敛）。
- 不引入新的外部依赖。
- 不做移动端适配。
- 不改动 `WebTaskController`  server-side 视图逻辑（保留原样，前端走 SPA 路由）。
- 不修改 CI/CD 配置或生成原生镜像。

## Definition of Done
- `example-task` 提供完整 CRUD + 状态流转 API，响应格式与现有 `AdminPageResult` 风格一致。
- 前端页面能从登录后的动态菜单进入，可完成新增、编辑、删除、状态操作，列表分页正常。
- `SysUser` 不再被应用服务直接大量 `setXxx` 修改，关键行为通过实体方法表达。
- 全量构建无错误、无新增 deprecation warning，后端可正常启动。
- Chrome 截图显示任务管理页面正常渲染、接口返回 code=0。

## Acceptance Criteria
| # | 验收项 | 验证方式 |
|---|--------|----------|
| 1 | `./gradlew build` 通过 | 命令行输出 |
| 2 | `./gradlew server-admin:bootRun` 启动无 ERROR | 日志 |
| 3 | `POST /task/create` 返回新任务 ID，数据库可查到 | curl / Swagger |
| 4 | `POST /task` 返回分页数据，`data.list` 包含 status/createTime | curl |
| 5 | `POST /task/start`/`complete`/`cancel` 正确变更状态且非法转换报错 | curl |
| 6 | 前端 `/example/task/index` 页面加载后表格有数据 | Chrome 截图 |
| 7 | 前端“新增/编辑/删除/开始/完成/取消”交互可用 | Chrome 截图 / 控制台无错 |
| 8 | `SysUserServiceImpl`/`UserServiceImpl` 不再直接调用 `setStatus/setPassword/setDeleted` 等裸 setter 修改用户状态 | 代码审查 |
| 9 | 登录、用户列表、修改用户、重置密码等原有用户功能仍正常 | 手动 / 单元测试 |

## Validation Commands
```bash
# 1. 全量构建（含 spotless + test）
./gradlew build

# 2. 启动 dev 依赖
bash scripts/dev/init.sh
bash scripts/dev/seed.sh

# 3. 启动后端
JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java)))) ./gradlew server-admin:bootRun

# 4. 前端（另开终端）
cd ../ArchForgeAdmin && pnpm dev

# 5. API 快速验证（替换 token）
curl -X POST http://localhost:8080/task -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d '{"currentPage":1,"pageSize":10}'

# 6. Chrome 验证（chrome-devtools MCP）
# 导航到 http://localhost:8848/login，登录后进入 /example/task/index，截图
```

## Subjective Scoring Criteria
| Dimension | 评分点 |
|-----------|--------|
| Functionality | 任务 CRUD 与状态机正确，前端联调可用 |
| Simplicity | 不引入过度抽象，复用现有 `AdminPageResult`/notice/config 模式 |
| Craft | 命名、类型、DTO 映射符合项目风格；Spotless 通过 |
| Originality | `SysUser` 富方法设计合理，而不是简单换个名字封装 setter |

## Risk Assumptions
- Dev 环境 PostgreSQL/Redis/RustFS 容器可用，端口 5432/6379/9000 未被占用。
- 当前 `TaskController` 的 `RequestContext` 参数解析与 `ResultValueWrapper` 行为不变。
- `SysUser` 作为 `admin-user-api` 的 API 实体，修改方法签名会影响 `server-admin` 应用服务；需全量编译验证。

## Stop Conditions
- `./gradlew build` 连续两次无法通过且原因不可修复。
- 发现前端无法连接后端（CORS/代理/token）且 15 分钟内无法定位。
- 用户明确要求缩小/扩大范围。

## Restart Conditions
- 同一验收项失败两次。
- `SysUser` 重构后发现登录/用户管理回归，需要回退重新设计。
- 实现变成“补丁叠补丁”。

## Final Deliverables
- 修改后的 `ArchForge` 代码（`example-task`、`domain/admin-user-api`、`server-admin`、`server-admin/src/main/resources/db/migration`）。
- 新增的 `ArchForgeAdmin` 前端代码（`src/api/task.ts`、`src/views/example/task/**`）。
- 更新的 `codeplans/ArchForge/2026-07-24-task-example-and-sysuser-rich-domain.md` 计划文件。
- 本 `.agent-loop/` 目录下的 `progress.md` 与 `log.md`。
- 构建通过、后端启动、Chrome 截图证据。
