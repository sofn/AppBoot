package com.lesofn.archforge.server.admin.adapter;

import com.lesofn.archforge.demo.task.api.port.CurrentUserPort;
import com.lesofn.archforge.infrastructure.frame.context.RequestContext;
import com.lesofn.archforge.infrastructure.frame.context.ScopedValueContext;
import org.springframework.stereotype.Component;

/**
 * {@link CurrentUserPort} 的装配实现：从基础设施的请求上下文取当前登录用户。
 *
 * <p>
 * 领域模块声明端口、server 层装配基础设施能力，是阶段 1「切断反向依赖」的标准装配位置。
 */
@Component
public class CurrentUserPortAdapter implements CurrentUserPort {

    @Override
    public long getCurrentUid() {
        if (!ScopedValueContext.isBound()) {
            return 0L;
        }
        RequestContext context = ScopedValueContext.getRequestContext();
        return context != null ? context.getCurrentUid() : 0L;
    }
}
