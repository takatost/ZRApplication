package com.zr.sanhua.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonTools {
    //	public static Gson getGson() {
//		GsonBuilder builder = new GsonBuilder(); // 不转换没有 @Expose注解的字段
//		// builder.excludeFieldsWithoutExposeAnnotation();
//		builder.registerTypeHierarchyAdapter(byte[].class,
//				new ByteArrayTypeAdapter()).create();
//		Gson gson = builder.create();
//		return gson;
//	}
//
//	public static Gson getVoiceGson() {
//		GsonBuilder builder = new GsonBuilder();
//		builder.registerTypeHierarchyAdapter(byte[].class, new VoiceAdapter());
//		// builder.registerTypeAdapter(CommonVoice.class, new
//		// CommonVoiceAdapter());
//		Gson gson = builder.create();
//		return gson;
//	}
//
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder(); // 
        builder.disableHtmlEscaping();
        builder.registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter());
        builder.disableHtmlEscaping();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        Gson gson = builder.create();
        return gson;
    }
}
