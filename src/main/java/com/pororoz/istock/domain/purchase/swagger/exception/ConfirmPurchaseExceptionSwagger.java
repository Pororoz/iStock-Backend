package com.pororoz.istock.domain.purchase.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public class ConfirmPurchaseExceptionSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.CONFIRM_PURCHASE)
  private String status;

  @Schema(description = "상세 메세지", example = ExceptionMessage.CONFIRM_PURCHASE)
  private String message;


}
