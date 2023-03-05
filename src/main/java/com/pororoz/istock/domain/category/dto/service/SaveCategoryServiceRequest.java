package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class SaveCategoryServiceRequest {

  private String categoryName;

  public Category toCategory() {
    return Category.builder()
        .categoryName(categoryName)
        .build();
  }
}
