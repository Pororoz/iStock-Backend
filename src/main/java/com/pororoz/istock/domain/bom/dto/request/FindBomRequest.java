package com.pororoz.istock.domain.bom.dto.request;

import com.pororoz.istock.domain.bom.dto.service.FindBomServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import java.beans.ConstructorProperties;
import lombok.Builder;

public class FindBomRequest {
  @Schema(description = "product 아이디", example = "1")
  @Positive
  private Long productId;

  @Builder
  @ConstructorProperties({"product-id"})
  public FindBomRequest(Long productId) {
    this.productId = productId;
  }

  public FindBomServiceRequest toService() {
    return FindBomServiceRequest.builder()
        .productId(productId)
        .build();
  }
}
