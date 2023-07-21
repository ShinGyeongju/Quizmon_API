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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    public static String popularityRankingKey;

    private final QuizRepository quizRepository;
    private final QnAImageRepository qnAImageRepository;
    private final UserRepository userRepository;
    private final RedisIO redisIO;
    private final HmacProvider hmacProvider;
    private final S3Manager s3Manager;

    @Value("${custom.properties.s3_presignedurl_signature_key}")
    private String signatureKey;
    @Value("${custom.properties.s3_presignedurl_expiration_millisec}")
    private long s3Expiration;

    @Override
    @Transactional(readOnly = true)
    public QuizDTO.CreateResponse createImageQuiz(QuizDTO.CreateRequest requestDto) {
        String quizId = requestDto.getQuizId().toString();

        // Signature Hash Code 생성
        String signatureCode;
        try {
            signatureCode = hmacProvider.genHmacBase64Code(signatureKey, requestDto.getSignatureMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 대표 이미지 URL 설정
        String thumbnailPreUrl = requestDto.getThumbnail()
                ? s3Manager.genPutPresignedUrl(quizId, "thumbnailImage", signatureCode)
                : null;
        String thumbnailPubUrl = requestDto.getThumbnail()
                ? s3Manager.getPublicUrl(quizId, "thumbnailImage")
                : null;

        // 문제 수 만큼 presignedUrl 생성
        int imageCount = requestDto.getQnaArray().length;
        Map<String, String> presignedUrls = s3Manager.genPutPresignedUrl(quizId, imageCount, signatureCode);

        // 각 문제 이미지의 S3 publicUrl 설정
        List<QnAImageEntity> imageEntities = new ArrayList<>(presignedUrls.size());
        int i = 0;
        for (String publicUrl : presignedUrls.keySet()) {
            String[] spliturl = publicUrl.split("/");
            String fileName = spliturl[spliturl.length - 1];

            QnAImageEntity imageEntity = QnAImageEntity.builder()
                    .sequence_number((short) (i + 1))
                    .file_name(fileName)
                    .image_url(publicUrl)
                    .options(requestDto.getQnaArray()[i].getOptionArray())
                    .answer(requestDto.getQnaArray()[i].getAnswerArray())
                    .build();

            imageEntities.add(imageEntity);
            i++;
        }

        // Redis에 임시 저장
        QuizDTO.CreateRedis quiz = requestDto.toRedisEntity(thumbnailPubUrl, imageEntities);
        redisIO.setQuiz(quizId, quiz, s3Expiration);

        // presignedUrl 배열 반환
        String[] urlArray = presignedUrls.values().toArray(String[]::new);

        return QuizDTO.CreateResponse.builder()
                .quizId(quizId)
                .thumbnailUrl(thumbnailPreUrl)
                .uploadUrlArray(urlArray)
                .build();
    }

    @Override
    @Transactional
    public QuizDTO.UpdateResponse updateImageQuiz(QuizDTO.UpdateRequest requestDto) {
        String quizId = requestDto.getQuizId().toString();

        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(quizId))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        // ID 확인
        if (!quiz.getUserEntity().getId().equals(requestDto.getUserId())) {
            throw new CustomApiException(ErrorCode.INVALID_QUIZ_ID);
        }

        // 이미지 업로드 여부 확인
        int containsNullCount = (int) Arrays.stream(requestDto.getQnaArray()).
                filter(qna -> qna.getQuestionUrl() == null)
                .count();
        boolean thumbnailUpload = requestDto.getThumbnailUpdate() && !requestDto.getThumbnailDelete();

        // 업로드할 이미지가 없으면 덮어쓰고 응답
        if (containsNullCount == 0 && !thumbnailUpload) {
            // 퀴즈 기본 정보 수정
            quiz.updateQuiz(requestDto);

            // 기존 문제 전체 삭제
            qnAImageRepository.deleteAllByQuizId(requestDto.getQuizId());

            // 요청 문제 저장
            List<QnAImageEntity> imageEntities = new ArrayList<>(requestDto.getQnaArray().length);
            int i = 0;
            for (QuizDTO.UpdateRequest.QnA qna : requestDto.getQnaArray()) {
                String[] spliturl = qna.getQuestionUrl().split("/");
                String fileName = spliturl[spliturl.length - 1];

                QnAImageEntity imageEntity = QnAImageEntity.builder()
                        .quizEntity(quiz)
                        .sequence_number((short) (i + 1))
                        .file_name(fileName)
                        .image_url(qna.getQuestionUrl())
                        .options(qna.getOptionArray())
                        .answer(qna.getAnswerArray())
                        .build();

                imageEntities.add(imageEntity);
                i++;
            }
            qnAImageRepository.saveAll(imageEntities);

            return QuizDTO.UpdateResponse.builder()
                    .quizId(quizId)
                    .checkRequire(false)
                    .thumbnailUrl(null)
                    .uploadUrlArray(null)
                    .build();
        }

        // Signature Hash Code 생성
        String signatureCode;
        try {
            signatureCode = hmacProvider.genHmacBase64Code(signatureKey, requestDto.getSignatureMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 대표 이미지 URL 설정
        String thumbnailPreUrl = null;
        String thumbnailPubUrl = null;
        if (!requestDto.getThumbnailDelete()) {
            thumbnailPreUrl = requestDto.getThumbnailUpdate()
                    ? s3Manager.genPutPresignedUrl(quizId, "thumbnailImage", signatureCode)
                    : null;
            thumbnailPubUrl = requestDto.getThumbnailUpdate()
                    ? s3Manager.getPublicUrl(quizId, "thumbnailImage")
                    : quiz.getThumbnail_url();
        }

        // 새로운 이미지 수 만큼 presignedUrl 생성
        int startIndex = quiz.getQnAImageEntities().stream()
                .map(qna -> Integer.parseInt(qna.getFile_name()))
                .max(Comparator.comparingInt(qna -> qna))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_VALUE, "유효하지 않은 이미지 url입니다."));
        Map<String, String> presignedUrls = s3Manager.genPutPresignedUrl(quizId, startIndex, containsNullCount, signatureCode);

        // 각 문제 이미지의 S3 publicUrl 설정
        List<QnAImageEntity> imageEntities = new ArrayList<>(requestDto.getQnaArray().length);
        int i = 0;
        List<String> publicUrlList = new ArrayList<>(presignedUrls.keySet().stream().toList());
        for (QuizDTO.UpdateRequest.QnA qna : requestDto.getQnaArray()) {
            String imageUrl = qna.getQuestionUrl() == null ? publicUrlList.remove(0) : qna.getQuestionUrl();

            String[] spliturl = imageUrl.split("/");
            String fileName = spliturl[spliturl.length - 1];

            QnAImageEntity imageEntity = QnAImageEntity.builder()
                    .sequence_number((short) (i + 1))
                    .file_name(fileName)
                    .image_url(imageUrl)
                    .options(qna.getOptionArray())
                    .answer(qna.getAnswerArray())
                    .build();

            imageEntities.add(imageEntity);
            i++;
        }

        // Redis에 임시 저장
        QuizDTO.CreateRedis quizRedis = requestDto.toRedisEntity(thumbnailPubUrl, imageEntities);
        redisIO.setQuiz(quizId, quizRedis, s3Expiration);

        // presignedUrl 배열 반환
        String[] urlArray = presignedUrls.values().toArray(String[]::new);

        return QuizDTO.UpdateResponse.builder()
                .quizId(quizId)
                .checkRequire(true)
                .thumbnailUrl(thumbnailPreUrl)
                .uploadUrlArray(urlArray)
                .build();
    }

    @Override
    @Transactional
    public QuizDTO.CheckResponse checkImageQuiz(QuizDTO.CommonRequest commonDto) {
        String quizId = commonDto.getQuizId();

        // Redis에서 퀴즈 정보 가져오기
        QuizDTO.CreateRedis redisQuiz = redisIO.getQuiz(quizId);
        if (redisQuiz == null) {
            log.error("There is no such quizId in redis.");

            return QuizDTO.CheckResponse.builder()
                    .quizId(quizId)
                    .succeed(false)
                    .build();
        }

        // Redis에서 퀴즈 정보 삭제
        redisIO.deleteValue(quizId);

        String thumbnailUrl = null;
        List<QnAImageEntity> qnas = null;
        QuizEntity quiz = null;

        try {
            // 업데이트 요청일 경우, 초기 퀴즈 정보 저장
            if (redisQuiz.isUpdate()) {
                quiz = quizRepository.findByQuizId(UUID.fromString(quizId))
                        .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));
                thumbnailUrl = quiz.getThumbnail_url();
                qnas = quiz.getQnAImageEntities().stream().toList();
            }

            // S3에 저장된 퀴즈 검증
            if (redisQuiz.isUpdate()) {
                int maxCount = 31 + redisQuiz.getQuestionCount();
                if (!s3Manager.checkObject(quizId, maxCount)) {
                    throw new Exception("Invalid image file");
                }
            } else {
                if (!s3Manager.checkObject(quizId)) {
                    throw new Exception("Invalid image file");
                }

                // Redis와 S3의 파일 개수 비교
                int imageCount = redisQuiz.getThumbnailUrl() != null
                        ? redisQuiz.getQuestionCount() + 1
                        : redisQuiz.getQuestionCount();
                if (imageCount != s3Manager.getObjectCount(quizId)) {
                    throw new Exception("File count does not match.");
                }
            }

            // ID 존재 여부 확인
            UserEntity user = userRepository.findById(commonDto.getUserId())
                    .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_USER));

            // Quiz 저장
            if (redisQuiz.isUpdate()) {
                quiz.updateQuiz(redisQuiz.toQuizEntity(user));

                thumbnailUrl = quiz.getThumbnail_url();

                // 기존 문제 전체 삭제
                qnAImageRepository.deleteAllByQuizId(quiz.getQuizId());
            } else {
                quiz = quizRepository.saveAndFlush(redisQuiz.toQuizEntity(user));
            }

            // Image 문/답 저장
            // TODO: Bulk Insert로 전환 필요
            qnas = qnAImageRepository.saveAll(redisQuiz.toQnAImageEntities(quiz));
        } catch (Exception ex) {
            log.error(ex.getMessage());

            // 생성 요청일 경우, 전체 이미지 파일 삭제
            if (!redisQuiz.isUpdate()) {
                s3Manager.deleteObject(quizId);
            }

            return QuizDTO.CheckResponse.builder()
                    .quizId(quizId)
                    .succeed(false)
                    .build();
        } finally {
            // 업데이트 요청일 경우, 사용하지 않는 이미지 파일 삭제
            if (redisQuiz.isUpdate()) {
                // DB에 저장된 파일 이름 가져오기
                List<String> dbFiles = new ArrayList<>(qnas.stream().map(QnAImageEntity::getFile_name).toList());
                if (thumbnailUrl != null) {
                    dbFiles.add("thumbnailImage");
                }

                // S3에 저장된 Key 가져오기
                List<String> s3Files = s3Manager.getObjectKeyList(quizId);

                if (dbFiles.size() != 0 && s3Files != null) {
                    // 사용하지 않는 이미지 파일 설정
                    List<String> deleteFiles = s3Files.stream()
                            .map(key -> {
                                String[] splitKey = key.split("/");
                                return splitKey[splitKey.length - 1];
                            }).filter(s3File -> !dbFiles.contains(s3File))
                            .toList();

                    s3Manager.deleteObject(quizId, deleteFiles);
                }
            }
        }

        return QuizDTO.CheckResponse.builder()
                .quizId(quizId)
                .succeed(true)
                .build();
    }

    @Override
    @Transactional
    public QuizDTO.CommonResponse deleteQuiz(QuizDTO.CommonRequest commonDto) {
        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(commonDto.getQuizId()))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        // ID 확인
        String quizOwner = quiz.getUserEntity().getId();
        if (!quizOwner.equals(commonDto.getUserId())) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }

        // 퀴즈 삭제
        quizRepository.delete(quiz);

        // 이미지 파일 삭제
        s3Manager.deleteObject(commonDto.getQuizId());

        return QuizDTO.CommonResponse.builder()
                .quizId(commonDto.getQuizId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDTO.GetResponse getQuiz(QuizDTO.GetRequest requestDto) {
        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByUrlId(requestDto.getUrlId())
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_URL_ID));

        // 퀴즈 관리 권한 확인
        boolean isOwner = requestDto.getUserId() != null
                && (requestDto.getUserId().equals(quiz.getUserEntity().getId()) || requestDto.getUserAuthority().equals("ADMIN"));

        // 문제 응답 배열 생성
        QuizDTO.GetResponse.QnA[] qnas = quiz.getQnAImageEntities().stream()
                .sorted(Comparator.comparing(QnAImageEntity::getSequence_number))
                .map(image -> QuizDTO.GetResponse.QnA.builder()
                        .questionUrl(image.getImage_url())
                        .optionArray(image.getOptions())
                        .answerArray(image.getAnswer())
                        .build())
                .toArray(QuizDTO.GetResponse.QnA[]::new);

        // 대표 이미지 지정
        String thumbnailUrl = quiz.getThumbnail_url() != null ? quiz.getThumbnail_url() : qnas[0].getQuestionUrl();

        if (requestDto.getPlay() == null || requestDto.getPlay()) {
            // 문제 순서 랜점 정렬
            if (quiz.isRandom_question()) {
                List<QuizDTO.GetResponse.QnA> qnsList = Arrays.asList(qnas);
                Collections.shuffle(qnsList);
                qnas = qnsList.toArray(QuizDTO.GetResponse.QnA[]::new);
            }
        }

        return QuizDTO.GetResponse.builder()
                .quizId(String.valueOf(quiz.getQuizId()))
                .isOwner(isOwner)
                .title(quiz.getTitle())
                .comment(quiz.getDescription())
                .type(quiz.getType())
                .thumbnailUrl(thumbnailUrl)
                .limitTime(quiz.getLimit_time())
                .publicAccess(quiz.isPublic_access())
                .randomQuestion(quiz.isRandom_question())
                .multipleChoice(quiz.isMultiple_choice())
                .playCount(quiz.getPlay_count())
                .reportCount(quiz.getReport_count())
                .qnaArray(qnas)
                .build();
    }

    @Override
    public QuizDTO.GetListResponse getQuizList(QuizDTO.GetListRequest requestDto) {
        QuizDTO.QuizListQuery quizQuery = new QuizDTO.QuizListQuery();

        // 퀴즈 종류 설정
        if (requestDto.getType() != null) {
            switch (requestDto.getType()) {
                case "1":
                    quizQuery.setType("IMAGE");
                    break;
                case "2":
                    quizQuery.setType("SOUND");
                    break;
            }
        } else {
            quizQuery.setType(null);
        }

        // 접근 종류 설정
        if (requestDto.getAccess() != null) {
            switch (requestDto.getAccess()) {
                case "1":
                    quizQuery.setAccess(false);
                    break;
                case "2":
                    quizQuery.setAccess(null);
                    break;
            }
        } else {
            quizQuery.setAccess(true);
        }

        // 퀴즈 업데이트 시간 설정

        // 퀴즈 순번 설정
        quizQuery.setSeqNum(requestDto.getSeqNum() != null ? requestDto.getSeqNum() : null);

        // 검색어 설정
        quizQuery.setSearchWord(requestDto.getSearchWord() != null ? requestDto.getSearchWord() : null);

        // 퀴즈 개수 설정 (기본값:20)
        quizQuery.setCount(requestDto.getCount() != null ? requestDto.getCount() : 20);

        // 사용자 설정
        if (requestDto.getUserOnly() != null && requestDto.getUserOnly()) {
            // ID 존재 여부 확인
            UserEntity user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_USER));

            quizQuery.setUserPk(user.getUser_pk());
        }

        QuizDTO.GetListResponse.Quiz[] quizs = null;
        String[] sortedQuizIds = null;

        // 정렬 방식 설정
        switch (requestDto.getSort()) {
            case "1":
                quizQuery.setTimeStamp(requestDto.getTimeStamp() != null ? requestDto.getTimeStamp() : null);
                quizQuery.setOrder(new Sort.Order(Sort.Direction.DESC, "updated_at"));
                break;
            case "2":
                long start = requestDto.getSeqNum() != null ? requestDto.getSeqNum() - 1 : 0;
                long end = start + quizQuery.getCount();

                sortedQuizIds = redisIO.getPopularityRanking("popularityRanking", start, end);
                if (sortedQuizIds == null) {
                    quizs = new QuizDTO.GetListResponse.Quiz[0];
                    break;
                }

                quizQuery.setQuizIdArray(Arrays.stream(sortedQuizIds)
                        .map(UUID::fromString)
                        .toArray(UUID[]::new));
                break;
            case "3":
                quizQuery.setSeqNum(requestDto.getSeqNum() != null ? requestDto.getSeqNum() : null);
                quizQuery.setOrder(new Sort.Order(Sort.Direction.DESC, "play_count"));
                break;
            case "4":
                quizQuery.setSeqNum(requestDto.getSeqNum() != null ? requestDto.getSeqNum() : null);
                quizQuery.setOrder(new Sort.Order(Sort.Direction.DESC, "report_count"));
                break;
        }

        if (quizs == null) {
            // DB 조회
            quizs = quizRepository.findAllOrderByCustom(quizQuery)
                    .toArray(QuizDTO.GetListResponse.Quiz[]::new);

            // 실시간 인기순으로 정렬
            if (quizs.length > 2 && requestDto.getSort().equals("2")) {
                List<String> sortedQuizIdList = Arrays.stream(sortedQuizIds).toList();
                quizs = Arrays.stream(quizs)
                        .sorted(Comparator.comparing(quiz -> sortedQuizIdList.indexOf(quiz.getQuizId().toString())))
                        .toArray(QuizDTO.GetListResponse.Quiz[]::new);
            }
        }

        return QuizDTO.GetListResponse.builder()
                .quizCount(quizs.length)
                .quizArray(quizs)
                .build();
    }

    @Override
    @Transactional
    public QuizDTO.CommonResponse reportQuiz(QuizDTO.CommonRequest commonDto) {
        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(commonDto.getQuizId()))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        String redisKey = "reportQuiz" + commonDto.getQuizId();

        // Redis에 없으면 Quiz 신고수 증가
        if (!redisIO.hasKey(redisKey)) {
            quiz.incrementReportCount();;

            // Redis에 저장
            redisIO.setQuizReport(redisKey, 60000);     // TTL 1분
        }

        return QuizDTO.CommonResponse.builder()
                .quizId(commonDto.getQuizId())
                .build();
    }

    @Override
    @Transactional
    public QuizDTO.CommonResponse reportResetQuiz(QuizDTO.CommonRequest commonDto) {
        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(commonDto.getQuizId()))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        // Quiz 신고수가 0보다 크면 초기화
        if (quiz.getReport_count() > 0) {
            quiz.resetReportCount();
        }

        return QuizDTO.CommonResponse.builder()
                .quizId(commonDto.getQuizId())
                .build();
    }


}
