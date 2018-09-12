package com.mstx.framework.user.service;

import com.mstx.framework.user.dao.TestUser;
import com.mstx.framework.user.dao.TestUserExample;

import java.util.List;

public interface ITestUserService {

    List<TestUser> selectByExampleForTestUser(TestUserExample example);
}
