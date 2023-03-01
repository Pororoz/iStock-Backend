package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class SubAssyNotFoundException extends CustomException {

  public SubAssyNotFoundException() {
    super(ErrorCode.SUB_ASSY_NOT_FOUND);
  }
}