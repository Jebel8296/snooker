package com.mstx.framework.task.task;

import com.mstx.framework.task.dao.NetworkRechargeInfo;
import com.mstx.framework.task.dao.NetworkRechargeInfoExample;
import com.mstx.framework.task.service.INetworkRechargeInfoService;
import com.mstx.framwork.common.constant.Contants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.RxHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import rx.Scheduler;
import rx.functions.Action0;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务：
 * 提交充值订单到BOSS
 *
 * @author xuxg
 */
@Component
public class PayFeeSubmitTask extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(PayFeeSubmitTask.class);
    @Autowired
    Environment environment;
    @Autowired
    INetworkRechargeInfoService iNetworkRechargeInfoService;

    @Override
    public void start() throws Exception {
        super.start();
        // 任务延迟时间
        int initialDelay = Integer.valueOf(environment.getProperty("scheduler.delay", "20"));
        // 任务间隔时间
        int period = Integer.valueOf(environment.getProperty("scheduler.period", "600"));
        // 任务处理的数据条数
        int rows = Integer.valueOf(environment.getProperty("scheduler.rows", "10"));
        Scheduler scheduler = RxHelper.scheduler(vertx);
        scheduler.createWorker().schedulePeriodically(new Action0() {
            @Override
            public void call() {
                NetworkRechargeInfoExample networkRechargeInfoExample = new NetworkRechargeInfoExample();
                NetworkRechargeInfoExample.Criteria criteria = networkRechargeInfoExample.createCriteria();
                criteria.andStatusEqualTo(Contants.RECHARGE_UNRECHARE);
                criteria.andPayStatusEqualTo(Contants.RECHARGE_PAYED);
                networkRechargeInfoExample.setOrderByClause("CREATE_TIME DESC");
                List<NetworkRechargeInfo> networkRechargeInfoList = iNetworkRechargeInfoService.selectByExample(networkRechargeInfoExample);
                if (networkRechargeInfoList != null && networkRechargeInfoList.size() > 0) {
                    for (int i = 0; i < networkRechargeInfoList.size(); i++) {
                        if (i == rows) {
                            break;
                        }
                        NetworkRechargeInfo networkRechargeInfo = networkRechargeInfoList.get(i);
                        if (networkRechargeInfo.getPayChannel() == null || networkRechargeInfo.getPayChannel().equals("")) {
                            break;
                        }
                        String payChannelId = networkRechargeInfo.getPayChannel();
                        if (!environment.containsProperty(payChannelId)) {
                            break;
                        }
                        JsonObject param = new JsonObject();
                        param.put("phonenum", networkRechargeInfo.getPhonenum());
                        param.put("fee", networkRechargeInfo.getRecharge().toString());
                        param.put("feeTypeCode", "2000");
                        param.put("operType", "S");
                        param.put("orderCode", networkRechargeInfo.getRid());
                        param.put("payChannelId", payChannelId);
                        param.put("appkey", environment.getProperty(payChannelId));
                        vertx.eventBus().send("task.recharge", param);
                    }
                }
            }
        }, initialDelay, period, TimeUnit.SECONDS);
    }
}
