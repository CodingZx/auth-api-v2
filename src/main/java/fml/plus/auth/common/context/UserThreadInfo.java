package fml.plus.auth.common.context;

import fml.plus.auth.common.constants.GlobalConstants;
import lombok.Data;

import java.util.UUID;

public class UserThreadInfo {
    private static final ThreadLocal<VO> vo = ThreadLocal.withInitial(VO::new);

    public static VO get() {
        return vo.get();
    }

    public static void clear() {
        vo.remove();
    }

    @Data
    public static class VO {
        /**
         * 用户ID
         */
        private UUID userId;

        /**
         * 角色ID
         */
        private UUID roleId;

        /**
         * 是否为超级管理员
         */
        public boolean isSuperAdmin() {
            return GlobalConstants.DEFAULT_ID.equals(roleId);
        }
    }
}
