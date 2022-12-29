package fml.plus.auth.common.util.time;

import com.google.common.base.Strings;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class LocalDateUtils {

    private LocalDateUtils() {
        throw new AssertionError();
    }

    /**
     * LocalDate转换为Date对象
     * @param localDate 待转换日期
     * @return 对应Date对象
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.of("+8")).toInstant());
    }

    /**
     * 字符串转换为LocalDate对象
     * @param date    日期字符串
     * @param pattern 转换格式
     * @return 对应LocalDate对象
     * @throws IllegalArgumentException 如果日期格式不正确
     * @throws DateTimeParseException   如果日期字符串无法被转换
     */
    public static LocalDate parse(String date, String pattern) {
        if(Strings.isNullOrEmpty(date)) {
            return null;
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(date, format);
    }

    /**
     * 以默认格式将字符串转换为LocalDate对象
     * @param date 日期字符串
     * @return 对应LocalDate对象
     * @throws DateTimeParseException 如果日期字符串无法被转换
     */
    public static LocalDate parse(String date) {
        return parse(date, DateUtils.DEFAULT_DATE_FORMATTER);
    }

    /**
     * 将LocalDate转换为字符串
     * @param date    待转换LocalDate对象
     * @param pattern 转换格式
     * @return 转换后的字符串
     * @throws IllegalArgumentException 如果日期格式不正确
     * @throws DateTimeParseException   如果日期字符串无法被转换
     */
    public static String format(LocalDate date, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }

    /**
     * 将LocalDate转换为默认日期格式字符串
     * @param date 待转换LocalDate对象
     * @return 转换后的字符串
     * @throws DateTimeParseException 如果日期字符串无法被转换
     */
    public static String format(LocalDate date) {
        return format(date, DateUtils.DEFAULT_DATE_FORMATTER);
    }

    /**
     * 将Date转换为LocalDate对象
     * @param date 待转换Date对象
     * @return 转换后的LocalDate对象
     */
    public static LocalDate toLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.of("+8"));
    }

    /**
     * 获得当前日期字符串
     */
    public static String getCurrentDate() {
        return format(LocalDate.now());
    }

    /**
     * 获得两日期之间间隔天数
     * @param start 开始日期
     * @param end   结束日期
     * @return 间隔天数
     */
    public static long getBetweenDays(LocalDate start, LocalDate end) {
        return start.until(end, ChronoUnit.DAYS);
    }

}
