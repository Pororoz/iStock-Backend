package com.pororoz.istock.domain.category.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindCategoryRequest {

  @Nullable
  private String name;

  @Nullable
  @PositiveOrZero(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer page;

  @Nullable
  @Positive(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer size;

  public FindCategoryServiceRequest toService() {
    return FindCategoryServiceRequest.builder().name(name).page(page).size(size).build();
  }
}
