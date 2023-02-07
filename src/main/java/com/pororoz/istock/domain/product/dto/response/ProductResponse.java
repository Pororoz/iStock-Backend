package com.pororoz.istock.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

  @Schema(description = "제품 아이디", example = "1")
  private Long id;

  @Schema(description = "제품명", example = "인덕션 컨트롤러 V1.2")
  private String productName;

  @Schema(description = "제품 번호", example = "GS-IH-01")
  private String productNumber;

  @Schema(description = "코드 번호", example = "0A")
  private String codeNumber;

  @Schema(description = "재고 수량", example = "0")
  private long stock;

  @Schema(description = "거래처 이름", example = "공신금속")
  private String companyName;

  @Schema(description = "카테고리 아이디", example = "1")
  private Long categoryId;
}
