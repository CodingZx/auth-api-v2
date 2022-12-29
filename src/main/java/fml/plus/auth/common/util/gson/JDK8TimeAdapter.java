package fml.plus.auth.common.util.gson;

import com.google.common.base.Strings;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public abstract class JDK8TimeAdapter<T extends Temporal> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String value = jsonElement.getAsJsonPrimitive().getAsString();
        if(Strings.isNullOrEmpty(value) || "null".equals(value.trim())) {
            return null;
        }
        try {
            return parser(value);
        } catch (Exception e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(T val, Type type, JsonSerializationContext jsonSerializationContext) {
        return val == null ? JsonNull.INSTANCE : new JsonPrimitive(format(val));
    }

    public abstract String format(T value);
    public abstract T parser(String value);

    public static final class LocalDateTimeAdapter extends JDK8TimeAdapter<LocalDateTime> {
        private LocalDateTimeAdapter(){}
        public static final LocalDateTimeAdapter _Instance = new LocalDateTimeAdapter();

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LocalDateTime value) {
            return value.format(formatter);
        }

        @Override
        public LocalDateTime parser(String value) {
            return LocalDateTime.parse(value, formatter);
        }
    }

    public static final class LocalDateAdapter extends JDK8TimeAdapter<LocalDate> {
        private LocalDateAdapter(){}
        public static final LocalDateAdapter _Instance = new LocalDateAdapter();

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public String format(LocalDate value) {
            return value.format(formatter);
        }

        @Override
        public LocalDate parser(String value) {
            return LocalDate.parse(value, formatter);
        }
    }

    public static final class LocalTimeAdapter extends JDK8TimeAdapter<LocalTime> {
        private LocalTimeAdapter(){}
        public static final LocalTimeAdapter _Instance = new LocalTimeAdapter();

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        @Override
        public String format(LocalTime value) {
            return value.format(formatter);
        }

        @Override
        public LocalTime parser(String value) {
            return LocalTime.parse(value, formatter);
        }
    }
}
