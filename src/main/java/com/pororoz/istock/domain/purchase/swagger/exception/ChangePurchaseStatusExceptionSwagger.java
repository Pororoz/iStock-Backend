package com.pororoz.istock.domain.purchase.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ChangePurchaseStatusExceptionSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.CHANGE_IO_STATUS)
  private String status;

  @Schema(description = "상세 메세지", example = ExceptionMessage.CHANGE_IO_STATUS)
  private String message;


}
