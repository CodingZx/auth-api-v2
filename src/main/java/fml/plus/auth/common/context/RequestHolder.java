package fml.plus.auth.common.context;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

public class RequestHolder {
    private static final ThreadLocal<Holder> vo = ThreadLocal.withInitial(Holder::new);

    public static Holder get() {
        return vo.get();
    }

    public static void clear() {
        vo.remove();
    }

    @Data
    public static class Holder {
        /**
         * 当前请求
         */
        private HttpServletRequest request;
    }
}
