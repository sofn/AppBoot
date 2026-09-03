/**
 * {@code server-web} application module: the public web site assembly layer.
 *
 * <p>
 * Declared explicitly so that {@code server-web} is covered by Modulith verification
 * (see {@code ModulithIntegrationTest}). Kept {@code OPEN} for now — tightening it to
 * {@code CLOSED} is tracked alongside the other modules.
 */
@NullMarked
@ApplicationModule(id = "server-web", type = ApplicationModule.Type.OPEN, allowedDependencies = {
        "common", "infrastructure", "blog :: api", "blog :: domain", "admin-user"
})
package com.lesofn.archforge.server.web;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
