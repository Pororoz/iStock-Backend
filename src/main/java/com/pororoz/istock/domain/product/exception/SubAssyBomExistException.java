package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class SubAssyBomExistException extends CustomException {

  public SubAssyBomExistException() {
    super(ErrorCode.SUB_ASSY_BOM_EXIST);
  }
}
