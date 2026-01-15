package com.meli.social.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
}
