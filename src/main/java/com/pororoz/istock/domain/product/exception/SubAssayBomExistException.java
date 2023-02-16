package com.pororoz.istock.domain.product.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class SubAssayBomExistException extends CustomException {

  public SubAssayBomExistException() {
    super(ErrorCode.SUB_ASSAY_BOM_EXIST);
  }
}
