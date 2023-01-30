package com.pororoz.istock.domain.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.UpdateCategoryRequest;
import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = CategoryController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
@MockBean(JpaMetamodelMappingContext.class)
class CategoryControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

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
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().id(id).name(newName).build();
      CategoryServiceResponse serviceDto = CategoryServiceResponse.builder().id(id).name(newName)
          .build();

      //when
      when(categoryService.updateCategory(any(UpdateCategoryServiceRequest.class))).thenReturn(
          serviceDto);
      ResultActions actions = mockMvc.perform(put(url)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
          .with(csrf()));

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_CATEGORY))
          .andExpect(jsonPath("$.data.id").value(id))
          .andExpect(jsonPath("$.data.name").value(newName));
    }
  }
}