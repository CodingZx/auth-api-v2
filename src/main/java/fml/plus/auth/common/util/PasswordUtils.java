package fml.plus.auth.common.util;

import fml.plus.auth.common.constants.GlobalConstants;
import lombok.Data;

import java.util.Objects;

public class PasswordUtils {

    private PasswordUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 生成含有随机盐的密码
     */
    public static String generate(String password) {
        if (password == null) throw new NullPointerException("Password不能为空.");
        var pwdObj = new Password(password, CodeUtils.getCode(8));
        return SMUtils.ecrypt(GsonUtils.gson().toJson(pwdObj), GlobalConstants.Password.PUBLIC_KEY);
    }

    /**
     * 校验密码是否正确
     */
    public static boolean verify(String password, String md5) {
        var decrypt = SMUtils.decrypt(md5, GlobalConstants.Password.PRIVATE_KEY);
        try {
            var pwd = GsonUtils.gson().fromJson(decrypt, Password.class);
            return pwd.verify(password);
        } catch (Exception e) {
            return false;
        }
    }


    @Data
    private static class Password {
        private String origin;
        private String salt;

        public Password(String origin, String salt) {
            this.origin = origin;
            this.salt = salt;
        }

        public boolean verify(String password) {
            return Objects.equals(origin, password);
        }
    }
}
