package fml.plus.auth.common.util;

import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtils {
    private IPUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * IP地址转Long
     * @param ipAddress ip地址
     * @return 返回的数字
     */
    public static long ipToLong(String ipAddress) {
        if (Strings.isNullOrEmpty(ipAddress)) {
            return 0;
        }
        if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
            return 0;
        }
        // ipAddressInArray[0] = 192
        String[] ipAddressInArray = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {
            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);

            // 1. 192 * 256^3
            // 2. 168 * 256^2
            // 3. 1 * 256^1
            // 4. 2 * 256^0
            result += ip * Math.pow(256, power);
        }
        return result;
    }

    /**
     * 数字转回对应IP地址
     * @param i 数字
     * @return IP地址
     */
    public static String longToIp(long i) {
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
    }

    /**
     * 获得真实IP地址
     * @param request Http请求
     * @return 真实IP地址
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 获得本机IP地址
     * @return 本机IP地址
     */
    public static String getLocalIP() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
