package com.pororoz.istock.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

  private final HttpStatus statusCode;

  private final String status;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.statusCode = errorCode.getStatusCode();
    this.status = errorCode.getStatus();
  }

  public CustomException(ErrorCode errorCode, String detail) {
    super(errorCode.getMessage() + " " + detail);
    this.statusCode = errorCode.getStatusCode();
    this.status = errorCode.getStatus();
  }
}