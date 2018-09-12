package com.mstx.framwork.common.service.impl;

import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.service.ICommService;
import com.mstx.framwork.common.util.JsonUtil;
import com.mstx.framwork.common.util.SHA1;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CommonServiceImpl implements ICommService {

    private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    Environment environment;


    @Override
    public void vertxSendFuture(Vertx vertx, String serviceAddress, JsonObject serviceParam, Future<String> future) {
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
        vertx.eventBus().send(serviceAddress, serviceParam, options, messageAsyncResult -> {
            if (messageAsyncResult.succeeded()) {
                future.complete(messageAsyncResult.result().body().toString());
            } else {
                logger.error("【" + serviceAddress + "】 Error : {}", messageAsyncResult.cause());
                future.complete(RespBodyBuilder.toError(RespCode.CODE_10508));
            }
        });
    }

    @Override
    public void sendLogInfo(String service, Vertx vertx, JsonObject logInfol) {
        vertx.eventBus().send(service, logInfol);
    }

    private String getBossUrl(String opCode) {
        return environment.getProperty("boss.url", "http://172.30.87.62:9898/micservice/http/").concat(opCode);
    }

    @Override
    public String post(String appkey, String aeskey, String token, String opCode, JsonObject svcCont) {
        JsonObject remoteParam = new JsonObject();
        try {
            //报文头
            JsonObject tcpCont = new JsonObject();
            tcpCont.put("appKey", appkey);
            tcpCont.put("opCode", opCode);
            tcpCont.put("timeStamp", new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
            tcpCont.put("nonce", JsonUtil.generateRandomCode(8));
            tcpCont.put("sign", SHA1.getSHA1(token, tcpCont.getString("timeStamp"), tcpCont.getString("nonce"), tcpCont.getString("appKey"), tcpCont.getString("opCode")));

            remoteParam.put("tcpCont", tcpCont);
            //报文体
            remoteParam.put("svcCont", svcCont);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity reqEntity = new HttpEntity(remoteParam.toString(), headers);
            logger.info("Begin Request Boss {}", remoteParam.encode());
            Object ret = restTemplate.postForObject(getBossUrl(tcpCont.getString("opCode")), reqEntity, Object.class);
            if (ret != null) {
                JsonObject result = new JsonObject(Json.encode(ret));
                logger.info("Result Request Boss {}", result.encode());
                return result.encode();
            }
        } catch (Exception exception) {
            logger.error("Request Boss Error {}", exception.getMessage());
        }
        return null;
    }

    @Override
    public String pay(String url, JsonObject param) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity reqEntity = new HttpEntity(param.toString(), headers);
            logger.info("Begin Request Pay {}", param.encode());
            Object ret = restTemplate.postForObject(url, reqEntity, Object.class);
            if (ret != null) {
                JsonObject result = new JsonObject(Json.encode(ret));
                logger.info("Result Request Pay {}", result.encode());
                return result.encode();
            }
        } catch (Exception exception) {
            logger.error("Request Pay Error {}", exception.getMessage());
        }
        return null;
    }
}
