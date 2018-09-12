package com.mstx.framework.user.service.impl;

import com.mstx.framework.user.dao.TestUser;
import com.mstx.framework.user.dao.TestUserExample;
import com.mstx.framework.user.mapper.TestUserMapper;
import com.mstx.framework.user.service.ITestUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestUserServiceImpl implements ITestUserService {

    private static Logger logger = LoggerFactory.getLogger(TestUserServiceImpl.class);

    @Autowired
    TestUserMapper testUserMapper;

    @Override
    public List<TestUser> selectByExampleForTestUser(TestUserExample example) {
        return this.testUserMapper.selectByExample(example);
    }
}
