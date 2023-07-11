package kr.quizmon.api.domain.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.quizmon.api.domain.user.UserEntity;
import lombok.*;

import java.util.List;
import java.util.UUID;

public class QuizDTO {
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @Setter
        private String userId;

        @Setter
        private UUID quizId;

        @NotBlank(message = "유효하지 않은 제목입니다.")
        @Size(max = 30, message = "제목은 최대 30글자 까지만 가능합니다.")
        private String title;

        @Size(max = 100, message = "설명은 최대 100글자 까지만 가능합니다.")
        private String comment;

        @NotNull(message = "유효하지 않은 제한 시간입니다.")
        private short limitTime;

        @NotNull(message = "유효하지 않은 대표 이미지 여부입니다.")
        private boolean thumbnail;

        @NotNull(message = "유효하지 않은 공개 여부입니다.")
        private boolean publicAccess;

        @NotNull(message = "유효하지 않은 랜덤 출제 여부입니다.")
        private boolean randomQuestion;

        @NotNull(message = "유효하지 않은 사지선다 여부입니다.")
        private boolean multipleChoice;

        @NotBlank(message = "유효하지 않은 인증 본문입니다.")
        private String signatureMessage;

        @NotNull(message = "유효하지 않은 정답 배열입니다.")
        private QnA[] qnaArray;

        @Getter
        public static class QnA {
            @Size(max = 4, message = "보기는 최대 4개 까지만 가능합니다.")
            private String[] optionArray;

            @NotEmpty(message = "유효하지 않은 정답 배열입니다.")
            private String[] answerArray;
        }

        public CreateRedis toRedisEntity(String thumbnailUrl, List<QnAImageEntity> images) {
            return CreateRedis.builder()
                    .quizId(quizId)
                    .title(title)
                    .comment(comment)
                    .type("IMAGE")
                    .limitTime(limitTime)
                    .thumbnailUrl(thumbnailUrl)
                    .publicAccess(publicAccess)
                    .randomQuestion(randomQuestion)
                    .multipleChoice(multipleChoice)
                    .questionCount(images.size())
                    .imageList(images)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRedis {
        private UUID quizId;
        private String title;
        private String comment;
        private String type;
        private short limitTime;
        private String thumbnailUrl;
        private boolean publicAccess;
        private boolean randomQuestion;
        private boolean multipleChoice;
        private int questionCount;
        private List<QnAImageEntity> imageList;

        public QuizEntity toQuizEntity(UserEntity user) {
            return QuizEntity.builder()
                    .quizId(quizId)
                    .userEntity(user)
                    .title(title)
                    .description(comment)
                    .type(type)
                    .thumbnail_url(thumbnailUrl)
                    .limit_time(limitTime)
                    .public_access(publicAccess)
                    .random_question(randomQuestion)
                    .multiple_choice(multipleChoice)
                    .question_count(questionCount)
                    .qnAImageEntities(imageList)
                    .build();
        }

        public List<QnAImageEntity> toQnAImageEntities(QuizEntity quiz) {
            return imageList.stream().peek(image -> image.setQuizEntity(quiz)).toList();
        }
    }

    @Getter
    @Builder
    public static class UpdateRequest {
        @Setter
        private String userId;

        @Setter
        private UUID quizId;

        @NotBlank(message = "유효하지 않은 제목입니다.")
        @Size(max = 30, message = "제목은 최대 30글자 까지만 가능합니다.")
        private String title;

        @Size(max = 100, message = "설명은 최대 100글자 까지만 가능합니다.")
        private String comment;

        @NotNull(message = "유효하지 않은 제한 시간입니다.")
        private short limitTime;

        @NotNull(message = "유효하지 않은 대표 이미지 수정 여부입니다.")
        private boolean thumbnail;

        @NotNull(message = "유효하지 않은 공개 여부입니다.")
        private boolean publicAccess;

        @NotNull(message = "유효하지 않은 랜덤 출제 여부입니다.")
        private boolean randomQuestion;

        @NotNull(message = "유효하지 않은 사지선다 여부입니다.")
        private boolean multipleChoice;

        @NotBlank(message = "유효하지 않은 인증 본문입니다.")
        private String signatureMessage;

        @NotNull(message = "유효하지 않은 정답 배열입니다.")
        private UpdateRequest.QnA[] qnaArray;

        @Getter
        public static class QnA {
            private String questionUrl;

            @Size(max = 4, message = "보기는 최대 4개 까지만 가능합니다.")
            private String[] optionArray;

            @NotEmpty(message = "유효하지 않은 정답 배열입니다.")
            private String[] answerArray;
        }
    }

    @Getter
    @Builder
    public static class CommonRequest {
        private String userId;
        private String quizId;
    }


    @Getter
    @Builder
    public static class CommonResponse {
        private String quizId;
    }



    @Getter
    @Builder
    static class CreateStartResponse {
        private String quizId;
        private String thumbnailUrl;
        private String[] uploadUrlArray;
    }

    @Getter
    @Builder
    static class CreateEndResponse {
        private String quizId;
        private boolean succeed;
    }

    @Getter
    @Builder
    static class GetResponse {
        private String quizId;
        private boolean owner;
        private String title;
        private String comment;
        private String type;
        private String thumbnailUrl;
        private short limitTime;
        private boolean publicAccess;
        private boolean randomQuestion;
        private boolean multipleChoice;
        private int playCount;
        private int reportCount;
        private GetResponse.QnA[] qnaArray;

        @Getter
        @Builder
        public static class QnA {
            private String questionUrl;
            private String[] optionArray;
            private String[] answerArray;
        }
    }

    @Getter
    @Builder
    public static class UpdateStartResponse {
        private String quizId;
        private boolean nextRequest;
        private String thumbnailUrl;
        private String[] uploadUrlArray;
    }

}
