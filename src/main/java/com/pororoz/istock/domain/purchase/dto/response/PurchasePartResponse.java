package com.pororoz.istock.domain.purchase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchasePartResponse {

  @Schema(description = "부품 아이디", example = "1")
  private Long partId;

  @Schema(description = "구매량", example = "100")
  private long amount;

}
