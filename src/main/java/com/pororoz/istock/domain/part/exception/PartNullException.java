package com.pororoz.istock.domain.part.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class PartNullException extends CustomException {

  public PartNullException() {
    super(ErrorCode.PART_NULL);
  }

}
