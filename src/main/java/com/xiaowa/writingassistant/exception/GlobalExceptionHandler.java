package com.xiaowa.writingassistant.exception;

import com.xiaowa.writingassistant.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理非法参数的异常，返回 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(Result.fail(ex.getMessage()));
    }

    // 处理资源未找到，用 404 表示
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Result<?>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(Result.fail(ex.getMessage()));
    }

    // 处理坏请求，如确认接口状态错误，用 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Result<?>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .badRequest()
                .body(Result.fail(ex.getMessage()));
    }

    // 捕获其他未处理异常，返回 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception ex) {
        // TODO: 这里可以加日志记录
        ex.printStackTrace();
        return ResponseEntity
                .status(500)
                .body(Result.fail("Internal server error"));
    }
}
