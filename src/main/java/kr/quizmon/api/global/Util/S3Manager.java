package kr.quizmon.api.global.Util;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class S3Manager {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${custom.properties.s3_presignedurl_expiration_millisec}")
    private long s3Expiration;

    private final String PREFIX_IMAGE = "static/image/";

    public Map<String, String> genPutPresignedUrl(String quizId, int count, String hashedSignature) {
        return genPutPresignedUrl(quizId, 0, count, hashedSignature);
    }

    public Map<String, String> genPutPresignedUrl(String quizId, int startIndex, int count, String hashedSignature) {
        Map<String, String> result = new LinkedHashMap<>();

        for (int i = startIndex; i < count + startIndex; i++) {
            String fileName = String.valueOf(i + 1);

            String presignedUrl = genPutPresignedUrl(quizId, fileName, hashedSignature);
            String publicUrl = getPublicUrl(quizId, fileName);

            result.put(publicUrl, presignedUrl);
        }

        return result;
    }

    public String genPutPresignedUrl(String quizId, String fileName, String hashedSignature) {
        // 객체 경로 설정
        String objectKey = PREFIX_IMAGE + quizId + "/" + fileName;

        // Presigned Url 유효 기간 5분으로 설정
        Date expiration = new Date(new Date().getTime() + s3Expiration);

        // PresignedUrl 생성
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        // Signature Header 지정
        request.putCustomRequestHeader("signature", hashedSignature);

        return amazonS3Client.generatePresignedUrl(request).toString();
    }

    public String getPublicUrl(String quizId, String fileName) {
        // 객체 경로 설정
        String objectKey = PREFIX_IMAGE + quizId + "/" + fileName;

        return amazonS3Client.getUrl(bucket, objectKey).toString();
    }

    public boolean checkObject(String quizId) {
        return checkObject(quizId, 31);
    }

    public boolean checkObject(String quizId, int maxCount) {
        // quizId에 해당하는 모든 이미지 조회
        ListObjectsV2Result listResult = amazonS3Client.listObjectsV2(bucket, PREFIX_IMAGE + quizId);
        List<S3ObjectSummary> objectList = listResult.getObjectSummaries();

        // 파일 개수 확인
        int objectCount = objectList.size();
        if (objectCount < 1 || objectCount > maxCount) {
            return false;
        }

        // 파일 용량 확인 (5.1MB 이하)
        boolean sizeChecked = objectList.stream()
                .filter(object -> object.getSize() > 5100000)
                .findFirst()
                .isEmpty();

        return sizeChecked;
    }

    public int getObjectCount(String quizId) {
        // quizId에 해당하는 모든 이미지 조회
        ListObjectsV2Result listResult = amazonS3Client.listObjectsV2(bucket, PREFIX_IMAGE + quizId);

        return listResult.getKeyCount();
    }

    public List<String> getObjectKeyList(String quizId) {
        ListObjectsV2Result listResult = amazonS3Client.listObjectsV2(bucket, PREFIX_IMAGE + quizId);

        // 이미지 존재 여부 확인
        if (listResult.getKeyCount() == 0) {
            return null;
        }

        return listResult.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

    public void deleteObject(String quizId) {
        // quizId에 해당하는 모든 이미지 조회
        ListObjectsV2Result listResult = amazonS3Client.listObjectsV2(bucket, PREFIX_IMAGE + quizId);

        // 이미지 존재 여부 확인
        if (listResult.getKeyCount() == 0) {
            return;
        }

        List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>(listResult.getKeyCount());
        listResult.getObjectSummaries().forEach(object -> {
            keyVersions.add(new DeleteObjectsRequest.KeyVersion(object.getKey()));
        });

        // quizId에 해당하는 모든 이미지 삭제
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucket)
                .withKeys(keyVersions);

        amazonS3Client.deleteObjects(request);
    }

    public void deleteObject(String quizId, List<String> fileNames) {
        // 파일 이름 배열 확인
        if (fileNames == null || fileNames.size() == 0) {
            return;
        }

        List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>(fileNames.size());
        fileNames.forEach(fileName -> {
            String key = PREFIX_IMAGE + quizId + "/" + fileName;
            keyVersions.add(new DeleteObjectsRequest.KeyVersion(key));
        });

        // quizId에서 fileNames에 해당하는 모든 이미지 삭제
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucket)
                .withKeys(keyVersions);

        amazonS3Client.deleteObjects(request);
    }

}
