package com.pororoz.istock.domain.purchase.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ConfirmPurchaseException extends CustomException {

  public ConfirmPurchaseException(String expectedStatus, String changingStatus, String detail) {
    super(ErrorCode.CONFIRM_PURCHASE,
        expectedStatus + "를 " + changingStatus + "로 변경 가능합니다. " + detail);
  }

}
