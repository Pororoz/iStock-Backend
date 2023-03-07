package com.pororoz.istock.domain.purchase.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ChangePurchaseStatusException extends CustomException {

  public ChangePurchaseStatusException(String expectedStatus, String changingStatus,
      String detail) {
    super(ErrorCode.CHANGE_IO_STATUS,
        expectedStatus + "를 " + changingStatus + "로 변경할 수 있습니다. " + detail);
  }

}
