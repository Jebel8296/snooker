package com.mstx.framework.snooker.worker;

import com.mstx.framework.user.dao.SendSmsInfo;
import com.mstx.framework.user.service.ISendSmsService;
import com.mstx.framwork.common.response.RespBodyBuilder;
import com.mstx.framwork.common.response.RespCode;
import com.mstx.framwork.common.util.Base64Util;
import com.mstx.framwork.common.util.JsonUtil;
import com.mstx.framwork.common.util.VerifyCodeUtil;
import com.mstx.framwork.common.util.VertxSendFutureUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Component
public class VerifyCodeVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(VerifyCodeVerticle.class);

    @Autowired
    Environment environment;
    @Autowired
    ISendSmsService iSendSmsService;

    @Override
    public void start() throws Exception {

        /** 忘记密码  add:xioagao.xu  20180906 */
        vertx.eventBus().consumer("user.forgetpw", message -> {
            JsonObject param = ((JsonObject) message.body());
            try {
                logger.info("accept:" + param);
                if (!param.containsKey("phonenum")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (!param.containsKey("cardnum")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (!param.containsKey("verifyCode")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (!param.containsKey("password")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (!param.containsKey("surepassword")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                if (!param.getString("password").equals(param.getString("surepassword"))) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                //1.校验短信码是否一致
                if (!iSendSmsService.verifySmsCode(param)) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10509));
                    return;
                }
                DeliveryOptions options = new DeliveryOptions();
                options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
                //2.调用BOSS查询客户信息 验证手机号和身份证号是否一致
                CompletableFuture<String> qryCustInfoFuture = new CompletableFuture<>();
                JsonObject qryCustInfoParam = new JsonObject();
                qryCustInfoParam.put("qryMode", "0");
                qryCustInfoParam.put("serialNumber", param.getString("phonenum"));
                qryCustInfoParam.put("psptCheckCode", JsonUtil.subAfter8OfCard(param.getString("cardnum")));
                VertxSendFutureUtil.vertxSendFuture(vertx, "boss.QryCustInfo", qryCustInfoParam, options, qryCustInfoFuture);
                qryCustInfoFuture.whenComplete((d, ex) -> {
                    JsonObject jsonObject = new JsonObject(d);
                    logger.info("【boss.QryCustInfo】 result {}", jsonObject.encode());
                    if (!(jsonObject.containsKey("code") && jsonObject.getInteger("code").toString().equals("10200"))) {
                        message.reply(RespBodyBuilder.toError(jsonObject.getJsonObject("data").getString("errorCode"), jsonObject.getJsonObject("data").getString("errorMsg")));
                        return;
                    }
                    //3.验证成功，重置服务密码
                    CompletableFuture<String> changePasswordSubmitFuture = new CompletableFuture<>();
                    JsonObject changePasswordSubmit = new JsonObject();
                    changePasswordSubmit.put("serialNumber", param.getString("phonenum"));
                    changePasswordSubmit.put("newPassword", param.getString("password"));
                    changePasswordSubmit.put("isReset", "Y");//密码重置
                    VertxSendFutureUtil.vertxSendFuture(vertx, "boss.ChangePasswordSubmit", changePasswordSubmit, options, changePasswordSubmitFuture);
                    changePasswordSubmitFuture.whenComplete((dd, exx) -> {
                        JsonObject jsonObject2 = new JsonObject(dd);
                        logger.info("【boss.ChangePasswordSubmit】 result {}", jsonObject2.encode());
                        if (!(jsonObject2.containsKey("code") && jsonObject2.getInteger("code").toString().equals("10200"))) {
                            message.reply(RespBodyBuilder.toError(jsonObject2.getJsonObject("data").getString("errorCode"), jsonObject2.getJsonObject("data").getString("errorMsg")));
                            return;
                        }
                        message.reply(RespBodyBuilder.toCommonSuccess());
                        return;
                    });
                });
            } catch (Exception exception) {
                logger.error("error:" + exception.getMessage());
                message.reply(RespBodyBuilder.toError(RespCode.CODE_10501));
            }
        });

        /** 发送短信验证码 */
        vertx.eventBus().consumer("login.SendSms", message -> {
            JsonObject param = ((JsonObject) message.body());
            try {
                logger.info("accept:" + param);
                if (!param.containsKey("phonenum")) {
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10502));
                    return;
                }
                String verifyCode = VerifyCodeUtil.generateVerifyCode(4);
                String[] smsStemplate = environment.getProperty("sms.template", "【民生通讯】您的短信验证码为：&code&，验证码需要妥善保存请勿透漏给他人。").split("&");
                try {
                    SendSmsInfo sendSmsInfo = new SendSmsInfo();
                    sendSmsInfo.setMid(JsonUtil.getServiceIDBySef(2));
                    sendSmsInfo.setPhonenum(param.getString("phonenum"));
                    sendSmsInfo.setCreateTime(new Date());
                    sendSmsInfo.setVerfyCode(verifyCode);
                    sendSmsInfo.setSmsMsg(smsStemplate[0] + verifyCode + smsStemplate[2]);
                    iSendSmsService.insertSelective(sendSmsInfo);
                } catch (Exception ex) {
                    logger.error("error:" + ex.getMessage());
                    message.reply(RespBodyBuilder.toError(RespCode.CODE_10510));
                    return;
                }
                JsonObject sendSmsParam = new JsonObject();
                sendSmsParam.put("serialNumber", param.getString("phonenum")).put("msg", smsStemplate[0] + verifyCode + smsStemplate[2]);
                DeliveryOptions options = new DeliveryOptions();
                options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
                vertx.eventBus().send("boss.SendSms", sendSmsParam, options, messageAsyncResult -> {
                    String reply = RespBodyBuilder.toError(RespCode.CODE_10508);
                    if (messageAsyncResult.succeeded()) {
                        JsonObject result = new JsonObject(messageAsyncResult.result().body().toString());
                        logger.info("【boss.SendSms】 result {}", result.encode());
                        if (result.containsKey("code") && result.getInteger("code").toString().equals("10200")) {
                            reply = RespBodyBuilder.toCommonSuccess();
                        } else {
                            reply = RespBodyBuilder.toError(result.getJsonObject("data").getString("errorCode"), result.getJsonObject("data").getString("errorMsg"));
                        }
                    } else {
                        logger.error("调用【boss.SendSms】 失败 {}", messageAsyncResult.cause().getMessage());
                    }
                    message.reply(reply);
                });
            } catch (Exception exception) {
                logger.error("error:" + exception.getMessage());
                message.reply(RespBodyBuilder.toError(RespCode.CODE_10501));
            }
        });


        /** 获取 图片验证码 */
        vertx.eventBus().consumer("login.ImageCode", message -> {
            JsonObject param = ((JsonObject) message.body());
            try {
                logger.info("accept:" + param);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                int w = 200, h = 80;
                String verifyCode = VerifyCodeUtil.generateVerifyCode(4);
                VerifyCodeUtil.outputImage(w, h, output, verifyCode);
                String image = Base64Util.encode(output.toByteArray());
                message.reply(RespBodyBuilder.toSuccessWithObject(new JsonObject().put("image", image).put("code", verifyCode)));
            } catch (Exception exception) {
                logger.error("error:" + exception.getMessage());
                message.reply(RespBodyBuilder.toError(RespCode.CODE_10501));
            }
        });


        logger.info("VerifyCodeVerticle start success.");
    }
}
