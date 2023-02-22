package com.pororoz.istock.domain.category.dto.request;

import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindCategoryRequest {

  @Parameter(description = "카테고리 이름", example = "착화기")
  @Nullable
  private String categoryName;

  public FindCategoryServiceRequest toService() {
    return FindCategoryServiceRequest.builder().categoryName(categoryName).build();
  }
}
