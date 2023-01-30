package com.pororoz.istock.domain.category.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

  @NotNull
  private Long id;
  @Size(min = 2, max = 100, message = ExceptionMessage.INVALID_CATEGORY_NAME)
  private String name;

  public UpdateCategoryServiceRequest toService() {
    return UpdateCategoryServiceRequest.builder().id(id).name(name).build();
  }
}
