package kr.quizmon.api.domain.quiz;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class QuizRepositoryImpl implements QuizRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QQuizEntity quiz = QQuizEntity.quizEntity;

    @Override
    public List<QuizDTO.GetListResponse.Quiz> findAllOrderByUpdated_at() {
        return queryFactory
                .select(Projections.fields(QuizDTO.GetListResponse.Quiz.class,
                        quiz.quizId,
                        quiz.urlId.as("urlId"),
                        quiz.title,
                        quiz.description.as("comment"),
                        quiz.type,
                        quiz.thumbnail_url.as("thumbnailUrl"),
                        quiz.limit_time.as("limitTime"),
                        quiz.play_count.as("playCount"),
                        quiz.report_count.as("reportCount"),
                        quiz.updated_at.as("timeStamp")))
                .from(quiz)
                .where()
                .orderBy()
                .fetch();
    }

    private BooleanExpression eqTimeStamp(LocalDateTime timeStamp) {
        return timeStamp == null ? null : quiz.updated_at.after(timeStamp);
    }

    private BooleanExpression eqType(String type) {
        return type == null ? null : quiz.type.eq(type);
    }

    private BooleanExpression eqAccess(Boolean access) {
        return access == null ? null : quiz.public_access.eq(access);
    }

    private BooleanExpression containsTitle(String word) {
        return word == null ? null : quiz.title.contains(word);
    }

    private BooleanExpression eqUser(UUID userPk) {
        return userPk == null ? null : quiz.userEntity.user_pk.eq(userPk);
    }

    private OrderSpecifier<?> sortQuiz(Sort.Order order) {
        Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
        return switch (order.getProperty()) {
            case "updated_at" -> new OrderSpecifier<>(direction, quiz.updated_at);
            case "play_count" -> new OrderSpecifier<>(direction, quiz.play_count);
            default -> new OrderSpecifier<>(Order.DESC, quiz.updated_at);
        };
    }


}
