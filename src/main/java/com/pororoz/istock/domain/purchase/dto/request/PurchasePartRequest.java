package com.pororoz.istock.domain.purchase.dto.request;

import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
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
public class PurchasePartRequest {

  @Schema(description = "부품 아이디", example = "1")
  @NotNull
  private Long partId;

  @Schema(description = "구매량", example = "100")
  @Positive
  private long amount;

  public PurchasePartServiceRequest toService() {
    return PurchasePartServiceRequest.builder()
        .partId(partId)
        .amount(amount)
        .build();
  }
}
