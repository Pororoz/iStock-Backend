package com.pororoz.istock.domain.bom.dto.request;

import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveBomRequest {
  @Size(max = 20)
  private String locationNumber;

  @Size(max = 20)
  private String codeNumber;

  @PositiveOrZero
  private Long quantity;

  private String memo;

  @NotNull
  private Long partId;

  @NotNull
  private Long productId;

  public SaveBomServiceRequest toService() {
    return SaveBomServiceRequest.builder()
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .partId(partId)
        .productId(productId)
        .build();
  }
}
