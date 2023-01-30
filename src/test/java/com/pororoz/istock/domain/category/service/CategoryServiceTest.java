package com.pororoz.istock.domain.category.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;

import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

      @Test
      @DisplayName("검색을 하지 않았을 때 페이지네이션을 한 ")
      void getCategory() {
        // given
        LocalDateTime create = LocalDateTime.now();
        LocalDateTime update = LocalDateTime.now();
        long totalCategories = 10L;
        int size = 2;
        int page = 3;
        Category category1 = Category.builder()
            .id(1L)
            .name("item1")
            .createdAt(create)
            .updatedAt(update)
            .build();
        Category category2 = Category.builder()
            .id(2L)
            .name("item2")
            .createdAt(create)
            .updatedAt(update)
            .build();
        List<Category> categories = List.of(category1, category2);

        GetCategoryServiceRequest getCategoryServiceRequest = GetCategoryServiceRequest.builder()
            .name()
            .page()
            .size()
            .build();
        PageImpl<Category> pages = new PageImpl<>(categories, PageRequest.of(page, size), totalCategories);
        List<GetCategoryServiceResponse> getCategoryServiceResponses = makeCategoryServiceResponses(categories);

        // when
        when(categoryRepository.findByName(any())).thenReturn(pages);
        Page<GetCategoryServiceResponse> result = categoryService.findCategories(getCategoryServiceRequest);

        // then
        assertThat(result.getTotalElements(), equalTo(totalCategories));
        assertThat(result.getTotalPages(),
            equalTo((int) (totalCategories + size) / size));
        assertThat(result.getContent().size(), equalTo(size));
        assertThat(result.getContent().get(0), equalTo(getCategoryServiceResponses.get(0)));
        assertThat(result.getContent().get(1), equalTo(getCategoryServiceResponses.get(1)));
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }

    private List<GetCategoryServiceRequest> makeCategoryServiceResponses(List<Category> categories) {
      return categories.stream().map(
          category -> GetCategoryServiceRequest.builder().id(category.getId()).name(category.getName())
              .createAt(category.getCreatedAt()).updateAt(category.getUpdatedAt()).build()).toList();
    }
  }
}