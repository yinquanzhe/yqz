package net.ahwater.demo.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by yqz on 2019/12/27
 */
@Component
public class AlarmConsumer{

    // 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
    @JmsListener(destination = "mytest.queue")  // @SendTo("out.queue") 为了实现双向队列
    @SendTo("out.queue")
    public void receiveQueue(String text) {
        if(StringUtils.isEmpty(text)){
            System.out.println("AlarmConsumer收到的报文为:"+text);
            System.out.println("把报警信息["+text+"]发送邮件给xxx");
            System.out.println("把报警信息["+text+"]发送短信给xxx");
            System.out.println("");
        }
    }
}