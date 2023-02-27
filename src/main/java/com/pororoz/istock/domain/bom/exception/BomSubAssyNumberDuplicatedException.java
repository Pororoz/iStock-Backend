package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class BomSubAssyNumberDuplicatedException extends CustomException {

  public BomSubAssyNumberDuplicatedException() {
    super(ErrorCode.BOM_SUB_ASSY_NUMBER_DUPLICATED);
  }
}
