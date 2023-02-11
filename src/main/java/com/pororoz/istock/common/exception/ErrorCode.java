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
  PRODUCT_NUMBER_DUPLICATED(HttpStatus.BAD_REQUEST, ExceptionStatus.PRODUCT_NUMBER_DUPLICATED,
      ExceptionMessage.PRODUCT_NUMBER_DUPLICATED),
  PART_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, ExceptionStatus.PART_NAME_DUPLICATED,
      ExceptionMessage.PART_NAME_DUPLICATED),
  NOT_EXISTED_PART(HttpStatus.BAD_REQUEST, ExceptionStatus.NOT_EXISTED_PART,
      ExceptionMessage.NOT_EXISTED_PART),
  PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, ExceptionStatus.PRODUCT_NOT_FOUND,
      ExceptionMessage.PRODUCT_NOT_FOUND),
  DUPLICATE_BOM(HttpStatus.BAD_REQUEST, ExceptionStatus.DUPLICATE_BOM,
      ExceptionMessage.DUPLICATE_BOM),
  BOM_NOT_FOUND(HttpStatus.BAD_REQUEST, ExceptionStatus.BOM_NOT_FOUND,
      ExceptionMessage.BOM_NOT_FOUND),
  ;

  private final HttpStatus statusCode;

  private final String status;

  private final String message;
}