package com.xiaowa.writingassistant.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleBadRequest(IllegalArgumentException ex) {
        // 返回 400 状态码 + 统一的 Result 结构
        return ResponseEntity
                .badRequest()
                .body(Result.fail(ex.getMessage()));
    }
}
