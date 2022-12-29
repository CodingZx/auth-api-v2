package fml.plus.auth.common.util;

import com.google.common.base.Verify;
import org.apache.commons.lang3.RandomUtils;

public class CodeUtils {
    private CodeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getCode(int length) {
        Verify.verify(length > 0, "length is too short!!");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RandomUtils.nextInt(0, 10));
        }
        return sb.toString();
    }
}
