package com.pororoz.istock.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.DeleteCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @InjectMocks
  CategoryService categoryService;

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
        category1 = Category.builder()
            .id(1L)
            .name("item1")
            .build();
        category2 = Category.builder()
            .id(2L)
            .name("item2")
            .build();
        categories = List.of(category1, category2);
      }

      @Test
      @DisplayName("카테고리 이름을 검색해 조회하면 페이지네이션을 한다.")
      void getCategoryWithNameAndPageAndSize() {
        // given
        int size = 2;
        int page = 3;
        String name = "item";

        FindCategoryServiceRequest getCategoryServiceRequest = FindCategoryServiceRequest.builder()
            .name(name)
            .page(page)
            .size(size)
            .build();
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(page, size),
            totalCategories);
        List<FindCategoryServiceResponse> findCategoryServiceRespons = makeCategoryServiceResponses(
            categories);

        // when
        when(categoryRepository.findAllByNameContaining(any(), any())).thenReturn(pages);
        Page<FindCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

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

        FindCategoryServiceRequest getCategoryServiceRequest = FindCategoryServiceRequest.builder()
            .name(null)
            .page(page)
            .size(size)
            .build();
        List<FindCategoryServiceResponse> findCategoryServiceResponses
            = makeCategoryServiceResponses(categories);
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(0, 20),
            totalCategories);

        // when
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(pages);
        Page<FindCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(totalCategories);
        assertThat(result.getTotalPages()).isEqualTo((int) (totalCategories / size));
        assertThat(result.getContent().size()).isEqualTo(size);
        assertThat(result.getContent().get(0)).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceResponses.get(0));
        assertThat(result.getContent().get(1)).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceResponses.get(1));
      }

      @Test
      @DisplayName("page와 size가 null이고 이름만 검색할 경우 이름값에 해당하는 전체 값을 준다.")
      void getCategoryWithOnlyName() {
        // given
        String name = "item";

        FindCategoryServiceRequest getCategoryServiceRequest = FindCategoryServiceRequest.builder()
            .name(name)
            .page(null)
            .size(null)
            .build();
        List<FindCategoryServiceResponse> findCategoryServiceResponses
            = makeCategoryServiceResponses(categories);
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(0, 20),
            totalCategories);

        // when
        when(categoryRepository.findAllByNameContaining(
            any(String.class), any(Pageable.class))).thenReturn(pages);
        Page<FindCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

        // then
        assertThat(result.getContent()).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceResponses);
        assertEquals(result.getTotalElements(), result.getNumberOfElements());
        assertEquals(result.getTotalPages(), 1);
      }

      @Test
      @DisplayName("page와 size가 null이면 default page설정(page=0, size=20)으로 전체를 조회한다.")
      void getCategoryWithNull() {
        // given
        FindCategoryServiceRequest getCategoryServiceRequest = FindCategoryServiceRequest.builder()
            .name(null)
            .page(null)
            .size(null)
            .build();
        List<FindCategoryServiceResponse> findCategoryServiceResponses
            = makeCategoryServiceResponses(categories);
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(0, 20),
            totalCategories);

        // when
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(pages);
        Page<FindCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

        // then
        assertThat(result.getContent()).usingRecursiveComparison()
            .isEqualTo(findCategoryServiceResponses);
        assertEquals(result.getTotalElements(), result.getNumberOfElements());
        assertEquals(result.getTotalPages(), 1);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }

    private List<FindCategoryServiceResponse> makeCategoryServiceResponses(
        List<Category> categories) {
      return categories.stream().map(
              category -> FindCategoryServiceResponse.builder().id(category.getId())
                  .name(category.getName())
                  .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build())
          .toList();
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
      Category category = Category.builder().id(id).name(oldName).build();
      UpdateCategoryServiceRequest request = UpdateCategoryServiceRequest.builder().id(1L)
          .name(newName).build();
      //when
      when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
      CategoryServiceResponse response = categoryService.updateCategory(request);

      //then
      assertThat(response.getId()).isEqualTo(id);
      assertThat(response.getName()).isEqualTo(newName);
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

    @Nested
    @DisplayName("카테고리 생성 API")
    class SaveCategory {

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
        assertThat(result.getId()).isEqualTo(response.getId());
        assertThat(result.getName()).isEqualTo(response.getName());
      }
    }

    @Nested
    @DisplayName("카테고리 삭제 API")
    class DeleteCategory {


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
        assertThat(result.getId()).isEqualTo(response.getId());
        assertThat(result.getName()).isEqualTo(response.getName());
      }

      @Test
      @DisplayName("존재하지 않는 category를 요청했을 경우, CategoryNotFoundException을 반환한다.")
      void categoryNotFound() {
        //given
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