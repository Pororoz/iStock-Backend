package com.pororoz.istock.domain.product.dto.request;

import com.pororoz.istock.domain.product.dto.service.FindProductByPartServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindProductByPartRequest {

  @Schema(description = "부품 아이디", example = "1")
  @PositiveOrZero
  private Long partId;

  @Schema(description = "부품명", example = "BEAD")
  private String partName;

  @Builder
  @ConstructorProperties({"part-id", "part-name"})
  public FindProductByPartRequest(Long partId, String partName) {
    this.partId = partId;
    this.partName = partName;
  }
  
  public FindProductByPartServiceRequest toService() {
    return FindProductByPartServiceRequest.builder()
        .partId(partId).partName(partName)
        .build();
  }
}
