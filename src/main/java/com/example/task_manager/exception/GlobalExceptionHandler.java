package com.example.task_manager.exception;

import com.example.task_manager.response.AppResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.example.task_manager.constant.AppConstant.ERROR;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    // Handle specific exceptions (like IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        AppResponse<String> response = new AppResponse<>(null, ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle any other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse<String>> handleGlobalException(Exception ex, WebRequest request) {
        AppResponse<String> response = new AppResponse<>(null, ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
