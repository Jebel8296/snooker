package com.mstx.framwork.common.util;

import io.vertx.core.json.JsonObject;

import java.util.*;

public class SignUtil {

    public static String getSign(JsonObject param, String key) {
        List<String> params = new ArrayList<>();
        if (param != null && param.size() > 0) {
            Iterator iterator = param.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getValue() != null && !entry.getValue().equals("") && !entry.getKey().toString().equals("sign")) {
                    params.add(entry.getValue().toString());
                }
            }
        }
        params.add(key);
        String[] paramArray = params.toArray(new String[0]);
        Arrays.sort(paramArray);
        StringBuilder sb = new StringBuilder();
        for (String s : paramArray) {
            if (s != null && !"null".equals(s)) {
                sb.append(s);
            }
        }
        System.out.println(sb.toString());
        return SHA.getSHA(sb.toString());
    }

    public static String getSign(JsonObject param) {
        List<String> params = new ArrayList<>();
        String label = param.getString("label");
        if (param != null && param.size() > 0) {
            Iterator iterator = param.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getValue() != null && !entry.getValue().equals("") && !entry.getKey().toString().equals("sign")) {
                    params.add(entry.getValue().toString());
                }
            }
        }
        params.add(label);
        String[] paramArray = params.toArray(new String[0]);
        Arrays.sort(paramArray);
        StringBuilder sb = new StringBuilder();
        for (String s : paramArray) {
            if (s != null && !"null".equals(s)) {
                sb.append(s);
            }
        }
        return SHA.getSHA(sb.toString());
    }


    public static void main(String[] args) {
        JsonObject params = new JsonObject();
        params.put("phonenum", "17000013579");
        params.put("orderCode", "Pay17000013579595153602725347");
        params.put("recharge", "1");
        params.put("payChannel", "yrboss");
        params.put("service", "agent.synrecharge");
        params.put("label", "BS001");
        String encode = SignUtil.getSign(params, "1e5af2fd-4b0c-4a6d-9d6a-fa4b7e6626c1");
        System.out.println(encode);
    }

}
