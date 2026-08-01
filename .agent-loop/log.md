# Log

## 2026-08-01 DDD 结构改造

- 需求：合并 admin-user-api + admin-user，新增 BasePO/BaseDomainEntity，配置 MapStruct 与 ArchUnit，验证 build 与 bootRun。
- 合并模块时保持 Java 包名不变，避免修改 server-admin 的 import；通过 `git mv` 迁移源码，删除 `domain/admin-user-api` 目录。
- 合并 `user.api` 与 `user.internal` 的 Spring Modulith 模块声明为单一 `admin-user` 根包。
- 新增 `BasePO` 与 `BaseDomainEntity` 后首次 build 因 Spotless 格式失败，运行 `./gradlew spotlessApply` 修复。
- `./gradlew build` 通过。
- `server-admin:bootRun` 在 dev 环境 + 给定 env 下启动成功，日志无 ERROR，Started Application in 23.471 seconds。
