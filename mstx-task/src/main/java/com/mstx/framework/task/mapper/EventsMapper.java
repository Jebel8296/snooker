package com.mstx.framework.task.mapper;

import com.mstx.framework.task.dao.Events;
import com.mstx.framework.task.dao.EventsExample;
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