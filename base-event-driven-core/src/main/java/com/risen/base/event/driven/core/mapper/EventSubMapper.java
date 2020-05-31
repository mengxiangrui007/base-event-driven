package com.risen.base.event.driven.core.mapper;

import com.risen.base.event.driven.core.BaseMapper;
import com.risen.base.event.driven.core.model.EventSub;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventSubMapper extends BaseMapper<EventSub> {

    int updateEventStatusByPrimaryKeyInCasMode(@Param("id") Long id, @Param("expect") Integer expect, @Param("target") Integer target);

    List<EventSub> selectByEventStatus(@Param("eventStatus") Integer eventStatus);

    Set<EventSub> selectByIds(@Param("ids") Set<String> ids);
}