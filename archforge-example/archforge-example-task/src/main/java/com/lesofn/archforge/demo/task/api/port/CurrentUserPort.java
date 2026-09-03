package com.lesofn.archforge.demo.task.api.port;

/**
 * 当前登录用户端口：领域模块需要的"当前请求用户"能力。
 *
 * <p>
 * 此前领域模块直接引用 infrastructure 的 {@code RequestContext}（经 MVC 参数解析器注入），
 * 属于对基础设施的反向依赖；现改为在此声明端口，由 server 层装配实现。
 */
public interface CurrentUserPort {

    /**
     * 获取当前登录用户 ID。
     *
     * @return 当前登录用户 ID；无登录上下文时返回 0
     */
    long getCurrentUid();
}
