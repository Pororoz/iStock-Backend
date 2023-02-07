package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ProductNameDuplicatedException extends CustomException {

  public ProductNameDuplicatedException() {
    super(ErrorCode.PRODUCT_NAME_DUPLICATED);
  }
}
