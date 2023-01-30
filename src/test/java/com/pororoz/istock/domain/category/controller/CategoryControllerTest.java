package com.pororoz.istock.domain.category.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.FindCategoryRequest;
import com.pororoz.istock.domain.category.dto.response.FindCategoryResponse;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import com.pororoz.istock.domain.category.service.CategoryService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  @InjectMocks
  CategoryController categoryController;

  @Mock
  CategoryService categoryService;

  @Nested
  @DisplayName("계정 조회")
  class FindUser {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("2번 페이지를 조회한다.")
      void findCategories() {
        //given
        LocalDateTime create = LocalDateTime.now();
        LocalDateTime update = LocalDateTime.now();
        FindCategoryServiceResponse response1 = FindCategoryServiceResponse.builder().id(1L)
            .name("item1").createdAt(create).updatedAt(update).build();
        FindCategoryServiceResponse response2 = FindCategoryServiceResponse.builder().id(1L)
            .name("item2").createdAt(create).updatedAt(update).build();
        List<FindCategoryServiceResponse> findCategoryServiceResponses = List.of(response1, response2);


        long totalCategories = 11L;
        String name = "item";
        int pages = 2;
        int countPerPages = 2;
        LocalDateTime today = LocalDateTime.now();
        FindCategoryRequest request = FindCategoryRequest.builder().name(name)
            .page(pages).size(countPerPages).build();

        Page<FindCategoryServiceResponse> responsePage = new PageImpl<>(findCategoryServiceResponses,
            PageRequest.of(3, countPerPages), totalCategories);
        List<FindCategoryResponse> findCategoryResponses = List.of(
            response1.toResponse(), response2.toResponse());

        //when
        when(categoryService.findCategories(any(FindCategoryServiceRequest.class))).thenReturn(responsePage);
        ResponseEntity<ResultDTO<PageResponse<FindCategoryResponse>>> response
            = categoryController.findCategories(request);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), ResponseStatus.OK);

        PageResponse<FindCategoryResponse> data = Objects.requireNonNull(response.getBody()).getData();
        assertEquals(data.getTotalPages(), (int) (totalCategories + countPerPages) / countPerPages);
        assertEquals(data.getTotalElements(), totalCategories);
        assertEquals(data.getCurrentSize(), countPerPages);
        assertFalse(data.isFirst());
        assertFalse(data.isLast());
        assertIterableEquals(data.getContents(), findCategoryResponses);

        FindCategoryResponse first = data.getContents().get(0);
        String format = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals(first.getCreatedAt(), format);
        assertEquals(first.getUpdatedAt(), format);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }
}