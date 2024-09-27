package com.musinsa.pointapi.presentation;

import com.musinsa.pointapi.presentation.dto.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return getFailResponseEntity(ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResultResponse> handleInvalidConditionExceptions(IllegalStateException ex) {
        return getFailResponseEntity(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse> handleCommonExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ResultResponse.failure());
    }

    private ResponseEntity<ResultResponse> getFailResponseEntity(String message) {
        log.warn(message);
        return ResponseEntity.badRequest()
                .body(ResultResponse.failure(message));
    }
}
