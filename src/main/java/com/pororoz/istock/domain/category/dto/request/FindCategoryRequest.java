package com.pororoz.istock.domain.category.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindCategoryRequest {

  @Schema(description = "카테고리 이름", example = "착화기")
  @Nullable
  private String query;

  @Schema(description = "페이지 요청", example = "0")
  @Nullable
  @PositiveOrZero(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer page;

  @Schema(description = "사이즈 요청", example = "20")
  @Nullable
  @Positive(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer size;

  public FindCategoryServiceRequest toService() {
    return FindCategoryServiceRequest.builder().name(query).page(page).size(size).build();
  }
}
