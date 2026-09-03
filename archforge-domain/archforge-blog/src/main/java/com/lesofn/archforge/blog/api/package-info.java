/**
 * Public contract of the {@code blog} module.
 *
 * <p>
 * Declared as a named interface so that the {@code CLOSED} module {@code blog} keeps
 * everything under {@code blog.api} reachable for {@code server-admin} and
 * {@code server-web}, while {@code blog.internal} stays module-private.
 */
@NullMarked
@NamedInterface("api")
package com.lesofn.archforge.blog.api;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.NamedInterface;
