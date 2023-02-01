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
import com.pororoz.istock.domain.category.dto.request.UpdateCategoryRequest;
import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
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
}