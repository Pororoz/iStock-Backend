package com.pororoz.istock.domain.file.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class InvalidFileExceptionSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.INVALID_FILE)
  private String status;

  @Schema(description = "상세 메시지", example = ExceptionMessage.INVALID_FILE)
  private String message;
}
