package com.mstx.framework.task.worker;

import com.mstx.framework.task.dao.NetworkRechargeInfo;
import com.mstx.framework.task.service.INetworkRechargeInfoService;
import com.mstx.framwork.common.constant.Contants;
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

import java.util.Date;

@Component
public class RechargeTaskVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(RechargeTaskVerticle.class);
    @Autowired
    Environment environment;
    @Autowired
    INetworkRechargeInfoService iNetworkRechargeInfoService;

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer("task.recharge", message -> {
            JsonObject param = ((JsonObject) message.body());
            try {
                logger.info("accept:" + param);
                JsonObject payFeeSubmitParam = new JsonObject();
                payFeeSubmitParam.put("serialNumber", param.getString("phonenum"));
                payFeeSubmitParam.put("remark", param.getString("phonenum") + "充值");
                payFeeSubmitParam.put("fee", param.getString("fee"));
                payFeeSubmitParam.put("feeTypeCode", "2000");
                payFeeSubmitParam.put("operType", "S");
                payFeeSubmitParam.put("payOrderNumber", param.getString("orderCode"));
                payFeeSubmitParam.put("payChannelId", param.getString("payChannelId"));
                payFeeSubmitParam.put("appkey", param.getString("appkey"));
                DeliveryOptions options = new DeliveryOptions();
                options.setSendTimeout(Integer.valueOf(environment.getProperty("vertx.sendimeout", "10000")));
                vertx.eventBus().send("boss.PayFeeSubmit", payFeeSubmitParam, options, messageAsyncResult -> {
                    String reply = RespBodyBuilder.toError(RespCode.CODE_10508);
                    if (messageAsyncResult.succeeded()) {
                        JsonObject result = new JsonObject(messageAsyncResult.result().body().toString());
                        logger.info("【boss.PayFeeSubmit】 result {}", result.encode());
                        try {
                            NetworkRechargeInfo networkRechargeInfo = new NetworkRechargeInfo();
                            networkRechargeInfo.setRid(param.getString("orderCode"));
                            if (result.containsKey("code") && result.getInteger("code").toString().equals("10200")) {
                                networkRechargeInfo.setStatus(Contants.RECHARGE_RECHARGED);
                                networkRechargeInfo.setRemark(result.getJsonObject("data").getString("tradeId"));
                                logger.info(networkRechargeInfo.getRid() + "缴费成功");
                            } else {
                                networkRechargeInfo.setStatus(Contants.RECHARGE_RECHARGERRO);
                                networkRechargeInfo.setRemark(result.encode());
                                logger.info(networkRechargeInfo.getRid() + "缴费失败");
                            }
                            networkRechargeInfo.setUpdateTime(new Date());
                            iNetworkRechargeInfoService.updateByPrimaryKeySelective(networkRechargeInfo);
                            reply = RespBodyBuilder.toCommonSuccess();
                        } catch (Exception ex) {
                            logger.error(messageAsyncResult.cause().getMessage());
                        }
                    } else {
                        logger.error("调用【boss.PayFeeSubmit】 失败 {}", messageAsyncResult.cause().getMessage());
                    }
                    message.reply(reply);
                });
            } catch (Exception exception) {
                logger.error("error:" + exception.getMessage());
                message.reply(RespBodyBuilder.toError(RespCode.CODE_10501));
            }
        });
    }
}
