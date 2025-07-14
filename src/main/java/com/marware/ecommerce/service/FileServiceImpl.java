package com.marware.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);

        String fileKey = generateFileKey(file.getOriginalFilename());
        uploadToS3(file, fileKey);

        return generatePublicUrl(fileKey);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }
    }

    private String generateFileKey(String originalFilename) {
        return "uploads/" + UUID.randomUUID() + "_" + originalFilename;
    }

    private void uploadToS3(MultipartFile file, String fileKey) {
        try (S3Client s3Client = buildS3Client()) {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo", e);
        }
    }

    private String generatePublicUrl(String fileKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, fileKey);
    }

    private S3Client buildS3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }
}
