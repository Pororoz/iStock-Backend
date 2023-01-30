package com.pororoz.istock.common.exception;

import com.pororoz.istock.common.dto.ErrorResponse;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // @RequestBody valid
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleRequestBodyValidationErrors(
      MethodArgumentNotValidException e) {
    return getValidErrorResponseEntity(e);
  }

  // @ModelAttribute valid
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleModelAttributeValidationErrors(
      BindException e) {
    return getValidErrorResponseEntity(e);
  }

  private ResponseEntity<ErrorResponse> getValidErrorResponseEntity(BindException e) {
    List<ErrorBinder> errors = new ArrayList<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      errors.add(new ErrorBinder(error.getField(), error.getDefaultMessage()));
    }

    return getResponseEntity(ExceptionStatus.BAD_REQUEST, errors.get(0).getMessage(), errors,
        HttpStatus.BAD_REQUEST);
  }

  // ConstraintViolation
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleValidationBadPath(ConstraintViolationException e) {
    List<ErrorBinder> errors = new ArrayList<>();
    for (ConstraintViolation error : e.getConstraintViolations()) {
      errors.add(new ErrorBinder(error.getPropertyPath().toString(), error.getMessage()));
    }
    return getResponseEntity(ExceptionStatus.BAD_REQUEST, errors.get(0).getMessage(), errors,
        HttpStatus.BAD_REQUEST);
  }

  // Type Mismatch Validation
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleValidationTypeMismatchErrors() {
    return getResponseEntity(ExceptionStatus.BAD_REQUEST, ExceptionMessage.TYPE_MISMATCH,
        null, HttpStatus.BAD_REQUEST);
  }

  // Custom exception
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomErrors(final CustomException e) {
    return getResponseEntity(e.getStatus(), e.getMessage(), null, e.getStatusCode());
  }

  // 404
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundApi() {
    return getResponseEntity(ExceptionStatus.PAGE_NOT_FOUND, ExceptionMessage.PAGE_NOT_FOUND,
        null, HttpStatus.NOT_FOUND);
  }

  // Custom Exception 에서 처리되지 않은 400
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeError(final RuntimeException e) {
    log.error("Uncontrolled Exception", e);
    return getResponseEntity(ExceptionStatus.RUNTIME_ERROR, e.getMessage(), null,
        HttpStatus.BAD_REQUEST);
  }

  // 500
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> unhandledException(final Exception e) {
    log.error("Uncontrolled Exception", e);
    return getResponseEntity(ExceptionStatus.INTERNAL_SERVER_ERROR, e.getMessage(),
        null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ErrorResponse> getResponseEntity(String status, String message,
      List<ErrorBinder> errors, HttpStatus httpStatus) {
    ErrorResponse response = ErrorResponse.builder()
        .status(status)
        .message(message)
        .errors(errors)
        .build();
    return new ResponseEntity<>(response, httpStatus);
  }
}
