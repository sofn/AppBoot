/**
 * blog 模块的基础设施层：模块私有，不对外暴露。
 *
 * <p>
 * 负责 JPA 实体、DAO、仓储实现与持久化配置，实现 {@code blog.domain.repository}
 * 声明的领域仓储接口（依赖倒置在模块内部落地）。
 */
@NullMarked
package com.lesofn.archforge.blog.infrastructure;

import org.jspecify.annotations.NullMarked;
