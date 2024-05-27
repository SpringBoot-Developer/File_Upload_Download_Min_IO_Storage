package com.minio.service;

import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface FileService {

    public void uploadFile(MultipartFile file) throws MinioException, IOException;

    public void downloadFile() throws MinioException, NoSuchAlgorithmException, InvalidKeyException, IOException;
}
