package com.pororoz.istock.common.exception;

public class BomAndSubAssyNotMatchedException extends CustomException {

  public BomAndSubAssyNotMatchedException() {
    super(ErrorCode.BOM_AND_SUB_ASSY_NOT_MATCHED);
  }
}
