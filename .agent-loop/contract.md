# Agent Loop Contract: DDD 结构改造（合并 admin-user 模块 + 基础设施）

## Goal
按 DDD 思想完成 `ArchForge` 结构改造：合并 `domain/admin-user-api` 与 `domain/admin-user`，建立 `common-jpa` 的 `BasePO`/`BaseDomainEntity`，搭建 `admin-user` 领域包骨架，配置 MapStruct 与 ArchUnit，并验证 `./gradlew build` 与 `./gradlew server-admin:bootRun` 通过。

## In Scope
- `settings.gradle.kts`、`domain/admin-user/build.gradle.kts`、`server-admin/build.gradle.kts` 依赖调整。
- `domain/admin-user-api` 源码与测试文件迁移到 `domain/admin-user`。
- Spring Modulith 模块声明合并为单一 `admin-user` 根包。
- `common/common-jpa` 新增 `BasePO`（JPA 持久化基类）与 `BaseDomainEntity`（领域基类）。
- `dependencies/build.gradle.kts` 增加 ArchUnit 约束；`server-admin` 增加测试依赖并创建占位测试类。
- `domain/admin-user/src/main/java/com/lesofn/archforge/user/domain/**` DDD 包骨架。
- `./gradlew build` 与 `./gradlew server-admin:bootRun`（dev 环境 + 指定 env）验证。

## Out of Scope
- 不迁移现有 `user.api.domain` 实体到新 `user.domain` 包（本次仅搭骨架）。
- 不修改业务逻辑、`server-admin` controller/service 行为。
- 不改动前端、example-task、Flyway 脚本。

## Definition of Done
- `domain/admin-user-api` 目录不存在；`settings.gradle.kts` 不再引用它。
- `domain/admin-user` 同时包含 `user/api`、`user/internal`、`user/domain`。
- `common/common-jpa` 新增两个基类并编译通过。
- `./gradlew build` 通过（spotlessCheck + test）。
- `./gradlew server-admin:bootRun` 在指定环境变量下无 ERROR 启动。

## Acceptance Criteria
| # | 验收项 | 验证方式 |
|---|--------|----------|
| 1 | `domain/admin-user-api` 已合并进 `domain/admin-user` | 目录与 `settings.gradle.kts` 检查 |
| 2 | `server-admin` 仍能编译并启动 | `./gradlew :server-admin:compileJava` |
| 3 | `BasePO` / `BaseDomainEntity` 存在于 `common-jpa` 并编译 | `./gradlew :common:common-jpa:compileJava` |
| 4 | `domain/admin-user` 包含领域包骨架 | 目录树 |
| 5 | ArchUnit 依赖与占位测试类已添加 | `./gradlew :server-admin:compileTestJava` |
| 6 | 全量构建通过 | `./gradlew build` |
| 7 | dev 启动成功 | `server-admin:bootRun` 日志无 ERROR |

## Validation Commands
```bash
./gradlew :domain:admin-user:compileJava
./gradlew :common:common-jpa:compileJava
./gradlew :server-admin:compileTestJava
./gradlew build
# 带环境变量启动
export DB_TASK_MASTER_URL=jdbc:postgresql://lesofn.com:22000/archforge_task
export DB_TASK_SLAVE_URL=jdbc:postgresql://lesofn.com:22000/archforge_task
export DB_USER_MASTER_URL=jdbc:postgresql://lesofn.com:22000/archforge_user
export DB_USER_SLAVE_URL=jdbc:postgresql://lesofn.com:22000/archforge_user
export REDIS_HOST=lesofn.com
export REDIS_PORT=22001
export S3_ENDPOINT=http://lesofn.com:22002
./gradlew server-admin:bootRun
```

## Risk Assumptions
- `com.lesofn.archforge.user.api.*` 包名保持不变，server-admin import 无需改动。
- ArchUnit 1.3.0 占位测试类不使用 ArchUnit API，避免 Java 25 字节码兼容风险。
- Modulith 模块声明合并后需删除子包 `@ApplicationModule` 注解，避免同一 jar 内多模块冲突。

## Stop Conditions
- `./gradlew build` 连续两次失败且无法修复。
- 同一验收项失败两次。
- 用户明确要求调整范围。

## Final Deliverables
- 修改后的 `ArchForge` 代码。
- 更新的 `codeplans/ArchForge/2026-08-01-ddd-admin-user-refactor.md` 计划文件。
- `.agent-loop/progress.md` 与 `log.md`。
- `build` 与 `bootRun` 验证日志。
