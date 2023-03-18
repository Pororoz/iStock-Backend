package com.pororoz.istock.domain.product.dto.response;

import com.pororoz.istock.domain.product.entity.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductIoResponse {

  @Schema(description = "제품 IO 아이디", example = "1")
  private Long productIoId;

  @Schema(description = "수량", example = "10")
  private long quantity;

  @Schema(description = "제품 상태", example = "생산대기")
  private ProductStatus status;

  @Schema(description = "생성일", example = "2023-01-16 13:01:23")
  private String createdAt;

  @Schema(description = "생성일", example = "2023-01-16 13:01:23")
  private String updatedAt;

  @Schema(description = "상위", example = "1")
  private Long superIoId;
  private Long productId;
  private String productName;
  private String productNumber;

  //private String isReady 추후 구현 필요
}
