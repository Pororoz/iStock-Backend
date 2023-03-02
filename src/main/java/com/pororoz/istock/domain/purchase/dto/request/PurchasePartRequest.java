package com.pororoz.istock.domain.purchase.dto.request;

import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePartRequest {

  @Schema(description = "구매량", example = "100")
  @Positive
  private long quantity;

  public PurchasePartServiceRequest toService() {
    return PurchasePartServiceRequest.builder()
        .quantity(quantity)
        .build();
  }
}
