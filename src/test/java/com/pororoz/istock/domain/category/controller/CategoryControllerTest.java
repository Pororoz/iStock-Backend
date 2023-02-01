package com.pororoz.istock.domain.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = CategoryController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class CategoryControllerTest extends ControllerTest {

  @MockBean
  CategoryService categoryService;

  @Nested
  @DisplayName("카테고리 수정")
  class UpdateUser {

    String url = "http://localhost:8080/v1/categories";

    @Test
    @DisplayName("카테고리를 수정한다.")
    void categoryUpdate() throws Exception {
      //given
      Long id = 1L;
      String newName = "새이름";
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().id(id).categoryName(newName)
          .build();
      CategoryServiceResponse serviceDto = CategoryServiceResponse.builder().id(id).name(newName)
          .build();

      //when
      when(categoryService.updateCategory(any(UpdateCategoryServiceRequest.class))).thenReturn(
          serviceDto);
      ResultActions actions = getResultActionsWithBody(request, HttpMethod.PUT, url);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_CATEGORY))
          .andExpect(jsonPath("$.data.id").value(id))
          .andExpect(jsonPath("$.data.categoryName").value(newName));
    }

    @Test
    @DisplayName("카테고리 이름이 1이하이면 예외가 발생한다.")
    void categoryNameLengthMinError() throws Exception {
      //given
      Long id = 1L;
      String newName = "새";
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().id(id).categoryName(newName)
          .build();

      testCategoryNameLength(request);
    }

    @Test
    @DisplayName("카테고리 이름이 16이상이면 예외가 발생한다.")
    void categoryNameLengthMaxError() throws Exception {
      //given
      Long id = 1L;
      String newName = "일이삼사오육칠팔구십십일십이십삼";
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().id(id).categoryName(newName)
          .build();

      testCategoryNameLength(request);
    }

    private void testCategoryNameLength(UpdateCategoryRequest request) throws Exception {
      //when
      ResultActions actions = getResultActionsWithBody(request, HttpMethod.PUT, url);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_CATEGORY_NAME))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("카테고리 생성하기")
  class SaveCategory {

    String url = "http://localhost:8080/v1/categories";

    @Test
    @DisplayName("카테고리 생성을 성공하면 Category 값을 반환한다.")
    void saveCategory() throws Exception {
      // given
      Long id = 1L;
      String categoryName = "착화기";
      SaveCategoryRequest saveCategoryRequest = SaveCategoryRequest.builder()
          .categoryName(categoryName)
          .build();
      CategoryServiceResponse categoryServiceResponse = CategoryServiceResponse.builder()
          .id(id)
          .name(categoryName)
          .build();

      //when
      when(categoryService.saveCategory(any(SaveCategoryServiceRequest.class))).thenReturn(
          categoryServiceResponse);
      ResultActions actions = getResultActionsWithBody(saveCategoryRequest, HttpMethod.POST, url);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_CATEGORY))
          .andExpect(jsonPath("$.data.id").value(id))
          .andExpect(jsonPath("$.data.categoryName").value(categoryName));
    }

    @Test
    @DisplayName("2이상 15이하가 아닌 카테고리 이름을 저장하면 오류가 발생한다.")
    void categoryNameFormatError() throws Exception {
      // given
      String categoryName = "";
      SaveCategoryRequest saveCategoryRequest = SaveCategoryRequest.builder()
          .categoryName(categoryName)
          .build();

      //when
      ResultActions actions = getResultActionsWithBody(saveCategoryRequest, HttpMethod.POST, url);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_CATEGORY_NAME))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("카테고리 삭제하기")
  class DeleteCategory {

    private String url(long id) {
      return "http://localhost:8080/v1/categories" + "/" + id;
    }

    private Long id;

    private String categoryName;


    @Test
    @DisplayName("존재하는 카테고리를 삭제하면 Category 값을 반환한다.")
    void deleteCategory() throws Exception {
      // given
      id = 1L;
      categoryName = "착화기";
      CategoryServiceResponse categoryServiceResponse = CategoryServiceResponse.builder()
          .id(id)
          .name(categoryName)
          .build();

      // when
      when(categoryService.deleteCategory(any(DeleteCategoryServiceRequest.class))).thenReturn(
          categoryServiceResponse);

      ResultActions actions = getResultActionsWithNoBody(HttpMethod.DELETE, url(id));

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_CATEGORY))
          .andExpect(jsonPath("$.data.id").value(id))
          .andExpect(jsonPath("$.data.categoryName").value(categoryName));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 삭제하면 오류가 발생한다.")
    void error() throws Exception {
      //given
      id = 2L;

      when(categoryService.deleteCategory(any(DeleteCategoryServiceRequest.class))).thenThrow(new CategoryNotFoundException());
      ResultActions actions = getResultActionsWithNoBody(HttpMethod.DELETE, url(id));

      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.CATEGORY_NOT_FOUND))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.CATEGORY_NOT_FOUND))
          .andDo(print());
    }
  }
}