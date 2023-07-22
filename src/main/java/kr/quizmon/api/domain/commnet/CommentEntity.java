package kr.quizmon.api.domain.commnet;

import jakarta.persistence.*;
import kr.quizmon.api.domain.quiz.QuizEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity(name = "tb_comment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@ToString(exclude = "quizEntity")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizEntity quizEntity;

    @Column(name = "score")
    private short score;

    @Column(name = "content")
    private String content;

    @Column(name = "report_count")
    private short report_count;

    @Column(name = "created_at")
    private LocalDateTime created_at;

}
