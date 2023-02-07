package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ProductNumberDuplicatedException extends CustomException {

  public ProductNumberDuplicatedException() {
    super(ErrorCode.PRODUCT_NUMBER_DUPLICATED);
  }
}
