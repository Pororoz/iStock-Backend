package com.pororoz.istock.domain.outbound.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OutboundResponse {

  @Schema(description = "제품 아이디", example = "1")
  private Long productId;

  @Schema(description = "제품 수량", example = "100")
  private Long quantity;
}
