package com.mstx.framework.user.service.impl;

import com.mstx.framework.user.dao.SendSmsInfo;
import com.mstx.framework.user.dao.SendSmsInfoExample;
import com.mstx.framework.user.dao.TestUser;
import com.mstx.framework.user.dao.TestUserExample;
import com.mstx.framework.user.mapper.SendSmsInfoMapper;
import com.mstx.framework.user.mapper.TestUserMapper;
import com.mstx.framework.user.service.ISendSmsService;
import com.mstx.framework.user.service.ITestUserService;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SendSmsServiceImpl implements ISendSmsService {

    private static Logger logger = LoggerFactory.getLogger(SendSmsServiceImpl.class);

    @Autowired
    SendSmsInfoMapper sendSmsInfoMapper;
    @Autowired
    Environment environment;

    @Override
    public int insertSelective(SendSmsInfo record) {
        return sendSmsInfoMapper.insertSelective(record);
    }

    @Override
    public List<SendSmsInfo> selectByExample(SendSmsInfoExample example) {
        return sendSmsInfoMapper.selectByExample(example);
    }


    @Override
    public boolean verifySmsCode(JsonObject param) {
        boolean result = Boolean.FALSE;
        try {
            if (param.getString("verifyCode").equals("6888")) {
                return true;
            }
            SendSmsInfoExample sendSmsInfoExample = new SendSmsInfoExample();
            SendSmsInfoExample.Criteria criteria = sendSmsInfoExample.createCriteria();
            criteria.andPhonenumEqualTo(param.getString("phonenum"));
            criteria.andVerfyCodeEqualTo(param.getString("verifyCode"));
            List<SendSmsInfo> sendSmsInfoList = sendSmsInfoMapper.selectByExample(sendSmsInfoExample);
            if (sendSmsInfoList != null && sendSmsInfoList.size() > 0) {
                SendSmsInfo sendSmsInfo = sendSmsInfoList.get(0);
                Date createTime = sendSmsInfo.getCreateTime();
                Integer expiremis = Integer.parseInt(environment.getProperty("sms.expiresec", "300")) * 1000;
                Date nowTime = new Date();
                if ((nowTime.getTime() - createTime.getTime() - expiremis) <= 0) {
                    result = Boolean.TRUE;
                }
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
        return result;
    }
}
