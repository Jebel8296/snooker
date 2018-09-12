package com.mstx.framework.snooker.worker;

import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LiveScoresVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(LiveScoresVerticle.class);

    @Autowired
    Environment environment;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer("snooker.livescores", message -> {
            JsonObject param = ((JsonObject) message.body());
            try {
                logger.info("accept:" + param);
                String template = param.containsKey("template") ? param.getString("template") : "21";
                String event = param.containsKey("event") ? param.getString("event") : "709";
            } catch (Exception exception) {
                logger.error("error:" + exception.getMessage());
                message.reply(RespBodyBuilder.toError(RespCode.CODE_10501));
            }
        });
        logger.info("LiveScoresVerticle start success.");
    }
}
