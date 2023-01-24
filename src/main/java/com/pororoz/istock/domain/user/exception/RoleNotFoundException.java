package com.pororoz.istock.domain.user.exception;


import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class RoleNotFoundException extends CustomException {
    public RoleNotFoundException() {
        super(ErrorCode.ROLE_NOT_FOUND);
    }
}
