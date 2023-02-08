package com.pororoz.istock.domain.bom.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class NotExistedPartExceptionSwagger {
  @Schema(description = "에러 명칭", example = ExceptionStatus.NOT_EXISTED_PART)
  private String status;

  @Schema(description = "상세 메시지", example = ExceptionMessage.NOT_EXISTED_PART)
  private String message;
}
