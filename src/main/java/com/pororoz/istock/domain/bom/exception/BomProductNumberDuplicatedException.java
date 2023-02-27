package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class BomProductNumberDuplicatedException extends CustomException {

  public BomProductNumberDuplicatedException() {
    super(ErrorCode.BOM_PRODUCT_NUMBER_DUPLICATED);
  }
}
