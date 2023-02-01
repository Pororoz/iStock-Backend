package com.pororoz.istock.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CategoryResponse {

  @Schema(description = "카테고리 아이디", example = "1")
  private Long id;

  @Schema(description = "카테고리 이름", example = "착화기")
  private String categoryName;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }

    CategoryResponse response = (CategoryResponse) obj;
    return id.equals(response.getId()) &&
        categoryName.equals(response.getCategoryName());
  }
}
