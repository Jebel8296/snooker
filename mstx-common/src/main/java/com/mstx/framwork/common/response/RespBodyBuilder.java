package com.mstx.framwork.common.response;

import com.mstx.framwork.common.util.JsonUtil;
import io.vertx.core.json.JsonObject;
import netscape.javascript.JSObject;
import org.apache.log4j.Logger;

import javax.annotation.Resource;

public class RespBodyBuilder {

    protected Logger LOG = Logger.getLogger(getClass());

    public static String toCommonSuccess() {
        RespBody body = new RespBody();
        return JsonUtil.convertObject2Json(body);
    }

    public static String toSuccessWithObject(Object data) {
        RespBody body = new RespBody(data);
        return JsonUtil.convertObject2Json(body);
    }

    public static String toError(RespCode code) {
        JsonObject error = new JsonObject();
        error.put("errorCode", code.getCode());
        error.put("errorMsg", code.getMsg());
        RespBody respBody = new RespBody(RespCode.CODE_10500, error);
        return JsonUtil.convertObject2Json(respBody);
    }

    public static String toError(String code, String msg) {
        JsonObject error = new JsonObject();
        error.put("errorCode", code);
        error.put("errorMsg", msg);
        RespBody respBody = new RespBody(RespCode.CODE_10500, error);
        return JsonUtil.convertObject2Json(respBody);
    }

}
