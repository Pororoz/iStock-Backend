package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class InvalidSubAssyTypeException extends CustomException {

  public InvalidSubAssyTypeException() {
    super(ErrorCode.INVALID_SUB_ASSY_TYPE);
  }

}
