package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class NotExistedPartException extends CustomException {

  public NotExistedPartException() {
    super(ErrorCode.NOT_EXISTED_PART);
  }
}
