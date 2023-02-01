package com.pororoz.istock.domain.category;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.SaveCategoryRequest;
import com.pororoz.istock.domain.category.dto.request.UpdateCategoryRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
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
      databaseCleanup.execute();
      categoryRepository.save(category);
    }

    @Test
    @WithMockUser
    @DisplayName("카테고리를 수정한다.")
    void updateCategory() throws Exception {
      //given
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().id(id).categoryName(newName)
          .build();

      //when
      ResultActions actions = getResultActionsWithBody(request, HttpMethod.PUT, url);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_CATEGORY))
          .andExpect(jsonPath("$.data.id").value(id))
          .andExpect(jsonPath("$.data.categoryName").value(newName));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인 하지 않으면 수정 API에 접근할 수 없다.")
    void updateCategoryAnonymous() throws Exception {
      //given
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().id(id).categoryName(newName)
          .build();

      //when
      ResultActions actions = getResultActionsWithBody(request, HttpMethod.PUT, url);

      //then
      actions.andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST /v1/categories - 카테고리 생성 API")
  class SaveCategory {

    private final String url = "http://localhost:8080/v1/categories";

    private String categoryName;

    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 카테고리를 넘겨주면 카테고리 생성에 성공한다.")
    void saveCategory() throws Exception {
      //given
      categoryName = "착화기";
      SaveCategoryRequest request = SaveCategoryRequest.builder()
          .categoryName(categoryName)
          .build();

      //when
      ResultActions actions = getResultActionsWithBody(request, HttpMethod.POST, url);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_CATEGORY))
          .andExpect(jsonPath("$.data.categoryName").value(categoryName));
    }

    @Test
    @WithMockUser
    @DisplayName("2이상 15이하의 크기가 아닌 카테고리를 입력하면 에러가 발생한다.")
    void invalidFormat() throws Exception {
      //given
      categoryName = "";
      SaveCategoryRequest request = SaveCategoryRequest.builder()
          .categoryName(categoryName)
          .build();

      //when
      ResultActions actions = getResultActionsWithBody(request, HttpMethod.POST, url);

      //then

      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_CATEGORY_NAME));
    }
  }

  @Nested
  @DisplayName("DELETE /v1/categories/{id} - 카테고리 삭제 API")
  class DeleteCategory {

    private String url(long id) {
      return "http://localhost:8080/v1/categories" + "/" + id;
    }
    private long id;
    private String categoryName;

    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
      categoryName = "착화기";
      Category category = Category.builder().name(categoryName).build();
      categoryRepository.save(category);
    }


    @Test
    @WithMockUser
    @DisplayName("존재하는 카테고리를 삭제할 수 있다.")
    void deleteCategory() throws Exception {
      //given
      id = 1L;

      //when
      ResultActions actions = getResultActionsWithNoBody(HttpMethod.DELETE, url(id));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_CATEGORY))
          .andExpect(jsonPath("$.data.id").value(id))
          .andExpect(jsonPath("$.data.categoryName").value(categoryName));
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 카테고리를 삭제하면 CATEGORY_NOT_FOUND를 반환한다.")
    void categoryNotFound() throws Exception {
      //given
      id = 2L;

      //when
      ResultActions actions = getResultActionsWithNoBody(HttpMethod.DELETE, url(id));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.CATEGORY_NOT_FOUND))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.CATEGORY_NOT_FOUND));
    }
  }
}


