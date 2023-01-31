package com.pororoz.istock.domain.category.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveCategoryRequest {

  @Schema(description = "카테고리 이름", example = "착화기")
  @Size(min = 1, max = 15, message = ExceptionMessage.INVALID_CATEGORY_FORMAT)
  private String categoryName;

  public SaveCategoryServiceRequest toService() {
    return SaveCategoryServiceRequest.builder()
        .name(categoryName)
        .build();
  }
}
