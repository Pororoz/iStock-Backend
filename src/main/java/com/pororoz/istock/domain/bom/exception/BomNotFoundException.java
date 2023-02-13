package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class BomNotFoundException extends CustomException {
  public BomNotFoundException() {
    super(ErrorCode.BOM_NOT_FOUND);
  }
}
