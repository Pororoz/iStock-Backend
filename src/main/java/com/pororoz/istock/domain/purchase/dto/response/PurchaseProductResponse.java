package com.pororoz.istock.domain.purchase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseProductResponse {

  @Schema(description = "제품 아이디", example = "1")
  @NotNull
  private Long productId;


  @Schema(description = "구매량", example = "100")
  @Positive
  private long amount;

}