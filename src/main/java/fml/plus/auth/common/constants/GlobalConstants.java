package fml.plus.auth.common.constants;

import java.util.UUID;

public class GlobalConstants {
    private GlobalConstants() {
        throw new IllegalStateException("Constants class");
    }

    /**
     * 全局默认ID
     */
    public static final UUID DEFAULT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    /**
     * 菜单显示ID
     */
    public static final UUID MENU_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");


    public static class Sign {
        private Sign() {
            throw new IllegalStateException("Constants class");
        }

        /**
         * Sign Header Name
         */
        public static final String SIGN_HEADER = "X-Access-Sign";

        /**
         * Version Header Name
         */
        public static final String VERSION_HEADER = "X-Access-Version";

        /**
         * Timestamp Header Name
         */
        public static final String TIMESTAMP_HEADER = "X-Access-Timestamp";

        /**
         * Source Header Name
         */
        public static final String SOURCE_HEADER = "X-Access-Source";

        /**
         * Nonce Header Name
         */
        public static final String NONCE_HEADER = "X-Access-Nonce";

        /**
         * 签名有效期(分钟)
         */
        public static final int EXPIRE_MINUTES = 30;
    }

    /**
     * Token相关全局变量
     */
    public static class Token {
        private Token() {
            throw new IllegalStateException("Constants class");
        }

        /**
         * Token Header Name
         */
        public static final String HEADER = "X-Access-Token";

        /**
         * Token Expire Days
         */
        public static final int EXPIRE_DAYS = 7;

        /**
         * token公钥信息
         */
        public static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEjs63K6Uvv8Madrw7u8jGQzFQMgIkE" +
                "2D29KJdV1vyClkiel3lThQ7OXR3aJ+wg02fubGPtdD/iowlIVnoR2A1aw==";
        /**
         * token私钥信息
         */
        public static final String PRIVATE_KEY = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgEndwU2zOpofNxmQ2" +
                "rVZIHfYn9MuQ22ogrNaJSRTTbgugCgYIKoEcz1UBgi2hRANCAASOzrcrpS+/wxp2vDu7yMZDMVAyAiQTYPb0ol1XW/IKWSJ6XeVOFDs" +
                "5dHdon7CDTZ+5sY+10P+KjCUhWehHYDVr";
    }

    /**
     * 密码相关全局变量
     */
    public static class Password {
        private Password() {
            throw new IllegalStateException("Constants class");
        }

        /**
         * 密码公钥信息
         */
        public static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEZsmB7ggifFRAXzUp9oTfxTgeolNZ19h4z+m" +
                "LKlu0y4UNnZt44Lhem8qjob8B3Jmt1J2tcGpAAXzH3PMoTxnWFw==";
        /**
         * 密码私钥信息
         */
        public static final String PRIVATE_KEY = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgoVQEk3EfSqLMj5j6CB3yeO" +
                "xtgVHuuZoyRevxD0IB4DmgCgYIKoEcz1UBgi2hRANCAARmyYHuCCJ8VEBfNSn2hN/FOB6iU1nX2HjP6YsqW7TLhQ2dm3jguF6byqOhv" +
                "wHcma3Una1wakABfMfc8yhPGdYX";
    }

}
