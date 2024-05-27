package com.minio.util;

import com.minio.response.FileDetails;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class ApiUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiUtils.class);

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


    public MinioClient createMinioClient() {
        return MinioClient.builder().endpoint(minioEndpointUrl).credentials(minioAccessKey, minioSecretKey).build();
    }

    public FileDetails getLatestFileDetails(MinioClient minioClient, String bucketName) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] fileContent = null;
        String latestFileName = null;

        // List objects in the bucket
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build()
        );
        Item latestItem = null;
        for (Result<Item> result : results) {
            Item item = result.get();
            if (latestItem == null || item.lastModified().isAfter(latestItem.lastModified())) {
                latestItem = item;
            }
        }
        if (latestItem != null) {
            // Retrieve the latest file content
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(latestItem.objectName())
                            .build())) {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileContent = outputStream.toByteArray();
                latestFileName = latestItem.objectName();
                LOGGER.info("Latest file: {}", latestFileName);
            }
        } else {
            LOGGER.warn("Bucket is empty or no files found.");
        }
        return new FileDetails(fileContent, latestFileName);
    }

    public void uploadToMinIO(MultipartFile file, String minioBucketName, MinioClient minioClient)
            throws IOException, MinioException {

        String fileName = file.getOriginalFilename();

        try (InputStream fileStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(fileName)
                            .stream(fileStream, file.getSize(), -1) // Use stream method to upload file contents
                            .build());
            LOGGER.info("File '{}' uploaded successfully to MinIO bucket: {}", fileName, minioBucketName);
        } catch (IOException | MinioException e) {
            LOGGER.error("Error uploading file to MinIO: {}", e.getMessage());
            throw e; // Re-throw the exception for handling at a higher level
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveToLocal(byte[] bytes, String localDrivePath, String fileName) throws IOException {
        String filePath = localDrivePath + File.separator + fileName;
        File file = new File(filePath);
        // Ensure the parent directory exists
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            LOGGER.info("Creating directory: {}", parentDir);
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir);
            }
        }
        // Saving file into local drive
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(bytes);
            LOGGER.info("File downloaded and saved at: {}", filePath);
        } catch (IOException e) {
            LOGGER.error("Error writing file to local storage: {}", e.getMessage());
            throw e;
        }
    }
}
