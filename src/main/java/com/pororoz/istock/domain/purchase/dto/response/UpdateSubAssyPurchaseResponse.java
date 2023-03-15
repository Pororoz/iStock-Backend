package com.pororoz.istock.domain.purchase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSubAssyPurchaseResponse {

  @Schema(description = "제품IO 아이디", example = "1")
  private Long productIoId;

  @Schema(description = "Sub Assy 아이디", example = "1")
  private Long productId;

  @Schema(description = "구매량", example = "100")
  private long quantity;

}
