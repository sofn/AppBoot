package com.lesofn.appboot.infrastructure.auth.spi;

import com.lesofn.appboot.infrastructure.auth.model.AuthRequest;
import com.lesofn.appboot.infrastructure.frame.utils.log.ApiLogger;

/**
 * @author sofn
 */
public abstract class AbstractAuthSpi implements AuthSpi {

    @Override
    public boolean canAuth(AuthRequest request) {
        try {
            boolean result = this.checkCanAuth(request);
            if (ApiLogger.isTraceEnabled()) {
                ApiLogger.trace(this.getName() + " can auth:" + result);
            }
            return result;
        } catch (Exception e) {
            ApiLogger.error("check can auth error:", e);
            return false;
        }
    }

    protected abstract boolean checkCanAuth(AuthRequest request);

    @Override
    public void afterAuth(long uid, AuthRequest request) {
        // default do nothing
    }
}
