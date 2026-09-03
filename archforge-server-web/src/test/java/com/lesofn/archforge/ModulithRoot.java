package com.lesofn.archforge;

import org.springframework.modulith.Modulithic;

/**
 * Dedicated Modulith root for {@code server-web} architecture tests.
 *
 * <p>
 * Keeps the module system root at {@code com.lesofn.archforge} while the Spring Boot
 * application entry point lives in {@code com.lesofn.archforge.server.web}. The
 * {@code server-web} test classpath does not contain {@code server-admin}, so the
 * verified module set is: {@code common}, {@code infrastructure}, {@code blog},
 * {@code admin-user} and {@code server-web}.
 */
@Modulithic(systemName = "ArchForgeWeb", sharedModules = "common")
public class ModulithRoot {
}
