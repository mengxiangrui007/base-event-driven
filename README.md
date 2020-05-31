# 事件驱动SDK

## 项目说明
1. 解决消息有序、不丢、重复
2. 实现思路当发送消息时持久化消息与本地事务绑定

## 规范说明
1. eventType 事件类型规范标准 组织:服务:事件类型 例如 risen:user-service:create_user 基础架构:用户服务:创建用户事件
2. eventContextKey 事件聚合根ID 消息基础设施保证相同的ContextKey下事件有序 可以为空

## 快速使用
1.kafka支持

```xml
        <dependency>
            <groupId>base-event-driven</groupId>
            <artifactId>base-event-driven-starter-kafka</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
```
2.发送统一事件格式
```json
{
	"eventId": "7b5266bf-6488-4bf3-a889-d920f01d5ff1",
	"eventType": "risen:event-driven:create_user",
	"eventOccurTime": "2019-10-21T02:53:11.000Z",
	"eventContextKey": "order:1",
	"metadata": {},
	"payloadData": {
		"id": 1,
		"name": "Shan rui",
		"cdate": 1571626390504
	}
}
```

```java
/**
 * 事件
 *
 * @author mengxr
 */
public class Event {
    /**
     * 事件ID 事件标识ID，用于标识单个服务内的唯一一条事件，可通过UUID生成器生成
     */
    private String eventId;
    /**
     * 事件类型 业务标记事件类型，例如订单创建事件可以表示为“order-service:OrderCreated” JSON表示使用ISO8601格式
     */
    private String eventType;
    /**
     * 事件发生时间，使用事件产生的时间 JSON表示使用ISO8601格式
     */
    @com.fasterxml.jackson.annotation.JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date eventOccurTime;

    /**
     * 事件聚合key 事件所属的聚合根对象的类型和ID，消息基础设施保证相同的ContextKey下事件有序
     */
    private String eventContextKey;
    /**
     * 事件扩展信息字段，可用于扩展事件
     */
    private Map<String, String> metadata;
    /**
     * 事件内容
     * 事件载荷信息，需要通过事件消息传递的关键信息
     * <p>
     * 不需要事件载荷时传递null
     * <p>
     * 不建议发布载荷为null的事件
     */
    private Object payloadData;
}
```
## 使用说明
### 初始化领域事件表
```sql
-- 事件消息发布
CREATE TABLE `event_pub` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` varchar(64) NOT NULL DEFAULT '' COMMENT '事件ID,用于消费者作去重',
  `event_occur_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '事件发生时间',
  `event_group` varchar(50) NOT NULL DEFAULT '' COMMENT '事件源服务分组',
  `event_service` varchar(50) NOT NULL DEFAULT '' COMMENT '事件源服务',
  `event_status` int(4) NOT NULL DEFAULT '0' COMMENT '事件状态 -128为未知错误, -3为NOT_FOUND(找不到exchange), -2为NO_ROUTE(找到exchange但是找不到queue), -1为FAILED(如类型尚未注册等的业务失败), 0为NEW(消息落地), 1为PENDING, 2为DONE',
  `event_context_key` varchar(256) NOT NULL DEFAULT '' COMMENT '事件聚合根key',
  `event_type` varchar(64) NOT NULL DEFAULT '' COMMENT '事件类型，业务标记事件类型，事件类型协议标准 组织:服务:事件事件 例如 risen:event-driven:create_user	',
  `metadata` varchar(512) NOT NULL DEFAULT '' COMMENT '事件扩展信息字段，可用于扩展事件',
  `payload_data` varchar(10240) NOT NULL DEFAULT '' COMMENT '事件内容',
  `lock_version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '锁版本号',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_event_id_group_service` (`event_id`,`event_group`,`event_service`),
  KEY `idx_status_group_service` (`event_status`,`event_group`,`event_service`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件消息发布'; 
-- 事件消息订阅
CREATE TABLE `event_sub` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` varchar(64) NOT NULL DEFAULT '' COMMENT '事件ID,用于消费者作去重',
  `event_occur_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '事件发生时间',
  `event_group` varchar(50) NOT NULL DEFAULT '' COMMENT '事件源服务分组',
  `event_service` varchar(50) NOT NULL DEFAULT '' COMMENT '事件源服务',
  `event_status` int(4) NOT NULL DEFAULT '0' COMMENT '事件状态 -128为未知错误, -3为NOT_FOUND(找不到exchange), -2为NO_ROUTE(找到exchange但是找不到queue), -1为FAILED(如类型尚未注册等的业务失败), 0为NEW(消息落地), 1为PENDING, 2为DONE',
  `event_context_key` varchar(256) NOT NULL DEFAULT '' COMMENT '事件聚合根key',
  `event_type` varchar(64) NOT NULL DEFAULT '' COMMENT '事件类型，业务标记事件类型，事件类型协议标准 组织:服务:事件事件 例如 risen:event-driven:create_user	',
  `metadata` varchar(512) NOT NULL DEFAULT '' COMMENT '事件扩展信息字段，可用于扩展事件',
  `payload_data` varchar(10240) NOT NULL DEFAULT '' COMMENT '事件内容',
  `lock_version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '锁版本号',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_event_id_group_service` (`event_id`,`event_group`,`event_service`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='事件消息订阅';

```
注意：需要保证mybatis可以扫描到事件驱动Mapper的mapper/*.xml路径 配置如下
```
mybatis.mapperLocations=classpath:mapper/*.xml
```

### 注册领域事件业务
```
    @PostConstruct
    public void postProcess() {
        EventDrivenPublisher.registerType(new String[]{UserStatus.CREATE_USER_EVENT, UserStatus.MODIFY_USER_EVENT}, new KafkaMessageRoute(USER_DRIVEN_TOPIC));
    }
```

### 发布领域事件
```
     @Autowired
     private EventDrivenPublisher eventDrivenPublisher;

    /**
       * 消息落地
       *
       * @param payload         消息体
       * @param businessType    业务类型
       * @param eventContextKey 聚合根
       * @return
       */
    eventDrivenPublisher.persistPublishMessage(messageCase, CREATE_USER_EVENT, "user" + ":" + messageCase.getId());
```

### 订阅领域事件
```
       @Autowired
       private EventDrivenSubscriber eventDrivenSubscriber;        

       @KafkaListener(topics = {EVENT_DRIVEN_TOPIC})
       public void listen(ConsumerRecord<?, ?> record) {
           Optional<?> kafkaMessage = Optional.ofNullable(record.value());
           if (kafkaMessage.isPresent()) {
   
               Object message = kafkaMessage.get();
               eventDrivenSubscriber.persistAndHandleMessage((String) message);
           }
       }
```

#### 实现事件处理Handler
@EventType 为事件类型 bizType 业务类型 serverName 生产方服务名称 可通过Eureka查看服务名称 

`注意1.0.3版本以前需要由继承EventHandlerAdapter升级为实现DomainEventHandler接口`

```
@EventType(bizType = "create_user",serverName = "event-driven")
@Component
public class UserEventEventHandler implements DomainEventHandler<User>{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventEventHandler.class);

    @Override
    public void handleBiz(User payloadData, String eventContextKey) {
        LOGGER.info("UserEventEventHandler payloadData[{}] eventContextKey[{}]", payloadData, eventContextKey);
//        throw new RuntimeException("test Exception");
    }
}

```

## 配置说明(以下配置默认不需要添加)

```yml
event:
  driven:
    orgName: risen #事件发送组织名称 默认risen
    serverName: application #事件服务名称 默认会获取spring的应用名称
    serverGroup: default #事件服务分组名称 默认为default 为了区分相同数据库下多环境服务事件抢渡发送问题
    sameEnvLimit: false #1.0.4版本添加，消息环境自动隔离功能，开启后消费者只消费同一环境生产者的消息 默认false不开启 注意使用此功能，不同环境的消费者，groupId也需要不一样
    publisherSkip: false #是否跳过事件发送器注入 默认为false不跳过
    subscriberSkip: false #是否跳过事件接收器注入 默认为false不跳过
```

## 附加功能

### 事件发送、消费回调勾子 业务方可根据需要进行扩展

```
/**
 * 事件发送钩子
 *
 * @author mengxr
 */
public interface EventDrivenPubHook {
    /**
     * 发送成功
     */
    void pubSuccess(EventPub eventPub);

    /**
     * 发送失败
     */
    void pubError(EventPub eventPub, Exception e);
}

```

```
/**
 * 事件消费钩子
 *
 * @author mengxr
 */
public interface EventDrivenSubHook {
    /**
     * 接收成功
     */
    void subSuccess(EventSub eventSub);

    /**
     * 接收失败
     */
    void subError(EventSub eventSub, Exception e);
}
```



