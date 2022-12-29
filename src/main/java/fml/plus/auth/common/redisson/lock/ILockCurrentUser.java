package fml.plus.auth.common.redisson.lock;

public interface ILockCurrentUser {

    /**
     * 获得当前登录用户ID
     */
    String getCurrentUserId();

}
