package kr.quizmon.api.global.Util.Scheduler;

import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
public class SchedulerInit {

    @PostConstruct
    protected void init() throws SchedulerException {
        // Job 설정
        JobDetail job = JobBuilder.newJob(OrderByNewest.class)
                .withIdentity("orderByNewestJob")
                .build();

        // Trigger 설정
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("orderByNewestTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();

        // 스케줄러 설정
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();

        scheduler.scheduleJob(job, trigger);
    }

}
