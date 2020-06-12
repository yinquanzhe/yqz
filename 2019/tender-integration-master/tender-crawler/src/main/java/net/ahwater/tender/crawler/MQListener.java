package net.ahwater.tender.crawler;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.BeanModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/**
 * Created by Reeye on 2018/11/19 19:59
 * Nothing is true but improving yourself.
 */
@Slf4j
@Component
public class MQListener {

    @Autowired
    private ApplicationListener4Crawler listener4Crawler;

    @Autowired
    private Crawler crawler;

    @JmsListener(destination = Constants.TIME_QUEUE)
    public void receiveMsg(final TextMessage msg, Session session) {
        try {
            log.debug("收到[{}]消息: {}", Constants.TIME_QUEUE, msg.getText());
            listener4Crawler.configSchedule();
            msg.acknowledge();
        } catch (Exception e) {
            try {
                session.recover();
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
            log.error(e.getMessage());
        }
    }

    @JmsListener(destination = Constants.TEST_QUEUE)
    public List receiveMsg2(final ObjectMessage msg, Session session) {
        try {
            msg.acknowledge();
            BeanModule module = (BeanModule) msg.getObject();
            log.debug("QUEUE[{}] 收到消息: {}", Constants.TEST_QUEUE, module);
            if (module != null) {
                return crawler.runModuleTest(module);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
