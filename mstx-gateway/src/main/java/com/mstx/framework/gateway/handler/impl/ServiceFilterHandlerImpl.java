package com.mstx.framework.gateway.handler.impl;

import com.mstx.framework.gateway.handler.ServiceFilterHandler;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.JsonUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceFilterHandlerImpl implements ServiceFilterHandler {

    private static Logger logger = LoggerFactory.getLogger(ServiceFilterHandlerImpl.class);
    private Vertx vertx;

    public ServiceFilterHandlerImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "application/json;charset=UTF-8");
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,OPTIONS");
        response.putHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        String method = ctx.request().method().name();
        logger.info(method);
        if (method.equals("OPTIONS")) {
            response.end();
            return;
        }
        if (!method.equals("GET") && !method.equals("POST")) {
            response.end();
            return;
        }
        JsonObject body = method.equals("POST") ? ctx.getBodyAsJson() : JsonUtil.convertMultiMap(ctx.request().params());
        logger.info("accept : " + body.toString());
        if (!body.containsKey("service")) {
            response.end(RespBodyBuilder.toError(RespCode.CODE_10502));
            return;
        }
        String service = body.getString("service");
        if (service.indexOf("boss") > -1) {
            response.end(RespBodyBuilder.toError(RespCode.CODE_10506));
            return;
        }
        ctx.next();
    }
}
