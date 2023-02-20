package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class SubAssyNotFoundByProductNameException extends CustomException {

  public SubAssyNotFoundByProductNameException() {
    super(ErrorCode.SUB_ASSY_NOT_FOUND_BY_PRODUCT_NAME);
  }
}
