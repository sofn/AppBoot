package com.lesofn.archforge.user.api.port;

/**
 * 用户认证端口：领域模块向基础设施认证流程提供的用户查询能力。
 *
 * <p>
 * 此前该接口定义在 infrastructure 中、由领域模块实现，属于依赖倒置方向写反。
 * 现改为由领域声明端口、基础设施仅依赖接口（实现由 server 层装配）。
 *
 * <p>
 * 原接口的 {@code checkCanAccess} 方法全仓无任何调用点，属死代码，此处不再保留。
 */
public interface UserProvider {

    /**
     * 是否为有效用户。
     *
     * @param uid 用户 ID
     * @return 有效返回 true
     */
    boolean isValidUser(long uid);

    /**
     * 通过用户名密码认证用户。
     *
     * @param loginName 登录名，不能为空
     * @param password 密码，不能为空
     * @return 用户 ID，认证失败返回 0
     */
    long authUser(String loginName, String password);
}
