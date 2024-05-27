package com.minio.exception;

import com.minio.response.FileResponse;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FileResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("IllegalArgumentException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid file provided",
                ex.getMessage()
        );
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<FileResponse> handleIOException(IOException ex, WebRequest request) {
        logger.error("IOException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "IO error occurred",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<FileResponse> handleNoSuchFileException(NoSuchFileException ex, WebRequest request) {
        logger.error("NoSuchFileException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.NOT_FOUND,
                "File not found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<FileResponse> handleMinioException(MinioException ex, WebRequest request) {
        logger.error("MinioException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Minio error occurred",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<FileResponse> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex, WebRequest request) {
        logger.error("NoSuchAlgorithmException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Algorithm error occurred",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<FileResponse> handleInvalidKeyException(InvalidKeyException ex, WebRequest request) {
        logger.error("InvalidKeyException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Invalid key error occurred",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FileResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Exception: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<FileResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        logger.error("MaxUploadSizeExceededException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "File size exceeds the maximum limit",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<FileResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        logger.error("HttpMediaTypeNotAcceptableException: ", ex);
        FileResponse errorResponse = new FileResponse(
                HttpStatus.NOT_ACCEPTABLE,
                "The server cannot provide a response in the requested format.",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}
