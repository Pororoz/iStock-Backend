package com.pororoz.istock.domain.part.dto.request;

import com.pororoz.istock.domain.part.dto.service.FindPartServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPartRequest {

  @Schema(description = "부품 아이디", example = "1")
  private Long partId;

  @Schema(description = "품명", example = "BEAD")
  private String partName;

  @Schema(description = "규격", example = "HBA3580PL")
  private String spec;

  @Builder
  @ConstructorProperties({"part-id", "part-name", "spec"})
  public FindPartRequest(Long partId, String partName, String spec) {
    this.partId = partId;
    this.partName = partName;
    this.spec = spec;
  }

  public FindPartServiceRequest toService() {
    return FindPartServiceRequest.builder()
        .partId(partId).partName(partName).spec(spec)
        .build();
  }
}
