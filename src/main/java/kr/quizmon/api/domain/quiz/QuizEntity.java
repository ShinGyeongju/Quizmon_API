package kr.quizmon.api.domain.quiz;

import jakarta.persistence.*;
import kr.quizmon.api.domain.user.UserEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "tb_quiz")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@ToString(exclude = "userEntity")
public class QuizEntity {
    @Id
    @Column(name = "quiz_id")
    private UUID quizId;

    @ManyToOne
    @JoinColumn(name = "user_pk")
    private UserEntity userEntity;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "thumbnail_url")
    private String thumbnail_url;

    @Column(name = "limit_time")
    private short limit_time;

    @Column(name = "public_access")
    private boolean public_access;

    @Column(name = "random_question")
    private boolean random_question;

    @Column(name = "multiple_choice")
    private boolean multiple_choice;

    @Column(name = "url_id")
    private String url_id;

    @Column(name = "question_count")
    private int question_count;

    @Column(name = "play_count")
    private int play_count;

    @Column(name = "report_count")
    private int report_count;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @OneToMany(mappedBy = "quizEntity")
    private List<QnAImageEntity> qnAImageEntities = new ArrayList<>();

    @OneToMany(mappedBy = "quizEntity")
    private List<QnASoundEntity> qnASoundEntities = new ArrayList<>();


    public void updateQuiz(QuizDTO.UpdateRequest request) {
        this.title = request.getTitle();
        this.description = request.getComment();
        this.limit_time = request.getLimitTime();
        this.public_access = request.isPublicAccess();
        this.random_question = request.isRandomQuestion();
        this.multiple_choice = request.isMultipleChoice();
    }
}
