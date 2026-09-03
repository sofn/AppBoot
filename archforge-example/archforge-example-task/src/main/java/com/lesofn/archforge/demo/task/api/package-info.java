/**
 * Public contract of the {@code example-task} module.
 *
 * <p>
 * Declared as a named interface so that the (default {@code CLOSED}) module {@code example-task}
 * keeps everything under {@code demo.task.api} reachable for {@code server-admin}, while
 * {@code demo.task.internal} stays module-private.
 */
@NullMarked
@NamedInterface("api")
package com.lesofn.archforge.demo.task.api;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.NamedInterface;
