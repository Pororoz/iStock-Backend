package com.pororoz.istock.domain.category.dto.response;

import java.util.Objects;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindCategoryResponse extends CategoryResponse{

  private String createdAt;

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
    return super.getId().equals(that.getId()) && super.getName().equals(that.getName()) &&
        Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.getId(), super.getName(), createdAt, updatedAt);
  }
}
