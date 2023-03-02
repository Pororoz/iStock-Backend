package com.pororoz.istock.domain.purchase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseProductResponse {

  @Schema(description = "제품 아이디", example = "1")
  private Long productId;

  @Schema(description = "구매량", example = "100")
  private long quantity;

}