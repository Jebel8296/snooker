package com.mstx.framework.gateway.handler.impl;

import com.mstx.framework.gateway.handler.PublicServiceFilterHandler;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.JsonUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicServiceFilterHandlerImpl implements PublicServiceFilterHandler {

    private static Logger logger = LoggerFactory.getLogger(PublicServiceFilterHandlerImpl.class);
    private Vertx vertx;

    public PublicServiceFilterHandlerImpl(Vertx vertx) {
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
        JsonObject body = method.equals("POST") ? ctx.getBodyAsJson() : JsonUtil.convertMultiMap(ctx.request().params());
        String service = body.getString("service");

        if (service.startsWith("user") || service.startsWith("login") || service.startsWith("network") || service.startsWith("agent") || service.startsWith("snooker")) {
            ctx.next();
        } else {
            response.end(RespBodyBuilder.toError(RespCode.CODE_10506));
            return;
        }
    }
}
