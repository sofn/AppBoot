@NullMarked
// 阶段 1：admin-user 已切断对 infrastructure 的反向依赖（外部能力一律经 api/port/ 声明端口，
// 由 infrastructure 实现或 server 层装配），白名单中不再包含 infrastructure。
@ApplicationModule(id = "admin-user", type = ApplicationModule.Type.OPEN, allowedDependencies = "common")
package com.lesofn.archforge.user;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
