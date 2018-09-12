package com.mstx.framework.gateway.handler;

import com.mstx.framework.gateway.handler.impl.ParamFilterHandlerImpl;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.springframework.core.env.Environment;

@VertxGen
public interface ParamFilterHandler extends Handler<RoutingContext> {
    static ParamFilterHandler create(Vertx vertx, Environment environment) {
        return new ParamFilterHandlerImpl(vertx,environment);
    }
}
