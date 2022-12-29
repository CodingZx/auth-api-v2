package fml.plus.auth.common.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    // 默认日期格式
    public static final String DEFAULT_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMATTER = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMATTER = "HH:mm:ss";

    public static Date parse(String date, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(date);
    }

    public static String format(Date time, String pattern) {
        return new SimpleDateFormat(pattern).format(time);
    }

    public static String format(Date time) {
        return format(time, DEFAULT_DATE_TIME_FORMATTER);
    }

    public static Date getCurrentDate() {
        return new Date();
    }

}
