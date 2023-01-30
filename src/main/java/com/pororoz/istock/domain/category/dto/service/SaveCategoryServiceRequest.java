package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.domain.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveCategoryServiceRequest {

  private String name;

  public Category toCategory(String name) {
    return Category.builder()
        .name(name)
        .build();
  }
}
