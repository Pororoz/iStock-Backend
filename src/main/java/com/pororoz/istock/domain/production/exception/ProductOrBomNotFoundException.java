package com.pororoz.istock.domain.production.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ProductOrBomNotFoundException extends CustomException {

  public ProductOrBomNotFoundException() {
    super(ErrorCode.PRODUCT_OR_BOM_NOT_FOUND);
  }
}
