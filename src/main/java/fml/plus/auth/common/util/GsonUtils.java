package fml.plus.auth.common.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fml.plus.auth.common.util.gson.FieldIgnore;
import fml.plus.auth.common.util.gson.BooleanAdapter;
import fml.plus.auth.common.util.gson.JDK8TimeAdapter;
import fml.plus.auth.common.util.gson.NumberAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GsonUtils {
    private GsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Gson gson;

    static {
        gson = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(LocalDateTime.class, JDK8TimeAdapter.LocalDateTimeAdapter._Instance)
                .registerTypeAdapter(LocalDate.class, JDK8TimeAdapter.LocalDateAdapter._Instance)
                .registerTypeAdapter(LocalTime.class, JDK8TimeAdapter.LocalTimeAdapter._Instance)

                .registerTypeAdapter(int.class, NumberAdapter.IntAdapter._Instance)
                .registerTypeAdapter(Integer.class, NumberAdapter.IntAdapter._Instance)
                .registerTypeAdapter(long.class, NumberAdapter.LongAdapter._Instance)
                .registerTypeAdapter(Long.class, NumberAdapter.LongAdapter._Instance)
                .registerTypeAdapter(short.class, NumberAdapter.ShortAdapter._Instance)
                .registerTypeAdapter(Short.class, NumberAdapter.ShortAdapter._Instance)
                .registerTypeAdapter(byte.class, NumberAdapter.ByteAdapter._Instance)
                .registerTypeAdapter(Byte.class, NumberAdapter.ByteAdapter._Instance)
                .registerTypeAdapter(float.class, NumberAdapter.FloatAdapter._Instance)
                .registerTypeAdapter(Float.class, NumberAdapter.FloatAdapter._Instance)
                .registerTypeAdapter(double.class, NumberAdapter.DoubleAdapter._Instance)
                .registerTypeAdapter(Double.class, NumberAdapter.DoubleAdapter._Instance)
                .registerTypeAdapter(BigDecimal.class, NumberAdapter.BigDecimalAdapter._Instance)

                .registerTypeAdapter(boolean.class, BooleanAdapter._Instants)
                .registerTypeAdapter(Boolean.class, BooleanAdapter._Instants)

                .setExclusionStrategies(new ExclusionStrategy() {

                    @Override
                    public boolean shouldSkipField(FieldAttributes f) { //需要忽略的字段,返回true会忽略
                        return f.getAnnotation(FieldIgnore.class) != null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) { //忽略的类
                        return false;
                    }

                }).create();

    }

    public static Gson gson() {
        return gson;
    }

}
