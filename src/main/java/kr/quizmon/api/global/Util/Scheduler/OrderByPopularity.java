package kr.quizmon.api.global.Util.Scheduler;

import kr.quizmon.api.domain.quiz.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * 퀴즈 실시간 인기순 정렬 스케줄러
 */

@Slf4j
@RequiredArgsConstructor
public class OrderByPopularity implements Job {
    private final ScoreRepository scoreRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("[SCHEDULER] {} - Executed", this.getClass().getName());

        // 퀴즈 인기 점수 설정 쿼리
        scoreRepository.updateAllPopularityScore();
    }
}
