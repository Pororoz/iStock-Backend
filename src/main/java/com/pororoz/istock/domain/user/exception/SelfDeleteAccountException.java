package com.pororoz.istock.domain.user.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class SelfDeleteAccountException extends CustomException {

  public SelfDeleteAccountException() {
    super(ErrorCode.SELF_DELETE_ACCOUNT);
  }

}
