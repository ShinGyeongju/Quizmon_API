package kr.quizmon.api.domain.quiz;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class QuizRepositoryImpl implements QuizRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<QuizEntity> findAllOrderByUpdated_at() {
        return null;
    }
}
