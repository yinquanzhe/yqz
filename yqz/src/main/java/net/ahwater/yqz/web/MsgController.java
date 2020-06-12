package net.ahwater.yqz.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Created by yqz on 2019/6/28
 */
@RestController
public class MsgController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @RequestMapping("/sendMsg")
    public void sendMsg(String msg) {
        jmsTemplate.send("q2", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                TextMessage textMessage = session.createTextMessage();
                textMessage.setText(msg);
                return textMessage;
            }
        });
    }
}