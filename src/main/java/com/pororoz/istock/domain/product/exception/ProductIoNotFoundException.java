package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ProductIoNotFoundException extends CustomException {

  public ProductIoNotFoundException() {
    super(ErrorCode.PRODUCT_IO_NOT_FOUND);
  }
}
