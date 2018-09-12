package com.mstx.framework.user.mapper;

import com.mstx.framework.user.dao.TestUser;
import com.mstx.framework.user.dao.TestUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestUserMapper {
    long countByExample(TestUserExample example);

    int deleteByExample(TestUserExample example);

    int deleteByPrimaryKey(Short id);

    int insert(TestUser record);

    int insertSelective(TestUser record);

    List<TestUser> selectByExample(TestUserExample example);

    TestUser selectByPrimaryKey(Short id);

    int updateByExampleSelective(@Param("record") TestUser record, @Param("example") TestUserExample example);

    int updateByExample(@Param("record") TestUser record, @Param("example") TestUserExample example);

    int updateByPrimaryKeySelective(TestUser record);

    int updateByPrimaryKey(TestUser record);
}