package net.ahwater.main.accept;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.main.accept.handler.BaseHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Reeye on 2020/4/23 10:47
 * Nothing is true but improving yourself.
 */
@Slf4j
@Component
@RabbitListener(queues = DirectRabbitConfig.QUEUE_NAME, ackMode = "MANUAL")
public class MqListener {

    private final List<BaseHandler> handlers;

    @Autowired
    public MqListener(List<BaseHandler> handlers) {
        this.handlers = handlers;
    }

    @RabbitHandler(isDefault = true)
    public void recieve(String data, Channel channel, Message message) throws Exception {
        try {
            String[] arr = data.split(",");
            if (arr.length >= 5) {
                Number value = arr[3].contains(".") ? Double.parseDouble(arr[3]) : Integer.parseInt(arr[3]);
                MsgData msgData = new MsgData(arr[0], arr[1], arr[2], value, arr[4]);
                // TODO 获取站点信息

                boolean flag = false;
                for (BaseHandler handler : handlers) {
                    if (handler.support(msgData)) {
                        handler.handle(msgData);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    log.error("此消息未被处理: {}", data);
                }
//                switch (data.getType()) {
//                    case "R":
//
//                        break;
//                    case "S":
//
//                        break;
//                    case "D":
//
//                        break;
//                    default:
//                        log.warn("未处理的类型: {}", data.getType());
//                        break;
//                }
            } else {
                log.error("错误的消息格式: {}", data);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("receiver success: {}", data);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
            log.error("receiver fail: {}", data);
        }
    }

}
