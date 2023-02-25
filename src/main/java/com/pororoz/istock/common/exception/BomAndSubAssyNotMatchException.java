package com.pororoz.istock.common.exception;

public class BomAndSubAssyNotMatchException extends CustomException {

  public BomAndSubAssyNotMatchException() {
    super(ErrorCode.BOM_AND_SUB_ASSY_NOT_MATCH);
  }
}
