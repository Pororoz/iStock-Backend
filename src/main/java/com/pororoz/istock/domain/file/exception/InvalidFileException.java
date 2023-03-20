package com.pororoz.istock.domain.file.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class InvalidFileException extends CustomException {
    public InvalidFileException() {
        super(ErrorCode.INVALID_FILE);
    }
}
