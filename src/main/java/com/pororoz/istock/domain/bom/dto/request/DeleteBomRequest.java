package com.pororoz.istock.domain.bom.dto.request;

import com.pororoz.istock.domain.bom.dto.service.DeleteBomServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteBomRequest {

  @Schema(description = "BOM 아이디", example = "1")
  @NotNull
  @PositiveOrZero
  private Long bomId;

  public DeleteBomServiceRequest toService() {
    return DeleteBomServiceRequest.builder()
        .bomId(bomId)
        .build();
  }
}
