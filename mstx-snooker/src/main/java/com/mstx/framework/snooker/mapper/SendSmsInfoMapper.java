package com.mstx.framework.user.mapper;

import com.mstx.framework.user.dao.SendSmsInfo;
import com.mstx.framework.user.dao.SendSmsInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SendSmsInfoMapper {
    long countByExample(SendSmsInfoExample example);

    int deleteByExample(SendSmsInfoExample example);

    int deleteByPrimaryKey(String mid);

    int insert(SendSmsInfo record);

    int insertSelective(SendSmsInfo record);

    List<SendSmsInfo> selectByExample(SendSmsInfoExample example);

    SendSmsInfo selectByPrimaryKey(String mid);

    int updateByExampleSelective(@Param("record") SendSmsInfo record, @Param("example") SendSmsInfoExample example);

    int updateByExample(@Param("record") SendSmsInfo record, @Param("example") SendSmsInfoExample example);

    int updateByPrimaryKeySelective(SendSmsInfo record);

    int updateByPrimaryKey(SendSmsInfo record);
}