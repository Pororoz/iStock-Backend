package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.domain.category.entity.Category;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCategoryServiceResponse {

  private Long id;

  private String name;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public static GetCategoryServiceResponse of(Category category) {
    return GetCategoryServiceResponse.builder().id(category.getId()).name(category.getName())
        .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    GetCategoryServiceResponse that = (GetCategoryServiceResponse) obj;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name)
        && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt,
        that.updatedAt);
  }
}
