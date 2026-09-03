package com.lesofn.archforge.infrastructure.auth.provider;

import com.lesofn.archforge.user.api.port.UserProvider;
import org.springframework.stereotype.Service;

/**
 * 用户认证端口的兜底实现：未接入真实用户模块时，认证一律失败。
 *
 * @author sofn
 * @version 1.0 Created at: 2017-07-26 14:54
 */
@Service
public class DefaultUserProvider implements UserProvider {

    @Override
    public boolean isValidUser(long uid) {
        return false;
    }

    @Override
    public long authUser(String loginName, String password) {
        return 0;
    }
}
