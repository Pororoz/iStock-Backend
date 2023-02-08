package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.category.dto.response.FindCategoryResponse;
import com.pororoz.istock.domain.category.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindCategoryServiceResponse {

  private Long categoryId;

  private String categoryName;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public static FindCategoryServiceResponse of(Category category) {
    return FindCategoryServiceResponse.builder().categoryId(category.getId())
        .categoryName(category.getCategoryName())
        .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build();
  }

  public FindCategoryResponse toResponse() {
    return FindCategoryResponse.builder()
        .categoryId(categoryId)
        .categoryName(categoryName)
        .createdAt(TimeEntity.formatTime(createdAt))
        .updatedAt(TimeEntity.formatTime(updatedAt))
        .build();
  }
}
