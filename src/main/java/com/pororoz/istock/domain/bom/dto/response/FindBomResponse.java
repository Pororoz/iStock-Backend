package com.pororoz.istock.domain.bom.dto.response;

import com.pororoz.istock.domain.part.dto.PartDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBomResponse {
  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;

  @Schema(description = "생성 시간", example = "2023-01-01 00:00:00")
  private String createdAt;

  @Schema(description = "수정 시간", example = "2023-01-01 00:00:01")
  private String updatedAt;
  private PartDto part;
}
