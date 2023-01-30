package com.pororoz.istock.domain.category.dto.request;

import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateCategoryRequest {

  private Long id;
  private String name;

  public UpdateCategoryServiceRequest toService() {
    return UpdateCategoryServiceRequest.builder().id(id).name(name).build();
  }
}
