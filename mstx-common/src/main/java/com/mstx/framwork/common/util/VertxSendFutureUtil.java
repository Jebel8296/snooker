package com.mstx.framwork.common.util;

import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class VertxSendFutureUtil {

    private static Logger logger = LoggerFactory.getLogger(VertxSendFutureUtil.class);

    public static void vertxSendFuture(Vertx vertx, String serviceAddress, JsonObject serviceParam, DeliveryOptions deliveryOptions, CompletableFuture<String> future) {
        vertx.eventBus().send(serviceAddress, serviceParam, deliveryOptions, messageAsyncResult -> {
            if (messageAsyncResult.succeeded()) {
                future.complete(messageAsyncResult.result().body().toString());
            } else {
                logger.error("【" + serviceAddress + "】 Error : {}", messageAsyncResult.cause());
                future.complete(RespBodyBuilder.toError(RespCode.CODE_10508));
            }
        });
    }
}
