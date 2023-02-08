package com.pororoz.istock.domain.product.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProductNotFoundSwagger {

  @Schema(description = "에러 명칭", example = ExceptionStatus.BAD_REQUEST)
  private String status;

  @Schema(description = "상세 메시지", example = ExceptionMessage.PRODUCT_NOT_FOUND)
  private String message;
}
