package com.pororoz.istock.domain.part.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartResponse {

  @Schema(description = "부품 아이디", example = "1")
  private Long partId;

  @Schema(description = "품명", example = "BEAD")
  private String partName;

  @Schema(description = "규격", example = "BID/3216")

  private String spec;

  @Schema(description = "단가", example = "10000")
  private long price;

  @Schema(description = "재고", example = "5")
  private long stock;
}
