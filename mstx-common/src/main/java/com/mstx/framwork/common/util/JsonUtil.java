package com.mstx.framwork.common.util;

import io.vertx.core.MultiMap;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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
        return Json.encode(object);
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

    public static JsonObject convertMultiMap(MultiMap multiMap) {
        JsonObject result = new JsonObject();
        if (multiMap != null && multiMap.size() > 0) {
            Iterator iterator = multiMap.entries().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getKey() != null && !entry.getKey().equals("")) {
                    result.put(entry.getKey().toString(), entry.getValue());
                }
            }
        }
        return result;
    }

    public static JsonObject parsePostBody(String body) {
        JsonObject result = null;
        if (body != null && body.trim().length() > 0) {
            result = new JsonObject();
            if (body.trim().indexOf("&") > 0) {
                String[] paras = body.trim().split("&");
                for (String p : paras) {
                    String[] ps = p.split("=");
                    result.put(ps[0].toString(), ps[1].toString());
                }
            } else {
                String[] paras = body.trim().split("=");
                result.put(paras[0].toString(), paras[1].toString());
            }
        }
        return result;
    }

    public static String generateRandomCode(int digit) {
        String code = String.valueOf(new Random().nextInt(10));
        switch (digit) {
            case 2:
                code = String.valueOf(new Random().nextInt(89) + 10);
                break;
            case 4:
                code = String.valueOf(new Random().nextInt(8999) + 1000);
                break;
            case 6:
                code = String.valueOf(new Random().nextInt(899999) + 100000);
                break;
            case 8:
                code = String.valueOf(new Random().nextInt(89999999) + 10000000);
                break;
            default:
                break;
        }
        return code;
    }

    public static String getServiceID() {
        return System.currentTimeMillis() + generateRandomCode(2);
    }

    public static String getServiceIDBySef(int digit) {
        return System.currentTimeMillis() + generateRandomCode(digit);
    }

    /**
     * 截取身份证后8位
     *
     * @param card
     * @throws Exception
     */
    public static String subAfter8OfCard(String card) {
        return card.substring(card.length() - 8, card.length());
    }

    public static void main(String[] args) throws Exception {

        try {
            Map<String, String> params = new TreeMap<String, String>();
            params.put("orderCode", "TEST153535491405551");
            params.put("phonenum", "17090337520");
            params.put("recharge", "1");
            params.put("payChannel", "alipay");
            String encode = CodecUtil.sign(params, "3c6d4258-5adb-4be4-8ca1-b9bff10ac884");
            params.put("encodeStr", encode);
            HttpURLConnection connection = HttpConnectPostUtil.submitPostData(params, "UTF-8", "http://111.198.131.251:6060/mstx/payback/toOrder");
            System.out.println("code:" + connection.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
