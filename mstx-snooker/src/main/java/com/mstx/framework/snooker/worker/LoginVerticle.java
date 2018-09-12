package com.mstx.framework.snooker.worker;

import com.mstx.framework.user.service.ISendSmsService;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.JsonUtil;
import com.mstx.framwork.common.util.SignUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LoginVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(LoginVerticle.class);

    @Autowired
    Environment environment;
    @Autowired
    ISendSmsService iSendSmsService;

    @Override
    public void start() throws Exception {
        JWTAuthOptions jwtAuthOptions = new JWTAuthOptions();
        jwtAuthOptions.setKeyStore(new KeyStoreOptions().setPath("keystore.jceks").setPassword("secret"));
        JWTAuth jwtAuth = JWTAuth.create(vertx, jwtAuthOptions);
        /** 用户登录 */
        vertx.eventBus().consumer("user.login", message -> {
            JsonObject param = ((JsonObject) message.body());
            try {
                logger.info("accept:" + param);
                if (!param.containsKey("phonenum")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (!param.containsKey("password")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                //1.调用BOSS验证服务密码
                JsonObject checkUserPasswordParam = new JsonObject();
                checkUserPasswordParam.put("serialNumber", param.getString("phonenum")).put("password", param.getString("password"));
                DeliveryOptions options = new DeliveryOptions();
                options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
                vertx.eventBus().send("boss.CheckUserPassword", checkUserPasswordParam, options, messageAsyncResult -> {
                    String reply = RespBodyBuilder.toError(RespCode.CODE_10508);
                    if (messageAsyncResult.succeeded()) {
                        JsonObject result = new JsonObject(messageAsyncResult.result().body().toString());
                        logger.info("【boss.CheckUserPassword】 result {}", result.encode());
                        if (result.containsKey("code") && result.getInteger("code").toString().equals("10200")) {
                            //2.验证成功，生成Token
                            String token = jwtAuth.generateToken(new JsonObject(), new JWTOptions().setExpiresInMinutes(Integer.valueOf(environment.getProperty("token.ExpiresInMinutes", "30"))));
                            reply = RespBodyBuilder.toSuccessWithObject(new JsonObject().put("token", token));
                        } else {
                            if (result.containsKey("data") && result.getJsonObject("data").getString("errorCode").equals("3456")) {//密码错误
                                reply = RespBodyBuilder.toError(RespCode.CODE_10507);
                            } else {
                                reply = RespBodyBuilder.toError(result.getJsonObject("data").getString("errorCode"), result.getJsonObject("data").getString("errorMsg"));
                            }
                        }
                    } else {
                        logger.error("调用【boss.CheckUserPassword】 失败 {}", messageAsyncResult.cause().getMessage());
                    }
                    message.reply(reply);
                    return;
                });
            } catch (Exception exception) {
                logger.error("error:" + exception.getMessage());
                message.reply(RespBodyBuilder.toError(RespCode.CODE_10501));
            }
        });
        /** 验证服务密码  */
        vertx.eventBus().consumer("login.VerifyUserPassword", message -> {
            JsonObject param = ((JsonObject) message.body());
            try {
                logger.info("accept:" + param);
                if (!param.containsKey("phonenum")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (!param.containsKey("password")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (param.containsKey("verifyCode")) {
                    if (!iSendSmsService.verifySmsCode(param)) {
                        message.reply(RespBodyBuilder.toError(RespCode.CODE_10509));
                        return;
                    }
                }
                //1.调用BOSS验证服务密码
                JsonObject checkUserPasswordParam = new JsonObject();
                checkUserPasswordParam.put("serialNumber", param.getString("phonenum")).put("password", param.getString("password"));
                DeliveryOptions options = new DeliveryOptions();
                options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
                vertx.eventBus().send("boss.CheckUserPassword", checkUserPasswordParam, options, messageAsyncResult -> {
                    String reply = RespBodyBuilder.toError(RespCode.CODE_10508);
                    if (messageAsyncResult.succeeded()) {
                        JsonObject result = new JsonObject(messageAsyncResult.result().body().toString());
                        logger.info("【boss.CheckUserPassword】 result {}", result.encode());
                        if (result.containsKey("code") && result.getInteger("code").toString().equals("10200")) {
                            reply = RespBodyBuilder.toCommonSuccess();
                        } else {
                            if (result.containsKey("data") && result.getJsonObject("data").getString("errorCode").equals("3456")) {//密码错误
                                reply = RespBodyBuilder.toError(RespCode.CODE_10507);
                            } else {
                                reply = RespBodyBuilder.toError(result.getJsonObject("data").getString("errorCode"), result.getJsonObject("data").getString("errorMsg"));
                            }
                        }
                    } else {
                        logger.error("调用【boss.CheckUserPassword】 失败 {}", messageAsyncResult.cause().getMessage());
                    }
                    message.reply(reply);
                });
            } catch (Exception exception) {
                logger.error("error:" + exception.getMessage());
                message.reply(RespBodyBuilder.toError(RespCode.CODE_10501));
            }
        });
        logger.info("LoginVerticle start success.");
    }
}
