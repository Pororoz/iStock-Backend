package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class NotExistedProduct extends CustomException {

  public NotExistedProduct() {
    super(ErrorCode.NOT_EXISTED_PRODUCT);
  }
}
