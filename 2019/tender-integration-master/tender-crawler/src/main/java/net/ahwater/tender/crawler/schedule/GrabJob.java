package net.ahwater.tender.crawler.schedule;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.crawler.Crawler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by yyc on 2018/3/5 10:53.
 * Nothing is true but improving yourself.
 */
@Slf4j
@Component
public class GrabJob implements Job {

    @Autowired
    private Crawler crawler;

    public GrabJob() {
        // Instances of Job must have a public no-argument constructor.
    }

    @Override
    public void execute(JobExecutionContext context) {
        log.debug("Job run ... ");
        crawler.run();
//        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("tender.push.queue"), new Random().nextInt(100) + "");
//        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic("tender.items.topic"), new BeanItem().setId(1).setTitle("标题").setGrabTime(new Date()).toString());
//        List list = jmsMessagingTemplate.convertSendAndReceive("tender.test.queue", "111", List.class);
//        log.error(">>>" + list.toString());
    }

}