package fml.plus.auth.common.util;

public class SQLUtils {
    private SQLUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 前后模糊查询
     * @param condition 查询条件
     * @return '%' + condition + '%'
     */
    public static String fuzzyAll(String condition) {
        if (condition == null || condition.isBlank()) {
            return null;
        }
        return "%" + condition.trim() + "%";
    }

    /**
     * 前模糊查询
     * @param condition 查询条件
     * @return '%' + condition
     */
    public static String fuzzyLeft(String condition) {
        if (condition == null || condition.isBlank()) {
            return null;
        }
        return "%" + condition.trim();
    }

    /**
     * 后模糊查询
     * @param condition 查询条件
     * @return condition + '%'
     */
    public static String fuzzyRight(String condition) {
        if (condition == null || condition.isBlank()) {
            return null;
        }
        return condition.trim() + "%";
    }
}
