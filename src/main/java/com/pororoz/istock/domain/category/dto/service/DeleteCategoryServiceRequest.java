package com.pororoz.istock.domain.category.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCategoryServiceRequest {

  private Long id;
}
