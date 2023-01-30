package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.domain.category.entity.Category;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindCategoryServiceResponse {

  private Long id;

  private String name;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public static FindCategoryServiceResponse of(Category category) {
    return FindCategoryServiceResponse.builder().id(category.getId()).name(category.getName())
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
    FindCategoryServiceResponse that = (FindCategoryServiceResponse) obj;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name)
        && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt,
        that.updatedAt);
  }
}
