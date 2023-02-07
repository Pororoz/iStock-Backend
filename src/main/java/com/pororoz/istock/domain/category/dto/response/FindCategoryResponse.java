package com.pororoz.istock.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindCategoryResponse extends CategoryResponse {

  @Schema(description = "생성 시간", example = "2023-01-01 00:00:00")
  private String createdAt;

  @Schema(description = "수정 시간", example = "2023-01-01 00:00:00")
  private String updatedAt;
}
