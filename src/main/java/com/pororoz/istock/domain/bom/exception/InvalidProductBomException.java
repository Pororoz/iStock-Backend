package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class InvalidProductBomException extends CustomException {

  public InvalidProductBomException() {
    super(ErrorCode.INVALID_PRODUCT_BOM);
  }
}
