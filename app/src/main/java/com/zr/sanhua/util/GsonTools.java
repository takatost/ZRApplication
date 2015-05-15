package com.zr.sanhua.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonTools {

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder(); // 
        builder.disableHtmlEscaping();
        builder.registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter());
        builder.disableHtmlEscaping();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return builder.create();
    }
}
