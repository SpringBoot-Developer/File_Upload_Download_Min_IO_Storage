package com.minio.service.impl;

import com.minio.response.FileDetails;
import com.minio.service.FileService;
import com.minio.util.ApiUtils;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Value("${minio.endpoint.url}")
    private String minioEndpointUrl;
    @Value("${minio.bucket.name}")
    private String minioBucketName;
    @Value("${minio.access.key}")
    private String minioAccessKey;
    @Value("${minio.secret.key}")
    private String minioSecretKey;
    @Value("${local.drive.path}")
    private String localDrivePath;
    @Autowired
    private ApiUtils apiUtils;

    @Override
    public void uploadFile(MultipartFile file) throws MinioException, IOException {
        apiUtils.uploadToMinIO(file, minioBucketName, apiUtils.createMinioClient());
        LOGGER.info("File uploaded to MinIO storage successfully.");
    }

    @Override
    public void downloadFile() throws MinioException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        MinioClient minioClient = apiUtils.createMinioClient();
        FileDetails latestFileDetails = apiUtils.getLatestFileDetails(minioClient, minioBucketName);
        byte[] fileContent = latestFileDetails.fileContent();
        String latestFileName = latestFileDetails.fileName();

        if (fileContent != null) {
            LOGGER.info("Successfully retrieved the latest file: {}", latestFileName);
            // Save the file locally
            apiUtils.saveToLocal(fileContent, localDrivePath, latestFileName);
        } else {
            LOGGER.error("No file found in the specified bucket.");
            throw new FileNotFoundException("No file found in the specified bucket.");
        }
    }

}
