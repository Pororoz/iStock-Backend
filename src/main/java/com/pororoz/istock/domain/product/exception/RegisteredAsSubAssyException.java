package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class RegisteredAsSubAssyException extends CustomException {

  public RegisteredAsSubAssyException() {
    super(ErrorCode.REGISTERED_AS_SUB_ASSY);
  }
}
