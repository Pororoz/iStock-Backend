package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class NotExistedPart extends CustomException {

  public NotExistedPart() {
    super(ErrorCode.NOT_EXISTED_PART);
  }
}
