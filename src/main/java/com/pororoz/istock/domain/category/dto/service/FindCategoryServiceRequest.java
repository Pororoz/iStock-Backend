package com.pororoz.istock.domain.category.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindCategoryServiceRequest {

  private String categoryName;
}
