package com.mstx.framework.user.service;

import com.mstx.framework.user.dao.SendSmsInfo;
import com.mstx.framework.user.dao.SendSmsInfoExample;
import com.sun.media.sound.JARSoundbankReader;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface ISendSmsService {

    int insertSelective(SendSmsInfo record);

    List<SendSmsInfo> selectByExample(SendSmsInfoExample example);

    boolean verifySmsCode(JsonObject param);
}
