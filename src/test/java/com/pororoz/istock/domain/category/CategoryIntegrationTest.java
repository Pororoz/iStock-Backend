package com.pororoz.istock.domain.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.common.service.DatabaseCleanup;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.SaveCategoryRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryIntegrationTest {
  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  DatabaseCleanup databaseCleanup;

  @AfterEach
  public void afterEach() {
    databaseCleanup.execute();
  }

  @Nested
  @DisplayName("POST /v1/categories - 카테고리 생성 API")
  @Transactional
  class SaveCategories {

    private final String url = "/v1/categories";

    private String categoryName;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하지 않는 카테고리를 넘겨주면 카테고리 생성에 성공한다.")
      void saveCategory() throws Exception {
        // given
        categoryName = "착화기";
        String request = objectMapper.writeValueAsString(SaveCategoryRequest.builder()
            .categoryName(categoryName)
            .build());

        // when
        ResultActions actions = mockMvc.perform(post(url)
            .content(request)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_CATEGORY))
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.name").value(categoryName))
            .andDo(print());
      }
    }
    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("1이상 15이하의 크기가 아닌 카테고리를 입력하면 에러가 발생한다.")
      void invalidFormat() throws Exception {
        //given
        SaveCategoryRequest request = SaveCategoryRequest.builder()
            .categoryName("")
            .build();

        //when
        ResultActions actions = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_CATEGORY_FORMAT))
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("DELETE /v1/categories/{id} 카테고리 삭제 API")
  @Transactional
  class DeleteCategories {

    private String url(long id) {
      return "/v1/categories" + "/" + id;
    }

    private Long id;
    private String categoryName;

    @BeforeEach
    public void beforeEach() {
      id = 1L;
      categoryName = "착화기";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 카테고리를 삭제할 수 있다.")
      void deleteCategory() throws Exception {
        // given
        Category category = Category.builder().name(categoryName).build();
        categoryRepository.save(category);

        // when
        ResultActions actions = mockMvc.perform(delete(url(id))
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_CATEGORY))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.name").value(categoryName))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 카테고리를 삭제하면 CATEGORY_NOT_FOUND를 반환한다..")
      void categoryNotFound() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(delete(url(id))
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.CATEGORY_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.CATEGORY_NOT_FOUND))
            .andDo(print());
      }
    }
  }
}
