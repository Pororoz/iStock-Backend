package com.pororoz.istock.domain.part.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class PartIoNotFoundException extends CustomException {

  public PartIoNotFoundException() {
    super(ErrorCode.PART_IO_NOT_FOUND);
  }

}
