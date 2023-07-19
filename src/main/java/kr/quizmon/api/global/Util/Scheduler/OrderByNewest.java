package kr.quizmon.api.global.Util.Scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class OrderByNewest implements Job {
    //private final

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println( new Date() + "@@@@@ Quartz TEST @@@@@");

        //log.info("[SCHEDULER] {} - ", this.getClass().getName());





    }
}
