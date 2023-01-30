package com.pororoz.istock.domain.category;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.UpdateCategoryRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class CategoryIntegrationTest extends IntegrationTest {

  @Autowired
  CategoryRepository categoryRepository;

  @Nested
  @DisplayName("PUT /v1/categories")
  class UpdateCategory {

    String url = "http://localhost:8080/v1/categories";
    Long id = 1L;
    String oldName = "이전카테고리";
    String newName = "새카테고리";
    Category category = Category.builder().id(id).name(oldName).build();

    @BeforeEach
    void setUp() {
      categoryRepository.save(category);
    }

    @Test
    @DisplayName("카테고리를 수정한다.")
    void updateCategory() throws Exception {
      //given
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().id(id).categoryName(newName)
          .build();

      //when
      ResultActions actions = mockMvc.perform(put(url)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
          .with(csrf()));

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_CATEGORY))
          .andExpect(jsonPath("$.data.id").value(id))
          .andExpect(jsonPath("$.data.categoryName").value(newName));
    }
  }
}
