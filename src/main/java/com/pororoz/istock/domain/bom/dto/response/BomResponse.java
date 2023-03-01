package com.pororoz.istock.domain.bom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BomResponse {

  @Schema(description = "bom 아이디", example = "1")
  private Long bomId;

  @Schema(description = "location 번호", example = "L5.L4")
  private String locationNumber;

  @Schema(description = "코드 번호", example = "0A")
  private String codeNumber;

  @Schema(description = "제품 수량", example = "3")
  private Long quantity;

  @Schema(description = "비고", example = "메모")
  private String memo;

  @Schema(description = "part 아이디", example = "1")
  private Long partId;

  @Schema(description = "부제품 번호", example = "2")
  private Long subAssyId;

  @Schema(description = "product 아이디", example = "3")
  private Long productId;
}
