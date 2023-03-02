package com.pororoz.istock.domain.production.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class PartStockMinusException extends CustomException {

  public PartStockMinusException() {
    super(ErrorCode.PART_STOCK_MINUS);
  }
}
