/**
 * blog 模块的领域层。
 *
 * <p>
 * 模块默认为 CLOSED，因此对外可见性由命名接口 {@code domain} 显式声明：
 * 聚合根与值对象需要被 server 层使用，其余（repository / query）保持模块私有。
 */
@NullMarked
@NamedInterface("domain")
package com.lesofn.archforge.blog.domain;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.NamedInterface;
