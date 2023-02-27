package com.pororoz.istock.domain.production.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ProductStockMinusException extends CustomException {

  public ProductStockMinusException() {
    super(ErrorCode.PRODUCT_STOCK_MINUS);
  }

}
