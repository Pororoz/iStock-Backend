package com.pororoz.istock.domain.category.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "카테고리 아이디", example = "1")
  @NotNull
  private Long categoryId;
  @Schema(description = "카테고리 이름", example = "착화기")
  @Size(min = 2, max = 15, message = ExceptionMessage.INVALID_CATEGORY_NAME)
  private String categoryName;

  public UpdateCategoryServiceRequest toService() {
    return UpdateCategoryServiceRequest.builder().categoryId(categoryId).categoryName(categoryName)
        .build();
  }
}
