package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.domain.category.dto.response.CategoryResponse;
import com.pororoz.istock.domain.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryServiceResponse {

    private Long id;

    private String name;

    public static CategoryServiceResponse of(Category category) {
        return CategoryServiceResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
    public CategoryResponse toResponse() {
        return CategoryResponse.builder()
                .id(id)
                .name(name)
                .build();
    }

}
