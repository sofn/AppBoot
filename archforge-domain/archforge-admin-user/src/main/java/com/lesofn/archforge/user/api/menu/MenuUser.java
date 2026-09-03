package com.lesofn.archforge.user.api.menu;

/**
 * 构建菜单路由所需的最小用户上下文。
 *
 * <p>
 * 领域层只需要"用户 ID + 是否超管"两项信息即可决定可见菜单，
 * 因此这里用窄契约替代 infrastructure 的 {@code SystemLoginUser}，
 * 避免领域模块反向依赖基础设施的认证模型。由 server 层负责装配。
 *
 * @param userId 用户 ID
 * @param admin 是否为超级管理员（超管可见全部菜单）
 */
public record MenuUser(Long userId, boolean admin) {
}
