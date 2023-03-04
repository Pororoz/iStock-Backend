package com.pororoz.istock.domain.production.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ChangeIoStatusException extends CustomException {

  public ChangeIoStatusException(String expectedStatus, String changingStatus, String detail) {
    super(ErrorCode.CHANGE_IO_STATUS,
        expectedStatus + "를 " + changingStatus + "로 변경할 수 있습니다. " + detail);
  }

}
