package com.pororoz.istock.domain.bom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindBomRequest {

  @Schema(description = "product 아이디", example = "1")
  @Positive
  private Long productId;

  @Builder
  @ConstructorProperties({"product-id"})
  public FindBomRequest(Long productId) {
    this.productId = productId;
  }
}
