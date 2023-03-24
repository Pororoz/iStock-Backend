package com.pororoz.istock.domain.part.dto.response;

import com.pororoz.istock.domain.part.entity.PartStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPartIoResponse {

  @Schema(description = "부품 IO 아이디", example = "1")
  private Long partIoId;

  @Schema(description = "부품 아이디", example = "1")
  private Long partId;

  @Schema(description = "수량",example = "10")
  private long quantity;

  @Schema(description = "부품 상태", example = "구매대기")
  private PartStatus status;

  @Schema(description = "생성일", example = "2023-01-16 13:01:23")
  private String createdAt;

  @Schema(description = "생성일", example = "2023-01-16 13:01:23")
  private String updatedAt;

  @Schema(description = "제품 IO 아이디", example = "1")
  private Long productIoId;

  @Schema(description = "품명", example = "PCB")
  private String partName;

  @Schema(description = "규격", example = "PCB-01-A")
  private String spec;
}
