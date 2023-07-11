package kr.quizmon.api.domain.quiz;

import kr.quizmon.api.domain.user.UserEntity;
import kr.quizmon.api.domain.user.UserRepository;
import kr.quizmon.api.global.Util.HmacProvider;
import kr.quizmon.api.global.Util.RedisIO;
import kr.quizmon.api.global.Util.S3Manager;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QnAImageRepository qnAImageRepository;
    private final UserRepository userRepository;
    private final RedisIO redisIO;
    private final HmacProvider hmacProvider;
    private final S3Manager s3Manager;

    @Value(("${custom.properties.s3_presignedurl_signature_key}"))
    private String signatureKey;

    @Override
    @Transactional(readOnly = true)
    public QuizDTO.CreateStartResponse createStartQuiz(QuizDTO.CreateRequest requestDto) {
        String quizId = requestDto.getQuizId().toString();

        // Signature Hash Code 생성
        String signatureCode;
        try {
            signatureCode = hmacProvider.genHmacBase64Code(signatureKey, requestDto.getSignatureMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 대표 이미지 URL 설정
        String thumbnailPreUrl = requestDto.isThumbnail()
                ? s3Manager.genPutPresignedUrl(quizId, "thumbnailImage", signatureCode)
                : null;
        String thumbnailPubUrl = requestDto.isThumbnail()
                ? s3Manager.getPublicUrl(quizId, "thumbnailImage")
                : null;

        // 문제 수 만큼 presignedUrl 생성
        int imageCount = requestDto.getQnaArray().length;
        Map<String, String> presignedUrls = s3Manager.genPutPresignedUrl(quizId, imageCount, signatureCode);

        // 각 문제 이미지의 S3 publicUrl 설정
        List<QnAImageEntity> imageEntities = new ArrayList<>(presignedUrls.size());
        int i = 0;
        for (String publicUrl : presignedUrls.keySet()) {
            QnAImageEntity imageEntity = QnAImageEntity.builder()
                    .sequence_number((short) (i + 1))
                    .image_url(publicUrl)
                    .options(requestDto.getQnaArray()[i].getOptionArray())
                    .answer(requestDto.getQnaArray()[i].getAnswerArray())
                    .build();

            imageEntities.add(imageEntity);
            i++;
        }

        // Redis용 객체 생성
        QuizDTO.CreateRedis quiz = requestDto.toRedisEntity(thumbnailPubUrl, imageEntities);

        // Redis에 임시 저장
        long expiration = 1000 * 60 * 5;
        redisIO.setQuiz(quizId, quiz, expiration);

        // presignedUrl 배열 반환
        String[] urlArray = presignedUrls.values().toArray(String[]::new);

        return QuizDTO.CreateStartResponse.builder()
                .quizId(quizId)
                .thumbnailUrl(thumbnailPreUrl)
                .uploadUrlArray(urlArray)
                .build();
    }

    @Override
    @Transactional
    public QuizDTO.CreateEndResponse createEndQuiz(QuizDTO.CommonRequest commonDto) {
        String quizId = commonDto.getQuizId();

        QuizDTO.CreateEndResponse errorResponse = QuizDTO.CreateEndResponse.builder()
                .quizId(quizId)
                .succeed(false)
                .build();

        try {
            // Redis에서 퀴즈 정보 가져오기
            QuizDTO.CreateRedis redisQuiz = redisIO.getQuiz(quizId);
            if (redisQuiz == null) {
                throw new Exception("There is no such quizId in redis.");
            }
            redisIO.deleteQuiz(quizId);

            // S3에 저장된 퀴즈 검증
            if (!s3Manager.checkOebject(quizId)) {
                throw new Exception("Invalid image file");
            }

            // Redis와 S3의 파일 개수 비교
            int imageCount = redisQuiz.getThumbnailUrl() != null
                    ? redisQuiz.getQuestionCount() + 1
                    : redisQuiz.getQuestionCount();
            if (imageCount != s3Manager.getObjectCount(quizId)) {
                throw new Exception("File count does not match.");
            }

            // ID 존재 여부 확인
            UserEntity user = userRepository.findById(commonDto.getUserId())
                    .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_USER));

            // Quiz 저장
            QuizEntity quiz = quizRepository.saveAndFlush(redisQuiz.toQuizEntity(user));

            // Image 문/답 저장
            qnAImageRepository.saveAllAndFlush(redisQuiz.toQnAImageEntities(quiz));
        } catch (Exception ex) {
            log.error(ex.getMessage());

            // 이미지 파일 삭제
            s3Manager.deleteObject(quizId);
            // Redis에서 퀴즈 정보 삭제
            redisIO.deleteQuiz(quizId);

            return errorResponse;
        }

        return QuizDTO.CreateEndResponse.builder()
                .quizId(quizId)
                .succeed(true)
                .build();
    }

    @Override
    @Transactional
    public QuizDTO.UpdateStartResponse updateStartQuiz(QuizDTO.UpdateRequest requestDto) {
        String quizId = requestDto.getQuizId().toString();

        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(quizId))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        // 이미지 업로드 여부 확인
        Optional<QuizDTO.UpdateRequest.QnA> containsNull = Arrays.stream(requestDto.getQnaArray()).
                filter(qna -> qna.getQuestionUrl() == null).
                findFirst();

        // 업로드할 이미지가 없으면 덮어쓰고 응답
        if (containsNull.isEmpty() && !requestDto.isThumbnail()) {
            // 퀴즈 기본 정보 수정
            quiz.updateQuiz(requestDto);

            // 기존 문제 전체 삭제
            qnAImageRepository.deleteAllByQuizId(quizId);

            // 요청 문제 추가
            List<QnAImageEntity> imageEntities = new ArrayList<>(requestDto.getQnaArray().length);
            int i = 0;
            for (QuizDTO.UpdateRequest.QnA qna : requestDto.getQnaArray()) {
                QnAImageEntity imageEntity = QnAImageEntity.builder()
                        .quizEntity(quiz)
                        .sequence_number((short) (i + 1))
                        .image_url(qna.getQuestionUrl())
                        .options(qna.getOptionArray())
                        .answer(qna.getAnswerArray())
                        .build();

                imageEntities.add(imageEntity);
                i++;
            }
            qnAImageRepository.saveAll(imageEntities);

            return QuizDTO.UpdateStartResponse.builder()
                    .nextRequest(false)
                    .thumbnailUrl(null)
                    .uploadUrlArray(null)
                    .build();
        }







        return null;
    }


    @Override
    @Transactional(readOnly = true)
    public QuizDTO.GetResponse getQuiz(QuizDTO.CommonRequest commonDto) {
        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(commonDto.getQuizId()))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        // 퀴즈 관리 권한 확인
        boolean owner = commonDto.getUserId() != null && commonDto.getUserId().equals(quiz.getUserEntity().getId());

        // 문제 응답 배열 생성
        QuizDTO.GetResponse.QnA[] qnas = quiz.getQnAImageEntities().stream()
                .sorted(Comparator.comparing(QnAImageEntity::getSequence_number))
                .map(image -> QuizDTO.GetResponse.QnA.builder()
                        .questionUrl(image.getImage_url())
                        .optionArray(image.getOptions())
                        .answerArray(image.getAnswer())
                        .build())
                .toArray(QuizDTO.GetResponse.QnA[]::new);

        return QuizDTO.GetResponse.builder()
                .quizId(commonDto.getQuizId())
                .owner(owner)
                .title(quiz.getTitle())
                .comment(quiz.getDescription())
                .type(quiz.getType())
                .thumbnailUrl(quiz.getThumbnail_url())
                .limitTime(quiz.getLimit_time())
                .publicAccess(quiz.isPublic_access())
                .randomQuestion(quiz.isRandom_question())
                .multipleChoice(quiz.isMultiple_choice())
                .playCount(quiz.getPlay_count())
                .reportCount(quiz.getReport_count())
                .qnaArray(qnas)
                .build();
    }

}
