package com.pororoz.istock.domain.part.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PartNotFoundExceptionSwagger {
  @Schema(description = "에러 명칭", example = ExceptionStatus.PART_NOT_FOUND)
  private String status;

  @Schema(description = "상세 메세지", example = ExceptionMessage.PART_NOT_FOUND)
  private String message;

}
