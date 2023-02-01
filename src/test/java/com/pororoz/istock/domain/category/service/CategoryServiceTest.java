package com.pororoz.istock.domain.category.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @InjectMocks
  CategoryService categoryService;

  @Mock
  CategoryRepository categoryRepository;

  @Nested
  @DisplayName("카테고리 수정")
  class CategoryUpdate {

    String oldName = "이전이름";
    String newName = "새이름";
    Long id = 1L;

    @Test
    @DisplayName("저장된 카테고리 이름을 수정한다.")
    void updateCategory() {
      //given
      Category category = Category.builder().id(id).name(oldName).build();
      UpdateCategoryServiceRequest request = UpdateCategoryServiceRequest.builder().id(1L)
          .name(newName).build();
      //when
      when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
      CategoryServiceResponse response = categoryService.updateCategory(request);

      //then
      assertThat(response.getId(), equalTo(id));
      assertThat(response.getName(), equalTo(newName));
    }

    @Test
    @DisplayName("존재하지 않는 ID의 카테고리는 예외가 발생한다.")
    void throwNotFoundCategoryId() {
      //given
      UpdateCategoryServiceRequest request = UpdateCategoryServiceRequest.builder().id(1L)
          .name(newName).build();

      //when
      when(categoryRepository.findById(id)).thenReturn(Optional.empty());

      //then
      assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(request));
    }
  }
}