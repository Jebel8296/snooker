package com.mstx.framework.task.service;

import com.mstx.framework.task.dao.NetworkRechargeInfo;
import com.mstx.framework.task.dao.NetworkRechargeInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface INetworkRechargeInfoService {

    int insertSelective(NetworkRechargeInfo record);

    List<NetworkRechargeInfo> selectByExample(NetworkRechargeInfoExample example);

    NetworkRechargeInfo selectByPrimaryKey(String rid);

    int updateByExampleSelective(@Param("record") NetworkRechargeInfo record, @Param("example") NetworkRechargeInfoExample example);

    int updateByPrimaryKeySelective(NetworkRechargeInfo record);

}
