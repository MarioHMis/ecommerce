package com.marware.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file);
    void deleteFile(String fileUrl);
}
