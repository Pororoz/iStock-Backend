package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class RegisteredBySubAssayException extends CustomException {

  public RegisteredBySubAssayException() {
    super(ErrorCode.REGISTERED_BY_SUB_ASSAY);
  }
}
