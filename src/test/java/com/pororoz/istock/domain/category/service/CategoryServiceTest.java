package com.pororoz.istock.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @InjectMocks
  CategoryService categoryService;

  @Mock
  ProductRepository productRepository;

  @Mock
  CategoryRepository categoryRepository;

  @Nested
  @DisplayName("카테고리 조회 API")
  class GetCategory {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      Long totalCategories;
      Category category1;
      Category category2;
      List<Category> categories;

      @BeforeEach
      void setup() {
        totalCategories = 11L;
        category1 = Category.builder().id(1L).categoryName("item1").build();
        category2 = Category.builder().id(2L).categoryName("item2").build();
        categories = List.of(category1, category2);
      }

      @Test
      @DisplayName("카테고리 이름을 검색해 조회하면 페이지네이션을 한다.")
      void getCategoryWithNameAndPageAndSize() {
        // given
        int size = 2;
        int page = 3;
        String name = "item";
        Pageable pageable = PageRequest.of(page, size);
        FindCategoryServiceRequest getCategoryServiceRequest = FindCategoryServiceRequest.builder()
            .categoryName(name).build();
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(page, size),
            totalCategories);
        List<FindCategoryServiceResponse> findCategoryServiceRespons = makeCategoryServiceResponses(
            categories);

        // when
        when(categoryRepository.findAllByCategoryNameContaining(any(), any())).thenReturn(pages);
        Page<FindCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(totalCategories);
        assertThat(result.getTotalPages()).isEqualTo((int) (totalCategories + size) / size);
        assertThat(result.getContent().size()).isEqualTo(size);
        assertThat(result.getContent().get(0)).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceRespons.get(0));
        assertThat(result.getContent().get(1)).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceRespons.get(1));
      }

      @Test
      @DisplayName("검색 없이 조회하면 전체를 대상으로 페이지네이션을 한다.")
      void getCategoryWithOnlyPage() {
        // given
        totalCategories = 2L;
        int size = 2;
        int page = 0;
        Pageable pageable = PageRequest.of(page, size);
        FindCategoryServiceRequest categoryServiceRequest = FindCategoryServiceRequest.builder()
            .categoryName(null).build();
        List<FindCategoryServiceResponse> findCategoryServiceResponses = makeCategoryServiceResponses(
            categories);
        PageImpl<Category> pages = new PageImpl<>(categories, pageable,
            totalCategories);

        // when
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(pages);
        Page<FindCategoryServiceResponse> result = categoryService.findCategories(
            categoryServiceRequest, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(totalCategories);
        assertThat(result.getTotalPages()).isEqualTo((int) (totalCategories / size));
        assertThat(result.getContent().size()).isEqualTo(size);
        assertThat(result.getContent().get(0)).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceResponses.get(0));
        assertThat(result.getContent().get(1)).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceResponses.get(1));
      }

      @Nested
      @DisplayName("실패 케이스")
      class FailCase {

      }

      private List<FindCategoryServiceResponse> makeCategoryServiceResponses(
          List<Category> categories) {
        return categories.stream().map(
            category -> FindCategoryServiceResponse.builder().categoryId(category.getId())
                .categoryName(category.getCategoryName()).createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt()).build()).toList();
      }
    }

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
        Category category = Category.builder().id(id).categoryName(oldName).build();
        UpdateCategoryServiceRequest request = UpdateCategoryServiceRequest.builder().categoryId(1L)
            .categoryName(newName).build();
        //when
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        CategoryServiceResponse response = categoryService.updateCategory(request);

        //then
        assertThat(response.getCategoryId()).isEqualTo(id);
        assertThat(response.getCategoryName()).isEqualTo(newName);
      }

      @Test
      @DisplayName("존재하지 않는 ID의 카테고리는 예외가 발생한다.")
      void throwNotFoundCategoryId() {
        //given
        UpdateCategoryServiceRequest request = UpdateCategoryServiceRequest.builder().categoryId(1L)
            .categoryName(newName).build();

        //when
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.updateCategory(request));
      }


    }

    @Nested
    @DisplayName("카테고리 생성 API")
    class SaveCategory {

      @Test
      @DisplayName("카테고리를 생성한다.")
      void saveCategory() {
        //given
        String name = "착화기";
        SaveCategoryServiceRequest saveCategoryServiceRequest = SaveCategoryServiceRequest.builder()
            .categoryName(name).build();
        Category category = Category.builder().id(1L).categoryName(name).build();
        CategoryServiceResponse response = CategoryServiceResponse.builder().categoryId(1L)
            .categoryName(name).build();

        //when
        when(categoryRepository.save(any())).thenReturn(category);
        CategoryServiceResponse result = categoryService.saveCategory(saveCategoryServiceRequest);

        //then
        assertThat(result.getCategoryId()).isEqualTo(response.getCategoryId());
        assertThat(result.getCategoryName()).isEqualTo(response.getCategoryName());
      }
    }

    @Nested
    @DisplayName("카테고리 삭제 API")
    class DeleteCategory {

      final Long categoryId = 1L;
      final String name = "착화기";
      final Category category = Category.builder().id(categoryId).categoryName(name).build();


      @Test
      @DisplayName("카테고리를 삭제한다.")
      void deleteCategory() {
        //given
        CategoryServiceResponse response = CategoryServiceResponse.builder().categoryId(1L)
            .categoryName(name).build();

        //when
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategory(category)).thenReturn(false);
        CategoryServiceResponse result = categoryService.deleteCategory(categoryId);

        //then
        assertThat(result.getCategoryId()).isEqualTo(response.getCategoryId());
        assertThat(result.getCategoryName()).isEqualTo(response.getCategoryName());
      }

      @Test
      @DisplayName("존재하지 않는 category를 요청했을 경우 예외가 발생한다.")
      void categoryNotFound() {
        //given
        //when
        //then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.deleteCategory(categoryId));
      }

      @Test
      @DisplayName("category와 연관된 product가 존재하면 예외가 발생한다.")
      void categoryCannotDelete() {
        //given
        //when
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategory(category)).thenReturn(true);

        //then
        assertThrows(DataIntegrityViolationException.class,
            () -> categoryService.deleteCategory(categoryId));
      }
    }
  }
}