package com.jzo2o.foundations.handler;

import com.jzo2o.canal.listeners.AbstractCanalRabbitMqMsgListener;
import com.jzo2o.es.core.ElasticSearchTemplate;
import com.jzo2o.foundations.model.domain.ServeSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Gods
 * @Date 2025/6/15 17:07
 * @description 为服务项目表添加数据同步功能
 */

@Component
@Slf4j
public class ServeCanalDataSyncHandler extends AbstractCanalRabbitMqMsgListener<ServeSync> {


    @Resource
    private ElasticSearchTemplate elasticSearchTemplate;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "canal-mq-jzo2o-foundations", arguments = {@Argument(name = "x-single-active-consumer", value = "true", type = "java.lang.Boolean")}),
            exchange = @Exchange(name = "exchange.canal-jzo2o", type = ExchangeTypes.TOPIC),
            key = "canal-mq-jzo2o-foundations"),
            // 为处理消息的线程数
            concurrency = "1"
    )
    public void onMessage(Message message) throws Exception {
        // 解析消息
        parseMsg(message);
    }

    /**
     * 为服务项目表添加数据同步功能
     *
     * @param data 待同步的数据
     */
    @Override
    public void batchSave(List<ServeSync> data) {
        Boolean serveAggregation = elasticSearchTemplate.opsForDoc().batchInsert("serve_aggregation", data);
        if (!serveAggregation) {
            // 睡眠一秒钟
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.error("数据同步失败");
            throw new RuntimeException("数据同步失败");
        }
    }

    /**
     * 为服务项目表删除数据同步功能
     *
     * @param ids 删除的数据
     */
    @Override
    public void batchDelete(List<Long> ids) {
        Boolean serveAggregation = elasticSearchTemplate.opsForDoc().batchDelete("serve_aggregation", ids);
        if (!serveAggregation) {
            // 睡眠一秒钟
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.error("数据同步失败");
            throw new RuntimeException("数据同步失败");
        }

    }
}
