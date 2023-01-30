package com.pororoz.istock.domain.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.common.service.DatabaseCleanup;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import java.util.List;
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
  @DisplayName("GET /v1/categories?query={}&page={}&size={} - 카테고리 리스트 조회")
  @Transactional
  class FindCategories {
    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      String url() {
        return "/v1/categories";
      }

      String url(String name) {
        return "/v1/categories" + "?query=" + name;
      }

      String url(int page, int size) {
        return "/v1/categories" + "?page=" + page + "&size=" + size;
      }

      String url(String name, int page, int size) {
        return "/v1/categories" + "?query=" + name + "&page=" + page + "&size=" + size;
      }

      @BeforeEach
      void setup() {
        Category category1 = Category.builder().name("item1").build();
        Category category2 = Category.builder().name("shop1").build();
        Category category3 = Category.builder().name("shop2").build();
        Category category4 = Category.builder().name("item2").build();
        Category category5 = Category.builder().name("item3").build();
        Category category6 = Category.builder().name("button1").build();
        Category category7 = Category.builder().name("item4").build();


        categoryRepository.saveAll(
            List.of(category1, category2, category3, category4, category5,category6, category7));
      }

      @Test
      @DisplayName(
          "카테고리 이름(=item), 페이지, 사이즈 값이 모두 들어오면 OK와 이름에 따른 검색 결과값을 전달해준다. (첫번째 페이지)")
      void findCategoriesWithNameAndPageAndSizeForItem() throws Exception {
        // given
        String name = "item";
        int itemCount = 4;
        int page = 0;
        int size = 2;
        String requestUrl = url(name, page, size);

        // when
        ResultActions actions = mockMvc.perform(get(requestUrl)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value(itemCount / size))
            .andExpect(jsonPath("$.data.totalElements").value(itemCount))
            .andExpect(jsonPath("$.data.first").value(true))
            .andExpect(jsonPath("$.data.last").value(false))
            .andExpect(jsonPath("$.data.currentSize").value(2))
            .andExpect(jsonPath("$.data.contents[0].id").value(1L))
            .andExpect(jsonPath("$.data.contents[0].name").value("item1"))
            .andDo(print());
      }

      @Test
      @DisplayName(
          "카테고리 이름(=shop), 페이지, 사이즈 값이 모두 들어오면 OK와 이름에 따른 검색 결과값을 전달해준다. "
              + "(첫번째 페이지이자 마지막 페이지)"
      )
      void findCategoriesWithNameAndPageAndSizeForShop() throws Exception {
        // given
        String name = "shop";
        int itemCount = 2;
        int page = 0;
        int size = 2;
        String requestUrl = url(name, page, size);

        // when
        ResultActions actions = mockMvc.perform(get(requestUrl)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value(itemCount / size))
            .andExpect(jsonPath("$.data.totalElements").value(itemCount))
            .andExpect(jsonPath("$.data.first").value(true))
            .andExpect(jsonPath("$.data.last").value(true))
            .andExpect(jsonPath("$.data.currentSize").value(2))
            .andExpect(jsonPath("$.data.contents[0].id").value(2L))
            .andExpect(jsonPath("$.data.contents[0].name").value("shop1"))
            .andExpect(jsonPath("$.data.contents[1].id").value(3L))
            .andExpect(jsonPath("$.data.contents[1].name").value("shop2"))
            .andDo(print());
      }

      @Test
      @DisplayName("페이지, 사이즈 값이 모두 들어오면 OK와 전체에 대한 페이지 결과값을 전달해준다.(중간 페이지)")
      void findCategoriesWithAndPageAndSize() throws Exception {
        // given
        int itemCount = 7;
        int page = 1;
        int size = 2;
        String requestUrl = url(page, size);

        // when
        ResultActions actions = mockMvc.perform(get(requestUrl)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value((itemCount + size) / size))
            .andExpect(jsonPath("$.data.totalElements").value(itemCount))
            .andExpect(jsonPath("$.data.first").value(false))
            .andExpect(jsonPath("$.data.last").value(false))
            .andExpect(jsonPath("$.data.currentSize").value(2))
            .andDo(print());
      }

      @Test
      @DisplayName("페이지, 사이즈 값이 모두 들어오면 OK와 전체에 대한 페이지 결과값을 전달해준다.(마지막 페이지)")
      void findCategoriesLastPage() throws Exception {
        // given
        int itemCount = 7;
        int page = 3;
        int size = 2;
        String requestUrl = url(page, size);

        // when
        ResultActions actions = mockMvc.perform(get(requestUrl)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value((itemCount + size) / size))
            .andExpect(jsonPath("$.data.totalElements").value(itemCount))
            .andExpect(jsonPath("$.data.first").value(false))
            .andExpect(jsonPath("$.data.last").value(true))
            .andExpect(jsonPath("$.data.currentSize").value(1))
            .andDo(print());
      }

      @Test
      @DisplayName("카테고리 이름값만 받으면 default page(page=0, size=20)로 페이지네이션된 값이 반환된다.")
      void findCategoriesWithName() throws Exception {
        // given
        int itemCount = 4;
        String name = "item";
        String requestUrl = url(name);
        int defaultSize = FindCategoryServiceRequest.DEFAULT_SIZE;

        // when
        ResultActions actions = mockMvc.perform(get(requestUrl)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value((itemCount+defaultSize) / defaultSize))
            .andExpect(jsonPath("$.data.totalElements").value(itemCount))
            .andExpect(jsonPath("$.data.first").value(true))
            .andExpect(jsonPath("$.data.last").value(true))
            .andExpect(jsonPath("$.data.currentSize").value(itemCount))
            .andDo(print());
      }

      @Test
      @DisplayName(
          "아무값도 쿼리로 넘겨주지 않으면 전체를 대상으로 default page(page=0, size=20)로 페이지네이션된 값이 반환된다.")
      void findCategoriesWithNull() throws Exception {
        // given
        int itemCount = 7;
        String requestUrl = url();
        int defaultSize = FindCategoryServiceRequest.DEFAULT_SIZE;

        // when
        ResultActions actions = mockMvc.perform(get(requestUrl)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value((itemCount+defaultSize) / defaultSize))
            .andExpect(jsonPath("$.data.totalElements").value(itemCount))
            .andExpect(jsonPath("$.data.first").value(true))
            .andExpect(jsonPath("$.data.last").value(true))
            .andExpect(jsonPath("$.data.currentSize").value(itemCount))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }
}
