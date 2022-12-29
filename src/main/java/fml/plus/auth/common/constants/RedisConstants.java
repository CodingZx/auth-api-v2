package fml.plus.auth.common.constants;

import fml.plus.auth.common.util.time.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class RedisConstants {
    private static final String PREFIX = "temp:";
    public static final String NULL_VAL = "null";

    /**
     * 登录用户对应的toke
     */
    public static String getTokenKey(UUID userId) {
        return PREFIX + "token:" + userId;
    }

    public static class LockKeys {
        /**
         * 初始化配置Lock Key
         */
        public static String initConfig(String configKey) {
            return PREFIX + "init:lock:" + configKey;
        }

        /**
         * 上报信息Lock Key
         */
        public static String backupLockKey() {
            return PREFIX + "lock:backup";
        }

        /**
         * 监控清理Lock key
         */
        public static String monitorCleanKey() {
            return PREFIX + "lock:monitor:clean";
        }

        /**
         * 登录限制Lock Key
         */
        public static String ipLimitLockKey(String ip) {
            return PREFIX + "lock:login:limit:" + ip;
        }
    }

    public static class LoginKeys {
        /**
         * IP限制
         */
        public static String ipLimitKey(String ip) {
            return PREFIX + "login:limit:" + ip;
        }

        /**
         * RSA Key
         */
        public static String rsaKey() {
            return PREFIX + "login:rsa";
        }
    }

    public static class MenuKeys {
        /**
         * 当前登录用户的菜单
         */
        public static String getCurrentKey(UUID roleId) {
            return PREFIX + "menu:current:" + roleId;
        }

        /**
         * 当前登录用户的权限
         */
        public static String getCurrentPermissionKey(UUID roleId) {
            return PREFIX + "menu:current:perm:" + roleId;
        }

        /**
         * 全部菜单
         */
        public static String getAllMenus() {
            return PREFIX + "menu:all";
        }
    }

    public static class AdminKeys {
        /**
         * 账号真实姓名
         */
        public static String adminRealNameKey(UUID id) {
            return PREFIX + "admin:name:" + id;
        }
    }

    public static class RoleKeys {
        /**
         * 角色名称
         */
        public static String roleNameKey(UUID id) {
            return PREFIX + "role:name:" + id;
        }
    }

    public static class ConfigKeys {
        /**
         * 配置缓存Key
         */
        public static String getConfigKey(String key) {
            return PREFIX + "config:" + key;
        }
    }

    public static class ExceptionKeys {
        /**
         * 上报异常信息
         */
        public static String getExceptionKey(String md5) {
            return PREFIX + "ex:" + md5;
        }

        /**
         * 上报异常信息记录ID
         */
        public static String getExceptionIdKey(String md5) {
            return PREFIX + "ex:id:" + md5;
        }
    }

    public static class MonitorKeys {
        /**
         * 获得服务器名称Key
         */
        public static String getServersKey() {
            return PREFIX + "monitor:servers";
        }

        /**
         * 获得请求统计Key
         */
        public static String getRequestCounterKey(String server, LocalDateTime time) {
            return PREFIX + "monitor:req:counter:" + server + ":" + LocalDateTimeUtils.format(time, "yyyy-MM-dd_HH-mm");
        }
    }

}
