package com.mstx.framework.task.mapper;

import com.mstx.framework.task.dao.NetworkRechargeInfo;
import com.mstx.framework.task.dao.NetworkRechargeInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NetworkRechargeInfoMapper {
    long countByExample(NetworkRechargeInfoExample example);

    int deleteByExample(NetworkRechargeInfoExample example);

    int deleteByPrimaryKey(String rid);

    int insert(NetworkRechargeInfo record);

    int insertSelective(NetworkRechargeInfo record);

    List<NetworkRechargeInfo> selectByExample(NetworkRechargeInfoExample example);

    NetworkRechargeInfo selectByPrimaryKey(String rid);

    int updateByExampleSelective(@Param("record") NetworkRechargeInfo record, @Param("example") NetworkRechargeInfoExample example);

    int updateByExample(@Param("record") NetworkRechargeInfo record, @Param("example") NetworkRechargeInfoExample example);

    int updateByPrimaryKeySelective(NetworkRechargeInfo record);

    int updateByPrimaryKey(NetworkRechargeInfo record);
}