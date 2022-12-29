package fml.plus.auth.common.redisson.lock;

import fml.plus.auth.common.context.UserThreadInfo;

import java.util.Optional;
import java.util.UUID;

public class LockCurrentUserSupport implements ILockCurrentUser {

    public String getCurrentUserId() {
        return Optional.ofNullable(UserThreadInfo.get().getUserId()).map(UUID::toString).orElse(null);
    }
}
