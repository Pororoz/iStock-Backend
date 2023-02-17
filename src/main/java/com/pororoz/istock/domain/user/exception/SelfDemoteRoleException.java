package com.pororoz.istock.domain.user.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class SelfDemoteRoleException extends CustomException {

  public SelfDemoteRoleException() {
    super(ErrorCode.SELF_DEMOTE_ROLE);
  }
}
