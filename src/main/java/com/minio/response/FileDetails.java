package com.minio.response;

public record FileDetails(byte[] fileContent, String fileName) {
}

