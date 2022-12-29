package fml.plus.auth.common.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;

public final class CharAdapter implements JsonSerializer<Character>, JsonDeserializer<Character> {

    public final static CharAdapter _Instants = new CharAdapter();

    private char defVal;

    @Override
    public Character deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            if (jsonElement.getAsString().equals("") || jsonElement.getAsString().equals("null")) {
                return type == char.class ?  defVal : null;
            }
        } catch (Exception ignore) {}
        return jsonElement.getAsString().charAt(0);
    }

    @Override
    public JsonElement serialize(Character c, Type type, JsonSerializationContext jsonSerializationContext) {
        return c == null ? JsonNull.INSTANCE : new JsonPrimitive(c);
    }
}
