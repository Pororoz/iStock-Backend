package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.domain.category.dto.response.CategoryResponse;
import com.pororoz.istock.domain.category.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryServiceResponse {

  private Long categoryId;
  private String categoryName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CategoryServiceResponse of(Category category) {
    return CategoryServiceResponse.builder()
        .categoryId(category.getId())
        .categoryName(category.getCategoryName())
        .createdAt(category.getCreatedAt())
        .updatedAt(category.getUpdatedAt())
        .build();
  }

  public CategoryResponse toResponse() {
    return CategoryResponse.builder()
        .categoryId(categoryId)
        .categoryName(categoryName)
        .build();
  }

}
