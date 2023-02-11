package com.pororoz.istock.domain.bom.dto.request;

import com.pororoz.istock.domain.bom.dto.service.DeleteBomServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteBomRequest {

  @NotNull
  @PositiveOrZero
  private Long bomId;

  public DeleteBomServiceRequest toService() {
    return DeleteBomServiceRequest.builder()
        .bomId(bomId)
        .build();
  }
}
