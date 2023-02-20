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
  PART_DUPLICATED(HttpStatus.BAD_REQUEST, ExceptionStatus.PART_DUPLICATED,
      ExceptionMessage.PART_DUPLICATED),
  PART_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.PART_NOT_FOUND,
      ExceptionMessage.PART_NOT_FOUND),
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.PRODUCT_NOT_FOUND,
      ExceptionMessage.PRODUCT_NOT_FOUND),
  DUPLICATE_BOM(HttpStatus.BAD_REQUEST, ExceptionStatus.DUPLICATE_BOM,
      ExceptionMessage.DUPLICATE_BOM),
  BOM_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.BOM_NOT_FOUND,
      ExceptionMessage.BOM_NOT_FOUND),
  INVALID_SUB_ASSY_BOM(HttpStatus.BAD_REQUEST, ExceptionStatus.INVALID_SUB_ASSY_BOM,
      ExceptionMessage.INVALID_SUB_ASSY_BOM),
  INVALID_PRODUCT_BOM(HttpStatus.BAD_REQUEST, ExceptionStatus.INVALID_PRODUCT_BOM,
      ExceptionMessage.INVALID_PRODUCT_BOM),
  SUB_ASSY_BOM_EXIST(HttpStatus.BAD_REQUEST, ExceptionStatus.SUB_ASSY_BOM_EXIST,
      ExceptionMessage.SUB_ASSY_BOM_EXIST),
  REGISTERED_AS_SUB_ASSY(HttpStatus.BAD_REQUEST, ExceptionStatus.REGISTERED_AS_SUB_ASSY,
      ExceptionMessage.REGISTERED_AS_SUB_ASSY),
  SUB_ASSY_NOT_FOUND_BY_PRODUCT_NAME(HttpStatus.NOT_FOUND,
      ExceptionStatus.SUB_ASSY_NOT_FOUND_BY_PRODUCT_NAME,
      ExceptionMessage.SUB_ASSY_NOT_FOUND_BY_PRODUCT_NAME),
  SELF_DEMOTE_ROLE(HttpStatus.BAD_REQUEST, ExceptionStatus.SELF_DEMOTE_ROLE,
      ExceptionMessage.SELF_DEMOTE_ROLE);

  private final HttpStatus statusCode;

  private final String status;

  private final String message;
}