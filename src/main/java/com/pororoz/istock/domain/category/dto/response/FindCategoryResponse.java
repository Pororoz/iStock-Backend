package com.pororoz.istock.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindCategoryResponse extends CategoryResponse{

  @Schema(description = "생성 시간", example = "2023-01-01 00:00:00")
  private String createdAt;

  @Schema(description = "수정 시간", example = "2023-01-01 00:00:00")
  private String updatedAt;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    FindCategoryResponse that = (FindCategoryResponse) obj;
    return super.getId().equals(that.getId()) && getCategoryName().equals(that.getCategoryName()) &&
        Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getCategoryName(), createdAt, updatedAt);
  }
}
