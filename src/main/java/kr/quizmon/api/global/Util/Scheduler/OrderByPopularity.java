package kr.quizmon.api.global.Util.Scheduler;

import kr.quizmon.api.domain.quiz.ScoreRepository;
import kr.quizmon.api.global.Util.RedisIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 퀴즈 실시간 인기순 정렬 스케줄러
 */

@Slf4j
@RequiredArgsConstructor
public class OrderByPopularity implements Job {
    private final ScoreRepository scoreRepository;
    private final RedisIO redisIO;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("[SCHEDULER] {} - Executed", this.getClass().getName());

        // 퀴즈 인기 점수 설정 쿼리
        List<ScoreRepository.ScoreDTO> score24CountList = scoreRepository.findAllOrderByPopularity();

//        // 새로운 Redis Key 생성
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
//        String newRedisKey = "popularityRanking" + dateFormat.format(new Date());

        // TODO: 삭제 후 저장하기 직전 타이밍에 접근하는 요청에 대한 처리 필요 (ex: 저장 후 삭제/Lock 등)

        // 기존 Redis의 SortedSet 삭제
        redisIO.deleteValue("popularityRanking");

        // Redis의 SortedSet에 저장
        redisIO.setPopularityRanking("popularityRanking", score24CountList);

    }
}
