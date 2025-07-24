package com.marware.ecommerce.service;

import com.marware.ecommerce.exception.FileProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceImplTest {

    @InjectMocks FileServiceImpl fileService;
    @Mock          S3Client     s3Client;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field accessKeyField = FileServiceImpl.class.getDeclaredField("accessKey");
        accessKeyField.setAccessible(true);
        accessKeyField.set(fileService, "AK");

        Field secretKeyField = FileServiceImpl.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(fileService, "SK");

        Field regionField = FileServiceImpl.class.getDeclaredField("region");
        regionField.setAccessible(true);
        regionField.set(fileService, "us-east-1");

        Field bucketNameField = FileServiceImpl.class.getDeclaredField("bucketName");
        bucketNameField.setAccessible(true);
        bucketNameField.set(fileService, "my-bucket");
    }

    @Test
    void uploadFile_whenNotImage_thenThrow() {
        MockMultipartFile bad = new MockMultipartFile(
                "f", "doc.pdf", "application/pdf", "x".getBytes());
        assertThatThrownBy(() -> fileService.uploadFile(bad))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Solo se permiten archivos de imagen");
    }

    @Test
    void uploadFile_whenTooLarge_thenThrow() {
        byte[] data = new byte[(int) (5L * 1024 * 1024 + 1)];
        MockMultipartFile img = new MockMultipartFile(
                "f", "big.png", "image/png", data);
        assertThatThrownBy(() -> fileService.uploadFile(img))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("excede el límite de");
    }

    @Test
    void uploadFile_whenOk_thenReturnsUrl() throws IOException {
        MockMultipartFile img = new MockMultipartFile(
                "f", "pic.png", "image/png", "data".getBytes());

        String url = fileService.uploadFile(img);

        assertThat(url).startsWith("https://my-bucket.s3.");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}