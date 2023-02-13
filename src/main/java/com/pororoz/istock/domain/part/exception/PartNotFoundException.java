package com.pororoz.istock.domain.part.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class PartNotFoundException extends CustomException {

  public PartNotFoundException() {
    super(ErrorCode.PART_NOT_FOUND);
  }

}
