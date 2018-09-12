package com.mstx.framework.gateway.handler;

import com.mstx.framework.gateway.handler.impl.CrosFilterHandlerImpl;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

@VertxGen
public interface CrosFilterHandler extends Handler<RoutingContext> {
    static CrosFilterHandler create(Vertx vertx) {
        return new CrosFilterHandlerImpl(vertx);
    }
}
