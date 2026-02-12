package com.example.demospringproject;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String detailedMessage, //error message
        LocalDateTime errorTime
){}
