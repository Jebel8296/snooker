package com.mstx.framework.gateway.handler.impl;

import com.mstx.framework.gateway.handler.ParamFilterHandler;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.JsonUtil;
import com.mstx.framwork.common.util.SignUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.List;

public class ParamFilterHandlerImpl implements ParamFilterHandler {

    private static Logger logger = LoggerFactory.getLogger(ParamFilterHandlerImpl.class);
    private Vertx vertx;
    private Environment environment;

    public ParamFilterHandlerImpl(Vertx vertx, Environment environment) {
        this.vertx = vertx;
        this.environment = environment;
    }

    @Override
    public void handle(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "application/json;charset=UTF-8");
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,OPTIONS");
        response.putHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        String method = ctx.request().method().name();
        JsonObject body = method.equals("POST") ? ctx.getBodyAsJson() : JsonUtil.convertMultiMap(ctx.request().params());
        String service = body.getString("service");
        if (service.startsWith("agent")) {
            if (!body.containsKey("sign") || !body.containsKey("label")) {
                response.end(RespBodyBuilder.toError(RespCode.CODE_10502));
                return;
            }
            if (!"ZMTX".equals(body.getString("label"))) {
                if (environment.containsProperty(body.getString("label"))) {
                    String sign = SignUtil.getSign(body, environment.getProperty(body.getString("label")));
                    logger.info(environment.getProperty(body.getString("label")) + " = " + sign);
                    if (!sign.equals(body.getString("sign"))) {
                        response.end(RespBodyBuilder.toError(RespCode.CODE_10503));
                        return;
                    }
                }
            }
        }
        ctx.next();
    }
}
