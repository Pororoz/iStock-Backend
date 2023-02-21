package com.pororoz.istock.domain.purchase.dto.request;

import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseProductRequest {

  @Schema(description = "제품 아이디", example = "1")
  @NotNull
  private Long productId;


  @Schema(description = "구매량", example = "100")
  @Positive
  private long amount;

  public PurchaseProductServiceRequest toService() {
    return PurchaseProductServiceRequest.builder()
        .productId(productId)
        .amount(amount)
        .build();
  }
}
