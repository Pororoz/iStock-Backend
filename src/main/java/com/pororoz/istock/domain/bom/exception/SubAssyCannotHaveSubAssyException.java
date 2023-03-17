package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class SubAssyCannotHaveSubAssyException extends CustomException {

  public SubAssyCannotHaveSubAssyException() {
    super(ErrorCode.SUB_ASSY_CANNOT_HAVE_SUB_ASSY);
  }
}
