package com.zr.deliver.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zr.deliver.misc.BASE64Decoder;
import com.zr.deliver.misc.BASE64Encoder;

import java.lang.reflect.Type;


public class ByteArrayTypeAdapter implements JsonSerializer<byte[]>,
        JsonDeserializer<byte[]> {
    public JsonElement serialize(byte[] src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        BASE64Encoder encode = new BASE64Encoder();
        String base64 = encode.encode(src);
        return new JsonPrimitive(base64);
    }

    public byte[] deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }
        try {
            BASE64Decoder decode = new BASE64Decoder();
            byte[] base64 = decode.decodeBuffer(json.getAsString());
            return base64;
        } catch (Exception ex) {
        }
        return null;
    }
}