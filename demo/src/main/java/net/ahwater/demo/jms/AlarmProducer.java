package net.ahwater.demo.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

/**
 * Created by yqz on 2019/12/27
 */
@Component
public class AlarmProducer{

    @Autowired
    private JmsTemplate jmsTemplate;

    public  void sendMessage(Destination destination, String message){
        this.jmsTemplate.convertAndSend(destination,message);
    }
}