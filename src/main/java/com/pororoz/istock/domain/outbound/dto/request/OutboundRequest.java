package com.pororoz.istock.domain.outbound.dto.request;

import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutboundRequest {

  @Schema(description = "제품 수량", example = "100")
  @NotNull
  @Positive
  private long quantity;

  public OutboundServiceRequest toService(Long productId) {
    return OutboundServiceRequest.builder()
        .productId(productId)
        .quantity(quantity)
        .build();
  }
}
