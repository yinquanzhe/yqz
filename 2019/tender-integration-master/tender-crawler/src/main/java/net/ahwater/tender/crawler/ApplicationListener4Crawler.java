package net.ahwater.tender.crawler;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.crawler.schedule.GrabJob;
import net.ahwater.tender.db.entity.BeanTime;
import net.ahwater.tender.db.mapper.TimeMapper;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Reeye on 2018/8/1 16:59
 * Nothing is true but improving yourself.
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ApplicationListener4Crawler implements ApplicationListener<ApplicationStartedEvent> {


    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TimeMapper timeMapper;

    @Autowired
    private Crawler crawler;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
//        configSchedule();
        crawler.run();
    }

    void configSchedule() {
        try {
            String hours = timeMapper.selectList(null)
                    .stream()
                    .filter(BeanTime::getStatus)
                    .map(BeanTime::getHour)
                    .sorted()
                    .map(e -> e + "")
                    .reduce("", (pre, v) -> pre + (pre.equals("") ? "" : ",") + v);
            if (hours.length() > 0) {
                String cron = "0 0 " + hours + " * * ?";
//                cron = "0/10 * * * * ?";   // 测试用
                scheduler.clear();
                JobDetail job = newJob(GrabJob.class).withIdentity("job2", "group2").build();
                CronTrigger trigger = newTrigger().withIdentity("trigger2", "group2")
                        .withSchedule(cronSchedule(cron))
                        .build();
                Date ft = scheduler.scheduleJob(job, trigger);
                log.info("定时抓取任务[{}]已设置在CRON: [{}], 于[{}]下次运行",
                        job.getKey(),
                        trigger.getCronExpression(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E").format(ft));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
