package com.example.campus.exception;

import cn.dev33.satoken.exception.NotLoginException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> business(AppException e) { return error(e.status(), e.getMessage()); }
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<?> login(NotLoginException e) { return error(401, "请先登录或重新登录"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException e) {
        var field = e.getBindingResult().getFieldErrors().get(0);
        return error(400, "参数错误：" + field.getField() + " " + field.getDefaultMessage());
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> malformed(Exception e) { return error(400, "请求格式错误，请检查数字和日期"); }
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> duplicate(DuplicateKeyException e) { return error(409, "记录已存在，请刷新后重试"); }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> database(DataAccessException e) {
        log.error("数据库操作失败", e);
        return error(503, "数据库暂时不可用，请稍后重试");
    }
}
