package com.mstx.framework.gateway.handler;

import com.mstx.framework.gateway.handler.impl.ServiceFilterHandlerImpl;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

@VertxGen
public interface ServiceFilterHandler extends Handler<RoutingContext> {
    static ServiceFilterHandler create(Vertx vertx) {
        return new ServiceFilterHandlerImpl(vertx);
    }
}
