package fml.plus.auth.common.util;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;

import java.nio.charset.StandardCharsets;

public class HMacUtils {
    private HMacUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * HMac-SHA256数据签名
     */
    public static String sign(String waitSignStr, String signKey) {
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, signKey.getBytes(StandardCharsets.UTF_8));
        return mac.digestHex(waitSignStr, StandardCharsets.UTF_8);
    }
}
