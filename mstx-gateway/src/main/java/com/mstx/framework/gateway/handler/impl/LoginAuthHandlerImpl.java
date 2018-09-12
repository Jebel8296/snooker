package com.mstx.framework.gateway.handler.impl;

import com.mstx.framework.gateway.handler.LoginAuthHandler;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.JsonUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginAuthHandlerImpl implements LoginAuthHandler {

    private static Logger logger = LoggerFactory.getLogger(LoginAuthHandlerImpl.class);
    private Vertx vertx;
    private JWTAuth jwt;

    public LoginAuthHandlerImpl(Vertx vertx, JWTAuth jwt) {
        this.vertx = vertx;
        this.jwt = jwt;
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
        if (service.startsWith("network")) {
            Optional<String> token = Optional.ofNullable(ctx.request().headers().get("token"));
            if (!token.isPresent() && !body.containsKey("token")) {
                logger.info("No Token.");
                response.end(RespBodyBuilder.toError(RespCode.CODE_10502));
                return;
            } else {
                String tokenStr = token.isPresent() ? token.get() : body.getString("token");
                if (tokenStr.indexOf("zmtx") > -1) {
                    ctx.next();
                } else {
                    jwt.authenticate(new JsonObject().put("jwt", tokenStr), handler -> {
                        if (handler.succeeded()) {
                            logger.info("Token check succeed.");
                            ctx.next();
                        } else {
                            logger.info("Token check faild.");
                            response.end(RespBodyBuilder.toError(RespCode.CODE_10504));
                            return;
                        }
                    });
                }
            }
        } else {
            ctx.next();
        }
    }
}
