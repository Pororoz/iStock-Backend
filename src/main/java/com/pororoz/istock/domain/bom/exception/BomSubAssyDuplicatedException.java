package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class BomSubAssyDuplicatedException extends CustomException {

  public BomSubAssyDuplicatedException() {
    super(ErrorCode.BOM_SUB_ASSY_DUPLICATED);
  }
}
