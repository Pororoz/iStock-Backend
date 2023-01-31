package com.pororoz.istock.domain.category.service;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.DeleteCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
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
  @DisplayName("카테고리 생성 API")
  class SaveCategory {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
      @Test
      @DisplayName("카테고리를 생성한다.")
      void saveCategory() {
        //given
        String name = "착화기";
        SaveCategoryServiceRequest saveCategoryServiceRequest = SaveCategoryServiceRequest.builder()
            .name(name).build();
        Category category = Category.builder()
            .id(1L)
            .name(name)
            .build();
        CategoryServiceResponse response = CategoryServiceResponse.builder()
            .id(1L)
            .name(name)
            .build();

        //when
        when(categoryRepository.save(any())).thenReturn(category);
        CategoryServiceResponse result = categoryService.saveCategory(saveCategoryServiceRequest);

        //then
        assertThat(result.getId(), equalTo(response.getId()));
        assertThat(result.getName(), equalTo(response.getName()));
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase{

    }
  }

  @Nested
  @DisplayName("카테고리 삭제 API")
  class DeleteCategory {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("카테고리를 삭제한다.")
      void deleteCategory() {
        //given
        String name = "착화기";
        DeleteCategoryServiceRequest deleteCategoryServiceRequest = DeleteCategoryServiceRequest.builder()
            .id(1L).build();
        Category category = Category.builder()
            .id(1L)
            .name(name)
            .build();
        CategoryServiceResponse response = CategoryServiceResponse.builder()
            .id(1L)
            .name(name)
            .build();

        //when
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(category));
        CategoryServiceResponse result = categoryService.deleteCategory(
            deleteCategoryServiceRequest);

        //then
        assertThat(result.getId(), equalTo(response.getId()));
        assertThat(result.getName(), equalTo(response.getName()));
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
      @Test
      @DisplayName("존재하지 않는 category를 요청했을 경우, CategoryNotFoundException을 반환한다.")
      void categoryNotFound() {
        //given
        String name = "anonymous";
        DeleteCategoryServiceRequest deleteCategoryServiceRequest = DeleteCategoryServiceRequest.builder()
            .id(1L).build();

        //when

        //then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.deleteCategory(deleteCategoryServiceRequest));
      }
    }
  }
}