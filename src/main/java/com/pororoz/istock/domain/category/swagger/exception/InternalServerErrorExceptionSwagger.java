package com.pororoz.istock.domain.category.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class InternalServerErrorExceptionSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.INTERNAL_SERVER_ERROR)
  private String status;

  @Schema(description = "상세 메시지", example = ExceptionMessage.INTERTNAL_SERVER_ERROR)
  private String message;
}
