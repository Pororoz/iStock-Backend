package com.pororoz.istock.domain.outbound.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class ChangeOutboundStatusException extends CustomException {

  public ChangeOutboundStatusException() {
    super(ErrorCode.CHANGE_OUTBOUND_STATUS);
  }
}
