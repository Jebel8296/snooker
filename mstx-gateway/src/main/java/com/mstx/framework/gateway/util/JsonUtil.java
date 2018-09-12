package com.mstx.framework.gateway.util;

import java.util.UUID;
import java.util.regex.Pattern;

import io.vertx.core.json.Json;

/**
 * Json格式处理类
 */
public class JsonUtil {

    /**
     * 将Object对象转换成Json
     *
     * @param object Object对象
     * @return Json字符串
     */
    public static String convertObject2Json(Object object) {
        return Json.encodePrettily(object);
    }

    /**
     * 将Json转换成Object对象
     *
     * @param json Json字符串
     * @param cls  转换成的对象类型
     * @return 转换之后的对象
     */
    public static <T> T convertJson2Object(String json, Class<T> cls) {
        return Json.decodeValue(json, cls);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(UUID.randomUUID());
    }

}
