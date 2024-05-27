package com.minio.controller;

import com.minio.response.FileResponse;
import com.minio.service.impl.FileServiceImpl;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api")
public class UploadAndDownloadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadAndDownloadController.class);


    @Autowired
    private FileServiceImpl fileServiceImpl;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                LOGGER.warn("Empty file provided");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new FileResponse(HttpStatus.BAD_REQUEST, "Empty file provided, please upload a valid file", null));
            }
            // Process the file upload using the uploadFile method
            fileServiceImpl.uploadFile(file);
            // Return a success response
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new FileResponse(HttpStatus.OK, "File uploaded successfully", null));
        } catch (IOException e) {
            LOGGER.error("IOException during file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new FileResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error during file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new FileResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", e.getMessage()));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<FileResponse> downloadFile() {
        try {
            fileServiceImpl.downloadFile();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new FileResponse(HttpStatus.OK, "File downloaded successfully", null));
        } catch (FileNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FileResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null));
        } catch (IOException | MinioException | NoSuchAlgorithmException | InvalidKeyException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FileResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null));
        }
    }


}
