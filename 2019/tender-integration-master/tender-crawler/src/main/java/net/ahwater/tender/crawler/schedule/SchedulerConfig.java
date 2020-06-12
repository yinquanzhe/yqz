package net.ahwater.tender.crawler.schedule;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yyc on 2018/3/5 10:59.
 * Nothing is true but improving yourself.
 */
@Configuration
public class SchedulerConfig {

    @Autowired
    private JobFactory quartzJobFactory;

    @Bean
    public SchedulerFactory schedulerFactory() {
        return new StdSchedulerFactory();
    }

//    @Bean
//    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
//        return new ThreadPoolTaskScheduler();
//    }

    @Bean(name = "scheduler")
    public Scheduler scheduler(SchedulerFactory factoryBean) throws Exception {
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.setJobFactory(quartzJobFactory);
        scheduler.start();
        return scheduler;
    }

}
