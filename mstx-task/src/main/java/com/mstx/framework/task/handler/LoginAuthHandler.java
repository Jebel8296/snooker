package com.mstx.framework.task.handler;

import com.mstx.framework.task.handler.impl.LoginAuthHandlerImpl;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

@VertxGen
public interface LoginAuthHandler extends Handler<RoutingContext> {
    static LoginAuthHandler create(Vertx vertx, JWTAuth jwt) {
        return new LoginAuthHandlerImpl(vertx, jwt);
    }
}
