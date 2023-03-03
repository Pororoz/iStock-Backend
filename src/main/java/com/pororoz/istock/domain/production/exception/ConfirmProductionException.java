package com.pororoz.istock.domain.production.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ConfirmProductionException extends CustomException {

  public ConfirmProductionException(String expectedStatus, String changingStatus, String detail) {
    super(ErrorCode.CONFIRM_PRODUCTION,
        expectedStatus + "를 " + changingStatus + "로 변경 가능합니다. " + detail);
  }

}
