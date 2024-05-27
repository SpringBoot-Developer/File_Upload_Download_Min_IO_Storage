package com.minio.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {

    private HttpStatus status;
    private String message;
    private String debugMessage;
}
