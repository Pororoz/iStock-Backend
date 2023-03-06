package com.pororoz.istock.domain.production.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ConfirmProductionExceptionSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.CONFIRM_PRODUCTION)
  private String status;

  @Schema(description = "상세 메시지", example = ExceptionMessage.CONFIRM_PRODUCTION)
  private String message;
}