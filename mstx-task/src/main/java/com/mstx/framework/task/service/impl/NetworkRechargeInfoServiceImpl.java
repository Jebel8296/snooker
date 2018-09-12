package com.mstx.framework.task.service.impl;

import com.mstx.framework.task.dao.NetworkRechargeInfo;
import com.mstx.framework.task.dao.NetworkRechargeInfoExample;
import com.mstx.framework.task.mapper.NetworkRechargeInfoMapper;
import com.mstx.framework.task.service.INetworkRechargeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkRechargeInfoServiceImpl implements INetworkRechargeInfoService {

    private static Logger logger = LoggerFactory.getLogger(NetworkRechargeInfoServiceImpl.class);

    @Autowired
    NetworkRechargeInfoMapper networkRechargeInfoMapper;

    @Override
    public int insertSelective(NetworkRechargeInfo record) {
        return networkRechargeInfoMapper.insertSelective(record);
    }

    @Override
    public List<NetworkRechargeInfo> selectByExample(NetworkRechargeInfoExample example) {
        return networkRechargeInfoMapper.selectByExample(example);
    }

    @Override
    public NetworkRechargeInfo selectByPrimaryKey(String rid) {
        return networkRechargeInfoMapper.selectByPrimaryKey(rid);
    }

    @Override
    public int updateByExampleSelective(NetworkRechargeInfo record, NetworkRechargeInfoExample example) {
        return networkRechargeInfoMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(NetworkRechargeInfo record) {
        return networkRechargeInfoMapper.updateByPrimaryKeySelective(record);
    }
}
