package com.pororoz.istock.common.exception;

import com.pororoz.istock.common.dto.ErrorResponse;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        List<ErrorBinder> errors = new ArrayList<>();
        for(FieldError error: e.getBindingResult().getFieldErrors()) {
            errors.add(new ErrorBinder(error.getField(), error.getDefaultMessage()));
        }

        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.BAD_REQUEST)
                .message(errors.get(0).getMessage())
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationBadPath(ConstraintViolationException e) {
        List<ErrorBinder> errors = new ArrayList<>();
        for (ConstraintViolation error: e.getConstraintViolations()) {
            errors.add(new ErrorBinder(error.getPropertyPath().toString(), error.getMessage()));
        }
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.BAD_REQUEST)
                .message(errors.get(0).getMessage())
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Type Mismatch Validation
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleValidationTypeMismatchErrors(MethodArgumentTypeMismatchException e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.BAD_REQUEST)
                .message(ExceptionMessage.TYPE_MISMATCH)
                .errors(new ArrayList<>())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Custom exception
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomErrors(final CustomException e){
        ErrorResponse response = ErrorResponse.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response, e.getStatusCode());
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundApi() {
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.PAGE_NOT_FOUND)
                .message(ExceptionMessage.PAGE_NOT_FOUND)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Custom Exception 에서 처리되지 않은 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeError(final RuntimeException e){
        log.error("Uncontrolled Exception", e);
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.RUNTIME_ERROR)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledException(final Exception e){
        log.error("Uncontrolled Exception", e);
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
