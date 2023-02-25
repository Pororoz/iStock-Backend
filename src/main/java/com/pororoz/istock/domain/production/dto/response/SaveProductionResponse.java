package com.pororoz.istock.domain.production.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveProductionResponse {

  @Schema(description = "제품 아이디", example = "1")
  private Long productId;

  @Schema(description = "수량", example = "100")
  private long amount;
}
