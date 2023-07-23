package kr.quizmon.api.domain.commnet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.quizmon.api.domain.quiz.QuizEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDTO {
    @Getter
    @Builder
    public static class CreateRequest {
        @Setter
        private String quizId;

        @NotNull(message = "유효하지 점수입니다.")
        private short score;

        @NotBlank(message = "유효하지 않은 내용입니다.")
        @Size(max = 50, message = "내용은 최대 50글자 까지만 가능합니다.")
        private String content;

        public CommentEntity toEntity(QuizEntity quiz) {
            return CommentEntity.builder()
                    .quizEntity(quiz)
                    .score(score)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetListRequest {
        @Setter
        private String quizId;

        @NotNull(message = "유효하지 않은 페이지입니다.")
        private int page;

        @Positive(message = "유효하지 않은 댓글 개수입니다.")
        private Integer count;
    }

    @Getter
    @Builder
    public static class CommonRequest {
        private String userId;
        private String commentId;
        private String quizId;
    }


    @Getter
    @Builder
    public static class CommonResponse {
        private String commentId;
    }

    @Getter
    @Builder
    public static class GetListResponse {
        private int totalPage;
        private int currentPage;
        private int countPerPage;
        private Comment[] commentArray;

        @Getter
        @Builder
        public static class Comment {
            private String commentId;
            private short score;
            private String content;
            private LocalDateTime createdAt;
        }
    }

}
