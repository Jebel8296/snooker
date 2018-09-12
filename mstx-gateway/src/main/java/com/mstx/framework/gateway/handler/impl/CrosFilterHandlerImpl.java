package com.mstx.framework.gateway.handler.impl;

import com.mstx.framework.gateway.handler.CrosFilterHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrosFilterHandlerImpl implements CrosFilterHandler {

    private static Logger logger = LoggerFactory.getLogger(CrosFilterHandlerImpl.class);
    private Vertx vertx;

    public CrosFilterHandlerImpl(Vertx vertx) {
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
        }
        if (!method.equals("GET") && !method.equals("POST")) {
            response.end();
        }
        ctx.next();
    }
}
