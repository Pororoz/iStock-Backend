package com.pororoz.istock.common.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.PAGE_NOT_FOUND,
      ExceptionMessage.PAGE_NOT_FOUND),
  ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.ROLE_NOT_FOUND,
      ExceptionMessage.ROLE_NOT_FOUND),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.USER_NOT_FOUND,
      ExceptionMessage.USER_NOT_FOUND),
  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.CATEGORY_NOT_FOUND,
      ExceptionMessage.CATEGORY_NOT_FOUND),
  PRODUCT_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, ExceptionStatus.PRODUCT_NAME_DUPLICATED,
      ExceptionMessage.PRODUCT_NAME_DUPLICATED),
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.PRODUCT_NOT_FOUND,
      ExceptionMessage.PRODUCT_NOT_FOUND);

  private final HttpStatus statusCode;

  private final String status;

  private final String message;
}