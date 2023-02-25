package com.pororoz.istock.domain.production.dto.request;

import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceRequest;
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
public class SaveProductionRequest {

  @NotNull
  @Positive
  private long amount;

  public SaveProductionServiceRequest toService(Long productId) {
    return SaveProductionServiceRequest.builder()
        .productId(productId).amount(amount)
        .build();
  }
}
