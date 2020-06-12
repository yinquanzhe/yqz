package net.ahwater.tender.wx.core;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.BeanUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * Created by Reeye on 2018/12/11 11:22
 * Nothing is true but improving yourself.
 */
@Slf4j
@Component
public class PushScheduleListener {

    @Autowired
    private PushScheduler scheduler;

    @JmsListener(destination = Constants.PUSH_REMOVE, containerFactory = "jmsListenerContainerFactory4Queue")
    public void receiveRemoveMsg(final ObjectMessage msg, Session session) {
        try {
            BeanUser user = (BeanUser) msg.getObject();
            log.debug("收到[{}]消息: {}", Constants.PUSH_REMOVE, user);
            scheduler.removeTask(user);
            msg.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
            try {
                session.recover();
            } catch (JMSException e1) {
                log.error(e1.getMessage());
            }
        }
    }

    @JmsListener(destination = Constants.PUSH_ADD, containerFactory = "jmsListenerContainerFactory4Queue")
    public void receiveAddMsg(final ObjectMessage msg, Session session) {
        try {
            BeanUser user = (BeanUser) msg.getObject();
            log.debug("收到[{}]消息: {}", Constants.PUSH_ADD, user);
            scheduler.newOrUpdateTask(user);
            msg.acknowledge();
        } catch (JMSException e) {
            log.error(e.getMessage());
            try {
                session.recover();
            } catch (JMSException e1) {
                log.error(e1.getMessage());
            }
        }
    }

}
