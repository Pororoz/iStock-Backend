package com.pororoz.istock.domain.category.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.category.dto.service.GetCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.GetCategoryServiceResponse;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import java.time.LocalDateTime;
import java.util.List;
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

      LocalDateTime create;
      LocalDateTime update;
      Category category1;
      Category category2;
      List<Category> categories;

      @BeforeEach
      void setup() {
        create = LocalDateTime.now();
        update = LocalDateTime.now();
        category1 = Category.builder()
            .id(1L)
            .name("item1")
            .createdAt(create)
            .updatedAt(update)
            .build();
        category2 = Category.builder()
            .id(2L)
            .name("item2")
            .createdAt(create)
            .updatedAt(update)
            .build();
        categories = List.of(category1, category2);
      }

      @Test
      @DisplayName("카테고리 이름을 검색해 조회하면 페이지네이션을 한다.")
      void getCategoryWithNameAndPageAndSize() {
        // given
        long totalCategories = 11L;
        int size = 2;
        int page = 3;
        String name = "item";

        GetCategoryServiceRequest getCategoryServiceRequest = GetCategoryServiceRequest.builder()
            .name(name)
            .page(page)
            .size(size)
            .build();
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(page, size),
            totalCategories);
        List<GetCategoryServiceResponse> getCategoryServiceResponses = makeCategoryServiceResponses(
            categories);

        // when
        when(categoryRepository.findAllByNameContaining(any(), any())).thenReturn(pages);
        Page<GetCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

        // then
        assertThat(result.getTotalElements(), equalTo(totalCategories));
        assertThat(result.getTotalPages(),
            equalTo((int) (totalCategories + size) / size));
        assertThat(result.getContent().size(), equalTo(size));
        assertThat(result.getContent().get(0), equalTo(getCategoryServiceResponses.get(0)));
        assertThat(result.getContent().get(1), equalTo(getCategoryServiceResponses.get(1)));
      }

      @Test
      @DisplayName("검색 없이 조회하면 전체를 대상으로 페이지네이션을 한다.")
      void getCategoryWithOnlyPage() {
        // given
        long totalCategories = 11L;
        int size = 2;
        int page = 3;

        GetCategoryServiceRequest getCategoryServiceRequest = GetCategoryServiceRequest.builder()
            .name(null)
            .page(page)
            .size(size)
            .build();
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(page, size),
            totalCategories);
        List<GetCategoryServiceResponse> getCategoryServiceResponses = makeCategoryServiceResponses(
            categories);

        // when
        when(categoryRepository.findAllByNameContaining(any(Pageable.class))).thenReturn(pages);
        Page<GetCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

        // then
        assertThat(result.getTotalElements(), equalTo(totalCategories));
        assertThat(result.getTotalPages(),
            equalTo((int) (totalCategories + size) / size));
        assertThat(result.getContent().size(), equalTo(size));
        assertThat(result.getContent().get(0), equalTo(getCategoryServiceResponses.get(0)));
        assertThat(result.getContent().get(1), equalTo(getCategoryServiceResponses.get(1)));
      }

      @Test
      @DisplayName("page와 size가 null이고 이름만 검색할 경우 이름값에 해당하는 전체 값을 준다.")
      void getCategoryWithOnlyName() {
        // given
        String name = "item";

        GetCategoryServiceRequest getCategoryServiceRequest = GetCategoryServiceRequest.builder()
            .name(name)
            .page(null)
            .size(null)
            .build();
        List<GetCategoryServiceResponse> getCategoryServiceResponses = makeCategoryServiceResponses(
            categories);

        // when
        when(categoryRepository.findAllByNameContaining(any(String.class))).thenReturn(categories);
        Page<GetCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

        // then
        assertIterableEquals(result.getContent(), getCategoryServiceResponses);
        assertEquals(result.getTotalElements(), result.getNumberOfElements());
        assertEquals(result.getTotalPages(), 1);
      }

      @Test
      @DisplayName("page와 size가 null이면 전체를 조회한다.")
      void getCategoryWithNull() {
        // given
        GetCategoryServiceRequest getCategoryServiceRequest = GetCategoryServiceRequest.builder()
            .name(null)
            .page(null)
            .size(null)
            .build();
        List<GetCategoryServiceResponse> getCategoryServiceResponses = makeCategoryServiceResponses(
            categories);

        // when
        when(categoryRepository.findAll()).thenReturn(categories);
        Page<GetCategoryServiceResponse> result = categoryService.findCategories(
            getCategoryServiceRequest);

        // then
        assertIterableEquals(result.getContent(), getCategoryServiceResponses);
        assertEquals(result.getTotalElements(), result.getNumberOfElements());
        assertEquals(result.getTotalPages(), 1);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }

    private List<GetCategoryServiceResponse> makeCategoryServiceResponses(
        List<Category> categories) {
      return categories.stream().map(
              category -> GetCategoryServiceResponse.builder().id(category.getId())
                  .name(category.getName())
                  .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build())
          .toList();
    }
  }
}