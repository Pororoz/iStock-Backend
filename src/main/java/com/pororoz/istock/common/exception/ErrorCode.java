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
  SUB_ASSY_CANNOT_HAVE_SUB_ASSY(HttpStatus.BAD_REQUEST,
      ExceptionStatus.SUB_ASSY_CANNOT_HAVE_SUB_ASSY,
      ExceptionMessage.SUB_ASSY_CANNOT_HAVE_SUB_ASSY),
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
      ExceptionMessage.SELF_DEMOTE_ROLE),
  SELF_DELETE_ACCOUNT(HttpStatus.BAD_REQUEST, ExceptionStatus.SELF_DELETE_ACCOUNT,
      ExceptionMessage.SELF_DELETE_ACCOUNT),
  INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, ExceptionStatus.BAD_REQUEST,
      ExceptionMessage.INVALID_PAGE_REQUEST),
  PRODUCT_STOCK_MINUS(HttpStatus.BAD_REQUEST, ExceptionStatus.PRODUCT_STOCK_MINUS,
      ExceptionMessage.PRODUCT_STOCK_MINUS),
  PART_STOCK_MINUS(HttpStatus.BAD_REQUEST, ExceptionStatus.PART_STOCK_MINUS,
      ExceptionMessage.PART_STOCK_MINUS),
  PRODUCT_OR_BOM_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.PRODUCT_OR_BOM_NOT_FOUND,
      ExceptionMessage.PRODUCT_OR_BOM_NOT_FOUND),
  BOM_AND_SUB_ASSY_NOT_MATCHED(HttpStatus.BAD_REQUEST, ExceptionStatus.BOM_AND_SUB_ASSY_NOT_MATCHED,
      ExceptionMessage.BOM_AND_SUB_ASSY_NOT_MATCHED),
  BOM_SUB_ASSY_DUPLICATED(HttpStatus.BAD_REQUEST,
      ExceptionStatus.BOM_SUB_ASSY_DUPLICATED,
      ExceptionMessage.BOM_SUB_ASSY_DUPLICATED),
  SUB_ASSY_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.SUB_ASSY_NOT_FOUND,
      ExceptionMessage.SUB_ASSY_NOT_FOUND),
  PART_IO_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.PART_IO_NOT_FOUND,
      ExceptionMessage.PART_IO_NOT_FOUND),
  PRODUCT_IO_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionStatus.PRODUCT_IO_NOT_FOUND,
      ExceptionMessage.PRODUCT_IO_NOT_FOUND),
  CHANGE_IO_STATUS(HttpStatus.BAD_REQUEST, ExceptionStatus.CHANGE_IO_STATUS,
      ExceptionMessage.CHANGE_IO_STATUS);
  private final HttpStatus statusCode;

  private final String status;

  private final String message;
}