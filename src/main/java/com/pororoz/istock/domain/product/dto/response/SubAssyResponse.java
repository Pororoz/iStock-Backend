package com.pororoz.istock.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubAssyResponse {

  @Schema(description = "part 아이디", example = "1")
  private Long partId;

  @Schema(description = "품명", example = "BEAD")
  private String partName;

  @Schema(description = "규격", example = "BID/3216")

  private String spec;

  @Schema(description = "재고", example = "5")
  private long stock;

  @Schema(description = "소요량", example = "5")
  private long quantity;
}
