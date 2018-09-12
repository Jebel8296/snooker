package com.mstx.framework.gateway.response;

import org.apache.log4j.Logger;

import com.mstx.framework.gateway.util.JsonUtil;

public class RespBodyBuilder {

    protected Logger LOG = Logger.getLogger(getClass());

    public String toSuccess() {
        String reply = null;
        RespBody body = new RespBody();
        reply = JsonUtil.convertObject2Json(body);
        return reply;
    }

    public String toSuccess(Object data) {
        String reply = null;
        RespBody body = new RespBody(data);
        reply = JsonUtil.convertObject2Json(body);
        return reply;
    }

    public String toError(RespCode code) {
        String reply = null;
        RespBody body = new RespBody(code);
        reply = JsonUtil.convertObject2Json(body);
        return reply;
    }

}
