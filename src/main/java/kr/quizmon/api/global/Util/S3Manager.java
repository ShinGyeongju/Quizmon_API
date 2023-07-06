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

    private final String PREFIX_IMAGE = "static/image/";

    public Map<String, String> genPutPresignedUrl(String quizId, int count, String hashedSignature) {
        Map<String, String> result = new LinkedHashMap<>();

        for (int i = 0; i < count; i++) {
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
        Date expiration = new Date(new Date().getTime() + (1000 * 60 * 5));

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

    public void deleteObject(String quizId) {
        // quizId에 해당하는 모든 이미지 조회
        ListObjectsV2Result listResult = amazonS3Client.listObjectsV2(bucket, PREFIX_IMAGE + quizId);

        List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>(listResult.getKeyCount());
        listResult.getObjectSummaries().forEach(object -> {
            keyVersions.add(new DeleteObjectsRequest.KeyVersion(object.getKey()));
        });

        // quizId에 해당하는 모든 이미지 삭제
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucket).withKeys(keyVersions);
        amazonS3Client.deleteObjects(request);
    }

}
