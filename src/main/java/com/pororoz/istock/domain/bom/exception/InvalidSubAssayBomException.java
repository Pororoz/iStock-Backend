package com.pororoz.istock.domain.bom.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class InvalidSubAssayBomException extends CustomException {

  public InvalidSubAssayBomException() {
    super(ErrorCode.INVALID_SUB_ASSAY_BOM);
  }
}
