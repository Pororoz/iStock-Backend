package com.pororoz.istock.domain.part.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class PartNameDuplicatedException extends CustomException {

  public PartNameDuplicatedException() {
    super(ErrorCode.PART_NAME_DUPLICATED);
  }
}
