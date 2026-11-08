package com.example.campus.exception;



/** 可预期的业务失败：由统一异常处理器转换成对应 HTTP 状态。 */
public class AppException extends RuntimeException {
    private final int status;
    public AppException(int status, String message) { super(message); this.status = status; }
    public int status() { return status; }
}
