package com.pororoz.istock.domain.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.SaveCategoryRequest;
import com.pororoz.istock.domain.category.dto.request.UpdateCategoryRequest;
import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.DeleteCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.service.CategoryService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@WebMvcTest(value = CategoryController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class CategoryControllerTest extends ControllerTest {

  @MockBean
  CategoryService categoryService;

  @Nested
  @DisplayName("?????? ??????")
  class FindUser {

    final String url = "http://localhost:8080/v1/categories";

    MultiValueMap<String, String> params;

    @BeforeEach
    void setup() {
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("2??? ???????????? ????????????.")
      void findCategories() throws Exception {
        //given
        LocalDateTime create = LocalDateTime.now();
        LocalDateTime update = LocalDateTime.now();
        FindCategoryServiceResponse response1 = FindCategoryServiceResponse.builder().categoryId(1L)
            .categoryName("item1").createdAt(create).updatedAt(update).build();
        FindCategoryServiceResponse response2 = FindCategoryServiceResponse.builder().categoryId(2L)
            .categoryName("item2").createdAt(create).updatedAt(update).build();
        List<FindCategoryServiceResponse> findCategoryServiceResponses = List.of(response1,
            response2);

        long totalCategories = 11L;
        String name = "item";
        int pages = 2;
        int countPerPages = 2;

        Page<FindCategoryServiceResponse> responsePage = new PageImpl<>(
            findCategoryServiceResponses,
            PageRequest.of(3, countPerPages), totalCategories);

        params.add("query", name);
        params.add("page", Integer.toString(pages));
        params.add("size", Integer.toString(countPerPages));

        //when
        when(categoryService.findCategories(any(FindCategoryServiceRequest.class),
            any(Pageable.class)))
            .thenReturn(responsePage);
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

        //then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value((totalCategories + countPerPages) / countPerPages))
            .andExpect(jsonPath("$.data.totalElements").value(totalCategories))
            .andExpect(jsonPath("$.data.first").value(false))
            .andExpect(jsonPath("$.data.last").value(false))
            .andExpect(jsonPath("$.data.currentSize").value(2))
            .andExpect(jsonPath("$.data.contents[0].categoryId").value(1L))
            .andExpect(jsonPath("$.data.contents[0].categoryName").value("item1"))
            .andExpect(jsonPath("$.data.contents[1].categoryId").value(2L))
            .andExpect(jsonPath("$.data.contents[1].categoryName").value("item2"))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

    }

    @Nested
    @DisplayName("???????????? ??????")
    class UpdateUser {

      String url = "http://localhost:8080/v1/categories";

      @Test
      @DisplayName("??????????????? ????????????.")
      void categoryUpdate() throws Exception {
        //given
        Long categoryId = 1L;
        String newName = "?????????";
        UpdateCategoryRequest request = UpdateCategoryRequest.builder().categoryId(categoryId)
            .categoryName(newName)
            .build();
        CategoryServiceResponse serviceDto = CategoryServiceResponse.builder()
            .categoryId(categoryId).categoryName(newName)
            .build();

        //when
        when(categoryService.updateCategory(any(UpdateCategoryServiceRequest.class))).thenReturn(
            serviceDto);
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_CATEGORY))
            .andExpect(jsonPath("$.data.categoryId").value(categoryId))
            .andExpect(jsonPath("$.data.categoryName").value(newName));
      }

      @Test
      @DisplayName("???????????? ????????? 1???????????? ????????? ????????????.")
      void categoryNameLengthMinError() throws Exception {
        //given
        Long categoryId = 1L;
        String newName = "???";
        UpdateCategoryRequest request = UpdateCategoryRequest.builder().categoryId(categoryId)
            .categoryName(newName)
            .build();

        testCategoryNameLength(request);
      }

      @Test
      @DisplayName("???????????? ????????? 16???????????? ????????? ????????????.")
      void categoryNameLengthMaxError() throws Exception {
        //given
        Long categoryId = 1L;
        String newName = "????????????????????????????????????????????????";
        UpdateCategoryRequest request = UpdateCategoryRequest.builder().categoryId(categoryId)
            .categoryName(newName)
            .build();

        testCategoryNameLength(request);
      }

      private void testCategoryNameLength(UpdateCategoryRequest request) throws Exception {
        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_CATEGORY_NAME))
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("???????????? ????????????")
  class SaveCategory {

    String url = "http://localhost:8080/v1/categories";

    @Test
    @DisplayName("???????????? ????????? ???????????? Category ?????? ????????????.")
    void saveCategory() throws Exception {
      // given
      Long categoryId = 1L;
      String categoryName = "?????????";
      SaveCategoryRequest request = SaveCategoryRequest.builder()
          .categoryName(categoryName)
          .build();
      CategoryServiceResponse categoryServiceResponse = CategoryServiceResponse.builder()
          .categoryId(categoryId)
          .categoryName(categoryName)
          .build();

      //when
      when(categoryService.saveCategory(any(SaveCategoryServiceRequest.class))).thenReturn(
          categoryServiceResponse);
      ResultActions actions = getResultActions(url, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_CATEGORY))
          .andExpect(jsonPath("$.data.categoryId").value(categoryId))
          .andExpect(jsonPath("$.data.categoryName").value(categoryName));
    }

    @Test
    @DisplayName("2?????? 15????????? ?????? ???????????? ????????? ???????????? ????????? ????????????.")
    void categoryNameFormatError() throws Exception {
      // given
      String categoryName = "";
      SaveCategoryRequest request = SaveCategoryRequest.builder()
          .categoryName(categoryName)
          .build();

      //when
      ResultActions actions = getResultActions(url, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_CATEGORY_NAME))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("???????????? ????????????")
  class DeleteCategory {

    private String url(long id) {
      return "http://localhost:8080/v1/categories" + "/" + id;
    }

    private Long categoryId;

    private String categoryName;

    @Test
    @DisplayName("???????????? ??????????????? ???????????? Category ?????? ????????????.")
    void deleteCategory() throws Exception {
      // given
      categoryId = 1L;
      categoryName = "?????????";
      CategoryServiceResponse categoryServiceResponse = CategoryServiceResponse.builder()
          .categoryId(categoryId)
          .categoryName(categoryName)
          .build();

      // when
      when(categoryService.deleteCategory(any(DeleteCategoryServiceRequest.class))).thenReturn(
          categoryServiceResponse);

      ResultActions actions = getResultActions(url(categoryId), HttpMethod.DELETE);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_CATEGORY))
          .andExpect(jsonPath("$.data.categoryId").value(categoryId))
          .andExpect(jsonPath("$.data.categoryName").value(categoryName));
    }

    @Test
    @DisplayName("???????????? ?????? ??????????????? ???????????? ????????? ????????????.")
    void error() throws Exception {
      //given
      categoryId = 2L;

      when(categoryService.deleteCategory(any(DeleteCategoryServiceRequest.class))).thenThrow(
          new CategoryNotFoundException());
      ResultActions actions = getResultActions(url(categoryId), HttpMethod.DELETE);

      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.CATEGORY_NOT_FOUND))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.CATEGORY_NOT_FOUND))
          .andDo(print());
    }
  }
}