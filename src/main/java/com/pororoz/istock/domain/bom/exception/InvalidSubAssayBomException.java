package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class InvalidSubAssyBomException extends CustomException {

  public InvalidSubAssyBomException() {
    super(ErrorCode.INVALID_SUB_ASSY_BOM);
  }
}
