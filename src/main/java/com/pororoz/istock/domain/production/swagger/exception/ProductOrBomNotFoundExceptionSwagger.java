package com.pororoz.istock.domain.production.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProductOrBomNotFoundExceptionSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.PRODUCT_OR_BOM_NOT_FOUND)
  private String status;

  @Schema(description = "상세 메시지", example = ExceptionMessage.PRODUCT_OR_BOM_NOT_FOUND)
  private String message;
}
