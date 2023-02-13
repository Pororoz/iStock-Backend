package com.pororoz.istock.domain.part.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class PartDuplicatedException extends CustomException {

  public PartDuplicatedException() {
    super(ErrorCode.PART_DUPLICATED);
  }
}
