package com.mstx.framwork.common.service;


import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface ICommService {
    void sendLogInfo(String service, Vertx vertx, JsonObject logInfol);

    String post(String appkey, String aeskey, String token, String opCode, JsonObject svcCount);

    String pay(String url, JsonObject param);

    void vertxSendFuture(Vertx vertx, String serviceAddress, JsonObject serviceParam, Future<String> future);
}
