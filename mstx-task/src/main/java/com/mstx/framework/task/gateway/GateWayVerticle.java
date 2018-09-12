package com.mstx.framework.task.gateway;

import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.JsonUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.LoggerHandler;
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

        router.route("/mstx/test.do").handler(this::doTest);
        router.route("/mstx/gateway.do").handler(this::doEventBusGateway);
        Integer port = Integer.valueOf(environment.getProperty("server.port", "6060"));
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }

    private void doTest(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader("content-type", "application/json;charset=UTF-8");
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,OPTIONS");
        response.putHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        response.end(RespBodyBuilder.toCommonSuccess());
    }

    private void doEventBusGateway(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader("content-type", "application/json;charset=UTF-8");
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,OPTIONS");
        response.putHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        String method = context.request().method().name();
        if (method.equals("OPTIONS")) {
            response.end();
            return;
        }
        if (!method.equals("GET") && !method.equals("POST")) {
            response.end();
            return;
        }
        JsonObject resp = new JsonObject();
        JsonObject requestBody = method.equals("POST") ? context.getBodyAsJson() : JsonUtil.convertMultiMap(context.request().params());
        try {
            logger.info("accept : " + requestBody);
            String channel = requestBody.containsKey("channel") ? requestBody.getString("channel") : "web";
            if (!requestBody.containsKey("service") || !requestBody.containsKey("sign") || !requestBody.containsKey("label")) {
                response.end(RespBodyBuilder.toError(RespCode.CODE_10502));
                return;
            }
            requestBody.put("channel", channel);
            DeliveryOptions options = new DeliveryOptions();
            options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
            vertx.eventBus().send(requestBody.getString("service"), requestBody, options, res -> {
                if (res.succeeded()) {
                    resp.mergeIn(new JsonObject(res.result().body().toString()));
                } else {
                    logger.error("error :" + res.cause().getMessage());
                    resp.mergeIn(new JsonObject(RespBodyBuilder.toError(RespCode.CODE_10501)));
                }
                logger.info("reply:" + resp.toString());
                response.end(resp.toString());
            });
        } catch (Exception e) {
            logger.error("error :" + e.getMessage());
            response.end(RespBodyBuilder.toError(RespCode.CODE_10501));
        }
    }

}
