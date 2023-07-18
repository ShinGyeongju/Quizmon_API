package kr.quizmon.api.domain.quiz;

import jakarta.validation.constraints.*;
import kr.quizmon.api.domain.user.UserEntity;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
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
        private Short limitTime;

        @NotNull(message = "유효하지 않은 대표 이미지 여부입니다.")
        private Boolean thumbnail;

        @NotNull(message = "유효하지 않은 공개 여부입니다.")
        private Boolean publicAccess;

        @NotNull(message = "유효하지 않은 랜덤 출제 여부입니다.")
        private Boolean randomQuestion;

        @NotNull(message = "유효하지 않은 사지선다 여부입니다.")
        private Boolean multipleChoice;

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
                    .update(false)
                    .title(title)
                    .comment(comment)
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
        private boolean update;
        private String title;
        private String comment;
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
                    .type("IMAGE")
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
        private Short limitTime;

        @NotNull(message = "유효하지 않은 대표 이미지 수정 여부입니다.")
        private Boolean thumbnailUpdate;

        @NotNull(message = "유효하지 않은 대표 이미지 삭제 여부입니다.")
        private Boolean thumbnailDelete;

        @NotNull(message = "유효하지 않은 공개 여부입니다.")
        private Boolean publicAccess;

        @NotNull(message = "유효하지 않은 랜덤 출제 여부입니다.")
        private Boolean randomQuestion;

        @NotNull(message = "유효하지 않은 사지선다 여부입니다.")
        private Boolean multipleChoice;

        @NotBlank(message = "유효하지 않은 인증 본문입니다.")
        private String signatureMessage;

        @NotNull(message = "유효하지 않은 정답 배열입니다.")
        private QnA[] qnaArray;

        @Getter
        public static class QnA {
            private String questionUrl;

            @Size(max = 4, message = "보기는 최대 4개 까지만 가능합니다.")
            private String[] optionArray;

            @NotEmpty(message = "유효하지 않은 정답 배열입니다.")
            private String[] answerArray;
        }

        public CreateRedis toRedisEntity(String thumbnailUrl, List<QnAImageEntity> images) {
            return CreateRedis.builder()
                    .quizId(quizId)
                    .update(true)
                    .title(title)
                    .comment(comment)
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
    @Setter
    @Builder
    public static class GetRequest {
        private String userId;
        private String userAuthority;
        //private String quizId;
        private String urlId;
        private Boolean play;
    }

    @Getter
    @Builder
    public static class GetListRequest {
        @Setter
        private String userId;

        @NotNull(message = "유효하지 않은 정렬 방식입니다.")
        @Pattern(regexp="[1234]" , message="유효하지 않은 정렬 방식입니다.")
        private String sort;

        @Pattern(regexp="[12]" , message="유효하지 않은 퀴즈 종류입니다.")
        private String type;

        @Pattern(regexp="[12]" , message="유효하지 않은 접근 종류입니다.")
        private String access;

        //@Pattern(regexp="[0-9]*" , message="유효하지 않은 업데이트 시간입니다.")
        private LocalDateTime timeStamp;

        //@Pattern(regexp="[0-9]*" , message="유효하지 않은 퀴즈 순번입니다.")
        private Integer seqNum;

        private String searchWord;

        //@Pattern(regexp="[0-9]*" , message="유효하지 않은 퀴즈 개수입니다.")
        private Integer count;

        private Boolean userOnly;
    }

    @Getter
    @Setter
    public static class QuizListQuery {
        private UUID userPk;
        private String type;
        private Boolean access;
        private LocalDateTime timeStamp;
        private Integer seqNum;
        private String searchWord;
        private long count;
        private Sort.Order order;
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
    public static class CreateResponse {
        private String quizId;
        private String thumbnailUrl;
        private String[] uploadUrlArray;
    }

    @Getter
    @Builder
    public static class UpdateResponse {
        private String quizId;
        private boolean checkRequire;
        private String thumbnailUrl;
        private String[] uploadUrlArray;
    }

    @Getter
    @Builder
    public static class CheckResponse {
        private String quizId;
        private boolean succeed;
    }

    @Getter
    @Builder
    public static class GetResponse {
        private String quizId;
        private boolean isOwner;
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
        private QnA[] qnaArray;

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
    public static class GetListResponse {
        private int quizCount;
        private Quiz[] quizArray;

        @Getter
        //@Builder
        @NoArgsConstructor
        public static class Quiz {
            private UUID quizId;
            private String urlId;
            private String title;
            private String comment;
            private String type;
            private String thumbnailUrl;
            private short limitTime;
            private int playCount;
            private int reportCount;
            private LocalDateTime timeStamp;
            private int seqNum;
        }
    }

}
