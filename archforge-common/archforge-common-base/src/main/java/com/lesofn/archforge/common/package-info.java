/**
 * Shared {@code common} application module.
 *
 * <p>
 * The root package {@code com.lesofn.archforge.common} is contributed to by TWO Gradle
 * modules: {@code archforge-common-base} (this one) and {@code archforge-common-jpa}.
 * Spring Modulith identifies a module by its base package, so the two Gradle modules
 * inevitably collapse into ONE Modulith module, and the two {@code package-info.class}
 * files shadow each other on the classpath.
 *
 * <p>
 * <strong>Invariant (do not break):</strong> the {@code @ApplicationModule} declaration
 * below and the one in {@code archforge-common-jpa}'s {@code package-info.java} must
 * stay byte-identical. Whichever jar wins the classpath race defines the module; if
 * only one of them declares it, the module can silently disappear (observed: Modulith
 * then detects 6 modules instead of 7 and {@code sharedModules = "common"} fails with
 * "Module common does not exist!").
 *
 * <p>
 * Splitting the two into distinct Modulith modules ({@code common-base} /
 * {@code common-jpa}) requires first moving {@code archforge-common-jpa}'s packages
 * ({@code persistence}, {@code repository}, {@code annotation}, {@code domain},
 * {@code utils.query}) under a distinct root — tracked for a later phase.
 */
@NullMarked
@ApplicationModule(id = "common", type = ApplicationModule.Type.OPEN, allowedDependencies = {})
package com.lesofn.archforge.common;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
