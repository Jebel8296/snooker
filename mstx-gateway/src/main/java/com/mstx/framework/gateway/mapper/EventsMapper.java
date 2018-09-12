package com.mstx.framework.gateway.mapper;

import com.mstx.framework.gateway.dao.Events;
import com.mstx.framework.gateway.dao.EventsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EventsMapper {
    long countByExample(EventsExample example);

    int deleteByExample(EventsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Events record);

    int insertSelective(Events record);

    List<Events> selectByExample(EventsExample example);

    int updateByExampleSelective(@Param("record") Events record, @Param("example") EventsExample example);

    int updateByExample(@Param("record") Events record, @Param("example") EventsExample example);
}