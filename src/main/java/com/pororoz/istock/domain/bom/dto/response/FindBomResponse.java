package com.pororoz.istock.domain.bom.dto.response;

import com.pororoz.istock.domain.part.dto.response.PartResponse;
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

  @Schema(description = "생성 시간", example = "2023-01-01 00:00:00")
  private String createdAt;

  @Schema(description = "수정 시간", example = "2023-01-01 00:00:01")
  private String updatedAt;
  private PartResponse part;
}
