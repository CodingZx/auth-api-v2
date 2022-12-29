package fml.plus.auth.common.util.time;

import com.google.common.base.Strings;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateTimeUtils {

    private LocalDateTimeUtils() {
        throw new AssertionError();
    }

    /**
     * 字符串转换为LocalDateTime
     * @param timeStr 时间字符串
     * @return 转换后的LocalDateTime
     * @throws IllegalArgumentException 如果转换格式不合法
     */
    public static LocalDateTime parseTime(String timeStr) {
        return parseTime(timeStr, DateUtils.DEFAULT_DATE_TIME_FORMATTER);
    }

    /**
     * 字符串转换为LocalDateTime
     * @param timeStr 时间字符串
     * @param pattern 转换格式
     * @return 转换后的LocalDateTime
     * @throws IllegalArgumentException 如果转换格式不合法
     */
    public static LocalDateTime parseTime(String timeStr, String pattern) {
        if(Strings.isNullOrEmpty(timeStr)) {
            return null;
        }
        return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDateTime转换为字符串
     * @param date    待转换时间
     * @param pattern 字符串格式
     * @return 对应时间的字符串
     * @throws IllegalArgumentException 如果转换格式不合法
     * @throws DateTimeException        如果转换时发生错误
     */
    public static String format(LocalDateTime date, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }

    /**
     * LocalDateTime转换为默认格式(yyyy-MM-dd HH:mm:ss)字符串
     * @param date 待转换时间
     * @return 对应时间的字符串
     * @throws DateTimeException 如果转换时发生错误
     */
    public static String format(LocalDateTime date) {
        return format(date, DateUtils.DEFAULT_DATE_TIME_FORMATTER);
    }

    /**
     * LocalDateTime转换为Date对象
     * @param time 待转换对象
     * @return 对应Date对象
     */
    public static Date toDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.of("+8")).toInstant());
    }

    /**
     * Date对象转换为LocalDateTime
     * @param time 待转换的Date对象
     * @return 对应LocalDateTime对象
     * @throws NullPointerException 如果传入参数为空
     */
    public static LocalDateTime toLocalDateTime(Date time) {
        return LocalDateTime.ofInstant(time.toInstant(), ZoneId.of("+8"));
    }

    /**
     * 时间戳转换为LocalDateTime
     * @param timestamp 时间戳
     * @return 对应LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("+8"));
    }

    /**
     * 获得LocalDateTime的时间戳
     */
    public static long getTime(LocalDateTime time) {
        return time.atZone(ZoneId.of("+8")).toInstant().toEpochMilli();
    }

    /**
     * 以默认格式获得当前时间的字符串
     * @return 当前时间
     */
    public static String getCurrentDateTime() {
        return format(LocalDateTime.now());
    }

}
