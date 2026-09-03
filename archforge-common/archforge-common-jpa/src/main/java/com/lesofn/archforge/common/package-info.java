/**
 * JPA support types of the shared {@code common} application module.
 *
 * <p>
 * This Gradle module contributes to the root package {@code com.lesofn.archforge.common},
 * which is also contributed to by {@code archforge-common-base}. Spring Modulith
 * identifies a module by its base package, so both jars carry the same
 * {@code package-info.class} and shadow each other on the classpath.
 *
 * <p>
 * <strong>Invariant (do not break):</strong> the {@code @ApplicationModule} declaration
 * below must stay byte-identical to the one in {@code archforge-common-base}. Removing
 * one of the two makes the {@code common} module disappear from Modulith detection
 * (verified: then only 6 modules are found and {@code sharedModules = "common"} fails).
 * Giving {@code common-jpa} an id of its own is equally unsafe — the winner depends on
 * classpath order.
 *
 * <p>
 * To make {@code common-jpa} a module of its own, move these packages
 * ({@code persistence}, {@code repository}, {@code annotation}, {@code domain},
 * {@code utils.query}) under a distinct root first.
 */
@NullMarked
@ApplicationModule(id = "common", type = ApplicationModule.Type.OPEN, allowedDependencies = {})
package com.lesofn.archforge.common;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
