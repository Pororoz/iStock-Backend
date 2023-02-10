package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class DuplicateBomException extends CustomException {

  public DuplicateBomException() {
    super(ErrorCode.DUPLICATE_BOM);
  }
}
