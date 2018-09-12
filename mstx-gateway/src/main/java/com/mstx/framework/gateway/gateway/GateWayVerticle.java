package com.mstx.framework.gateway.gateway;

import com.mstx.framework.gateway.handler.*;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.JsonUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GateWayVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(GateWayVerticle.class);

    @Autowired
    private Environment environment;

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(LoggerHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());

        JWTAuthOptions jwtAuthOptions = new JWTAuthOptions();
        jwtAuthOptions.setKeyStore(new KeyStoreOptions().setPath("keystore.jceks").setPassword("secret"));
        JWTAuth jwtAuth = JWTAuth.create(vertx, jwtAuthOptions);

        router.route("/mstx/pr/*").handler(ServiceFilterHandler.create(vertx));

        router.route("/mstx/pr/gateway.do").handler(ParamFilterHandler.create(vertx, environment));
        router.route("/mstx/pr/gateway.do").handler(LoginAuthHandler.create(vertx, jwtAuth));
        router.route("/mstx/pr/gateway.do").handler(PublicServiceFilterHandler.create(vertx));
        router.route("/mstx/pr/gateway.do").handler(this::doPrEventBusGateway);

        router.route("/mstx/pu/gateway.do").handler(PublicServiceFilterHandler.create(vertx));
        router.route("/mstx/pu/gateway.do").handler(this::doPuEventBusGateway);

        router.route("/mstx/payback/toOrder").handler(this::doPaybackToorder);

        Integer port = Integer.valueOf(environment.getProperty("server.port", "6060"));
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }


    private void doPaybackToorder(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader("content-type", "application/json;charset=UTF-8");
        String method = context.request().method().name();
        logger.info("doPaybackToorder method=" + method);
        JsonObject resp = new JsonObject();
        logger.info("doPaybackToorder param=" + context.getBodyAsString());
        JsonObject requestBody = method.equals("POST") ? JsonUtil.parsePostBody(context.getBodyAsString()) : JsonUtil.convertMultiMap(context.request().params());
        try {
            if (requestBody == null) {
                response.end(RespBodyBuilder.toError(RespCode.CODE_10502));
                return;
            }
            logger.info("accept : " + requestBody.toString());
            String LID = JsonUtil.getServiceIDBySef(4);
            resp.put("lid", LID);
            requestBody.put("lid", LID);
            vertx.eventBus().send("log.service.req", requestBody.copy().put("service", "network.payback"));//记录日志
            DeliveryOptions options = new DeliveryOptions();
            options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
            vertx.eventBus().send("network.payback", requestBody, options, res -> {
                if (res.succeeded()) {
                    resp.mergeIn(new JsonObject(res.result().body().toString()));
                } else {
                    logger.error("error :" + res.cause().getMessage());
                    if (res.cause().getMessage().indexOf("No handlers for address") > -1) {
                        resp.mergeIn(new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10505)));
                    } else {
                        resp.mergeIn(new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10501)));
                    }
                }
                logger.info("reply:" + resp.toString());
                vertx.eventBus().send("log.service.res", resp.copy().put("service", "network.payback"));
                resp.remove("lid");
                response.end(resp.toString());
            });
        } catch (Exception e) {
            logger.error("Error :" + e.getMessage());
            response.end(RespBodyBuilder.toError(RespCode.CODE_10501));
        }
    }

    private void doPuEventBusGateway(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader("content-type", "application/json;charset=UTF-8");
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,OPTIONS");
        response.putHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        String method = context.request().method().name();
        JsonObject resp = new JsonObject();
        JsonObject requestBody = method.equals("POST") ? context.getBodyAsJson() : JsonUtil.convertMultiMap(context.request().params());
        try {
            String channel = requestBody.containsKey("channel") ? requestBody.getString("channel") : "web";
            requestBody.put("channel", channel);
            String LID = JsonUtil.getServiceIDBySef(4);
            resp.put("lid", LID);
            requestBody.put("lid", LID);
            vertx.eventBus().send("log.service.req", requestBody);//记录日志
            DeliveryOptions options = new DeliveryOptions();
            options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
            vertx.eventBus().send(requestBody.getString("service"), requestBody, options, res -> {
                if (res.succeeded()) {
                    resp.mergeIn(new JsonObject(res.result().body().toString()));
                } else {
                    logger.error("error :" + res.cause().getMessage());
                    if (res.cause().getMessage().indexOf("No handlers for address") > -1) {
                        resp.mergeIn(new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10505)));
                    } else {
                        resp.mergeIn(new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10501)));
                    }
                }
                logger.info("reply:" + resp.toString());
                vertx.eventBus().send("log.service.res", resp.copy().put("service", requestBody.getString("service")).put("phonenum", requestBody.getString("serialNumber")));
                resp.remove("lid");
                response.end(resp.toString());
            });
        } catch (Exception e) {
            logger.error("Error :" + e.getMessage());
            vertx.eventBus().send("log.service.res", new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10501)).put("service", requestBody.getString("service")).put("phonenum", requestBody.getString("serialNumber")));
            response.end(RespBodyBuilder.toError(RespCode.CODE_10501));
        }
    }

    private void doPrEventBusGateway(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader("content-type", "application/json;charset=UTF-8");
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,OPTIONS");
        response.putHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        String method = context.request().method().name();
        JsonObject resp = new JsonObject();
        JsonObject requestBody = method.equals("POST") ? context.getBodyAsJson() : JsonUtil.convertMultiMap(context.request().params());
        try {
            String channel = requestBody.containsKey("channel") ? requestBody.getString("channel") : "web";
            requestBody.put("channel", channel);
            String LID = JsonUtil.getServiceIDBySef(4);
            resp.put("lid", LID);
            requestBody.put("lid", LID);
            vertx.eventBus().send("log.service.req", requestBody);//记录日志
            DeliveryOptions options = new DeliveryOptions();
            options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
            vertx.eventBus().send(requestBody.getString("service"), requestBody, options, res -> {
                if (res.succeeded()) {
                    resp.mergeIn(new JsonObject(res.result().body().toString()));
                } else {
                    logger.error("error :" + res.cause().getMessage());
                    if (res.cause().getMessage().indexOf("No handlers for address") > -1) {
                        resp.mergeIn(new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10505)));
                    } else {
                        resp.mergeIn(new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10501)));
                    }
                }
                logger.info("reply:" + resp.toString());
                vertx.eventBus().send("log.service.res", resp.copy().put("service", requestBody.getString("service")).put("phonenum", requestBody.getString("serialNumber")));
                resp.remove("lid");
                response.end(resp.toString());
            });
        } catch (Exception e) {
            logger.error("Error :" + e.getMessage());
            vertx.eventBus().send("log.service.res", new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10501)).put("service", requestBody.getString("service")).put("phonenum", requestBody.getString("serialNumber")));
            response.end(RespBodyBuilder.toError(RespCode.CODE_10501));
        }
    }

}
