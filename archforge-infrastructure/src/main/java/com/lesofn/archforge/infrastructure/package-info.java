@NullMarked
// 依赖倒置：infrastructure 实现 admin-user 声明的领域端口（EnumDictionaryPort / UserProvider），
// 因此 allowedDependencies 需显式放行 admin-user；不允许依赖任何 server-* 模块。
@ApplicationModule(
        id = "infrastructure",
        type = ApplicationModule.Type.OPEN,
        allowedDependencies = {
                "common", "admin-user"
        })
package com.lesofn.archforge.infrastructure;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
