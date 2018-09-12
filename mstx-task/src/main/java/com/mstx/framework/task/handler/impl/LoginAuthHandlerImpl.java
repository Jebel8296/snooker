package com.mstx.framework.task.handler.impl;

import com.mstx.framework.task.handler.LoginAuthHandler;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginAuthHandlerImpl implements LoginAuthHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Vertx vertx;
    protected final JWTAuth jwt;

    public LoginAuthHandlerImpl(Vertx vertx, JWTAuth jwt) {
        this.vertx = vertx;
        this.jwt = jwt;
    }

    @Override
    public void handle(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        Optional<String> token = Optional.ofNullable(ctx.request().headers().get("token"));
        if (!token.isPresent()) {
            logger.info("No Token.");
            response.end(RespBodyBuilder.toError(RespCode.CODE_10504));
        } else {
            jwt.authenticate(new JsonObject().put("jwt", token.get()), handler -> {
                if (handler.succeeded()) {
                    logger.info("Token check succeed.");
                    ctx.next();
                } else {
                    logger.info("Token check faild.");
                    response.end(RespBodyBuilder.toError(RespCode.CODE_10504));
                }
            });
        }
    }
}
