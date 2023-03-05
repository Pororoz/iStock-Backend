package com.pororoz.istock.domain.file.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductNotFoundExceptionSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.PRODUCT_NOT_FOUND)
  private String status;

  @Schema(description = "상세 메시지", example = ExceptionMessage.PRODUCT_NOT_FOUND)
  private String message;
}
