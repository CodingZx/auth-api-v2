package fml.plus.auth.common.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public abstract class NumberAdapter<T extends Number> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(jsonElement.isJsonNull()) {
            return getDefaultValue(type);
        }
        try {
            if (jsonElement.getAsString().equals("") || jsonElement.getAsString().equals("null")) {
                return getDefaultValue(type);
            }
        } catch (Exception ignore) {}
        try {
            return getValue(jsonElement, type);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(T num, Type type, JsonSerializationContext jsonSerializationContext) {
        return num == null ? JsonNull.INSTANCE : new JsonPrimitive(num);
    }

    /**
     * 获得默认值
     */
    protected abstract T getDefaultValue(Type type);

    /**
     * 取值
     */
    protected abstract T getValue(JsonElement jsonElement, Type type);


    /**
     * int类型
     */
    public static final class IntAdapter extends NumberAdapter<Integer> {
        private IntAdapter(){}

        public final static IntAdapter _Instance = new IntAdapter();

        @Override
        protected Integer getDefaultValue(Type type) {
            return type == int.class ? 0 : null;
        }

        @Override
        protected Integer getValue(JsonElement jsonElement, Type type) {
            return jsonElement.getAsInt();
        }
    }

    /**
     * long类型
     */
    public static final class LongAdapter extends NumberAdapter<Long> {
        private LongAdapter(){}
        public final static LongAdapter _Instance = new LongAdapter();

        @Override
        protected Long getDefaultValue(Type type) {
            return type == long.class ? 0L : null;
        }

        @Override
        protected Long getValue(JsonElement jsonElement, Type type) {
            return jsonElement.getAsLong();
        }
    }


    /**
     * short类型
     */
    public static final class ShortAdapter extends NumberAdapter<Short> {
        private ShortAdapter(){}
        public final static ShortAdapter _Instance = new ShortAdapter();

        @Override
        protected Short getDefaultValue(Type type) {
            return type == short.class ? (short)0 : null;
        }

        @Override
        protected Short getValue(JsonElement jsonElement, Type type) {
            return jsonElement.getAsShort();
        }
    }

    /**
     * byte类型
     */
    public static final class ByteAdapter extends NumberAdapter<Byte> {
        private ByteAdapter(){}
        public final static ByteAdapter _Instance = new ByteAdapter();

        @Override
        protected Byte getDefaultValue(Type type) {
            return type == byte.class ? (byte)0 : null;
        }

        @Override
        protected Byte getValue(JsonElement jsonElement, Type type) {
            return jsonElement.getAsByte();
        }
    }

    /**
     * float类型
     */
    public static final class FloatAdapter extends NumberAdapter<Float> {
        private FloatAdapter(){}
        public final static FloatAdapter _Instance = new FloatAdapter();

        @Override
        protected Float getDefaultValue(Type type) {
            return type == float.class ? 0.0f : null;
        }

        @Override
        protected Float getValue(JsonElement jsonElement, Type type) {
            return jsonElement.getAsFloat();
        }
    }

    /**
     * double类型
     */
    public static final class DoubleAdapter extends NumberAdapter<Double> {
        private DoubleAdapter(){}
        public final static DoubleAdapter _Instance = new DoubleAdapter();

        @Override
        protected Double getDefaultValue(Type type) {
            return type == double.class ? 0.0 : null;
        }

        @Override
        protected Double getValue(JsonElement jsonElement, Type type) {
            return jsonElement.getAsDouble();
        }
    }


    /**
     * BigDecimal
     */
    public static final class BigDecimalAdapter extends NumberAdapter<BigDecimal> {
        private BigDecimalAdapter(){}
        public final static BigDecimalAdapter _Instance = new BigDecimalAdapter();

        @Override
        protected BigDecimal getDefaultValue(Type type) {
            return null;
        }

        @Override
        protected BigDecimal getValue(JsonElement jsonElement, Type type) {
            return jsonElement.getAsBigDecimal();
        }
    }
}
