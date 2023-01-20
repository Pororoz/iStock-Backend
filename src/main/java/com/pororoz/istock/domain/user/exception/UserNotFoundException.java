package com.pororoz.istock.domain.user.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
