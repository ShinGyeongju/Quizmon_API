package kr.quizmon.api.domain.quiz;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

@Entity(name = "tb_qna_sound")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@ToString
public class QnASoundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sound_id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizEntity quizEntity;

    @Column(name = "sequence_number")
    private short sequence_number;

    @Column(name = "sound_url")
    private String sound_url;

    @Type(StringArrayType.class)
    @Column(name = "options")
    private String[] options;

    @Type(StringArrayType.class)
    @Column(name = "answer")
    private String[] answer;
}
