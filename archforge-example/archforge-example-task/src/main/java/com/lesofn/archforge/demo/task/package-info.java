@NullMarked
// 阶段 1：example-task 已切断对 infrastructure 的反向依赖（当前用户能力经 api/port/CurrentUserPort 声明，
// 由 server 层装配），白名单中不再包含 infrastructure。
@ApplicationModule(id = "example-task", allowedDependencies = "common")
package com.lesofn.archforge.demo.task;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
