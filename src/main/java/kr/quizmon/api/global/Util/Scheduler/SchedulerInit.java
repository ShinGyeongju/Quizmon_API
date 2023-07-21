package kr.quizmon.api.global.Util.Scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;


@Configuration
@RequiredArgsConstructor
public class SchedulerInit {
    private final ApplicationContext applicationContext;

    @PostConstruct
    protected void init() throws SchedulerException {
        // Job 설정
        JobDetail job = JobBuilder.newJob(OrderByPopularity.class)
                .withIdentity("orderByNewestJob")
                .build();

        // Trigger 설정
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("orderByNewestTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();

        // 스케줄러 설정
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.setJobFactory(jobFactory);

        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

}
