package fml.plus.auth.common.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;

public final class BooleanAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

    private BooleanAdapter(){}

    public final static BooleanAdapter _Instants = new BooleanAdapter();

    @Override
    public Boolean deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            if (jsonElement.getAsString().equals("") || jsonElement.getAsString().equals("null")) {
                return type == boolean.class ? false : null;
            }
        } catch (Exception ignore) {}
        return jsonElement.getAsBoolean();
    }

    @Override
    public JsonElement serialize(Boolean aBoolean, Type type, JsonSerializationContext jsonSerializationContext) {
        return aBoolean == null ? JsonNull.INSTANCE : new JsonPrimitive(aBoolean);
    }
}
