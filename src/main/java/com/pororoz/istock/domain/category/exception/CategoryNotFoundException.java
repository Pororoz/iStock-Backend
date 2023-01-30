package com.pororoz.istock.domain.category.exception;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;

public class CategoryNotFoundException extends CustomException {

  public CategoryNotFoundException() {
    super(ErrorCode.CATEGORY_NOT_FOUND);
  }
}
