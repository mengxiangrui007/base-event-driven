package com.risen.base.event.driven.core.mapper;

import com.risen.base.event.driven.core.BaseMapper;
import com.risen.base.event.driven.core.model.EventPub;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventPubMapper extends com.risen.base.event.driven.core.BaseMapper<EventPub> {
    /**
     * 获取定量的指定状态的事件群，
     */
    Set<EventPub> selectLimitedEntityByEventType(@Param("eventStatus") Integer eventStatus, @Param("eventGroup") String eventGroup,
                                                 @Param("eventService") String eventService, @Param("limited") int limited);

    Set<EventPub> selectByIds(@Param("ids") Set<String> ids);

    /**
     * 获取定量的、指定状态的、在指定更新时间前的事件群，用于Broker没有confirm某消息的情况下进行重新发送，目的是防止在单节点的情况因为意外宕机而导致PENDING事件没有转换为其他状态的能力。
     */
    Set<EventPub> selectLimitedEntityByEventTypeBeforeTheSpecifiedUpdateTime(@Param("eventStatus") Integer eventStatus, @Param("eventGroup") String eventGroup
            , @Param("eventService") String eventService, @Param("limited") int limited, @Param("beforeThisTime") OffsetDateTime beforeThisTime);

    /**
     * 使用乐观锁更新实体，目的防止是在多实例的情况下对同一记录重复更新的问题，该方法主要用于发往Broker前的事件状态更新所用
     */
    int updateByPrimaryKeySelectiveWithOptimisticLock(EventPub event);

    /**
     * 该方法目前主要用于处理basic.ack的状态处理，将PENDING转换为DONE或者NOT_FOUND时所用。本方法仅针对event_type的cas更新，目的同样是防止多实例的情况重复更新的问题，
     * 不适用乐观锁而是特定针对event_status的原因有：basic.ack仅会返回一次，除非重复发送相同的消息；乐观锁是针对所有字段，而本cas方法仅是针对event_status，具有更细颗粒的控制能力
     */
    int updateEventStatusByEventIdKeyInCasMode(@Param("eventId") String eventId, @Param("expect") Integer expect, @Param("target") Integer target);

    List<EventPub> selectByEventStatus(@Param("eventStatus") Integer eventStatus);

    EventPub selectByEventId(@Param("eventId") String eventId);
}