/**
 * 领域值对象所在包，属于 {@code domain} 命名接口。
 *
 * <p>
 * 这里的类受 ArchUnit 规则 5 守护：值对象必须是 record（不可变）。
 */
@NullMarked
@NamedInterface("domain")
package com.lesofn.archforge.blog.domain.valueobject;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.NamedInterface;
