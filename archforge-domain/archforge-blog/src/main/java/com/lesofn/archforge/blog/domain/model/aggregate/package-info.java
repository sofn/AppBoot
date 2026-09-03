/**
 * 文章聚合根所在包，属于 {@code domain} 命名接口。
 *
 * <p>
 * 这里的类受 ArchUnit 规则 4 守护：聚合根不得暴露 public setter，
 * 状态变更必须走业务方法。
 */
@NullMarked
@NamedInterface("domain")
package com.lesofn.archforge.blog.domain.model.aggregate;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.NamedInterface;
