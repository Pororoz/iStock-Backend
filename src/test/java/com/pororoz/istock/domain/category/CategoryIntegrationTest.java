package com.pororoz.istock.domain.category;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.configuration.CustomPageableArgumentResolver;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.SaveCategoryRequest;
import com.pororoz.istock.domain.category.dto.request.UpdateCategoryRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CategoryIntegrationTest extends IntegrationTest {

  @Autowired
  CategoryRepository categoryRepository;

  @Nested
  @DisplayName("GET /v1/categories?categoryName={}&page={}&size={} - 카테고리 리스트 조회")
  @Transactional
  class FindCategories {

    final String url = "/v1/categories";

    MultiValueMap<String, String> params;

    @BeforeEach
    public void setUp() {
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("성공 케이스")
    @WithMockUser
    class SuccessCase {

      @BeforeEach
      void setup() {
        Category category1 = Category.builder().categoryName("item1").build();
        Category category2 = Category.builder().categoryName("shop1").build();
        Category category3 = Category.builder().categoryName("shop2").build();
        Category category4 = Category.builder().categoryName("item2").build();
        Category category5 = Category.builder().categoryName("item3").build();
        Category category6 = Category.builder().categoryName("button1").build();
        Category category7 = Category.builder().categoryName("item4").build();

        categoryRepository.saveAll(
            List.of(category1, category2, category3, category4, category5, category6, category7));
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
        params.add("categoryName", name);
        params.add("page", Integer.toString(page));
        params.add("size", Integer.toString(size));

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

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
            .andExpect(jsonPath("$.data.contents[0].categoryId").value(1L))
            .andExpect(jsonPath("$.data.contents[0].categoryName").value("item1"))
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
        params.add("categoryName", name);
        params.add("page", Integer.toString(page));
        params.add("size", Integer.toString(size));

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

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
            .andExpect(jsonPath("$.data.contents[0].categoryId").value(2L))
            .andExpect(jsonPath("$.data.contents[0].categoryName").value("shop1"))
            .andExpect(jsonPath("$.data.contents[1].categoryId").value(3L))
            .andExpect(jsonPath("$.data.contents[1].categoryName").value("shop2"))
            .andDo(print());
      }

      @Test
      @DisplayName("페이지, 사이즈 값이 모두 들어오면 OK와 전체에 대한 페이지 결과값을 전달해준다.(중간 페이지)")
      void findCategoriesWithAndPageAndSize() throws Exception {
        // given
        int itemCount = 7;
        int page = 1;
        int size = 2;
        params.add("page", Integer.toString(page));
        params.add("size", Integer.toString(size));

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

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
        params.add("page", Integer.toString(page));
        params.add("size", Integer.toString(size));

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

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
        int defaultSize = CustomPageableArgumentResolver.DEFAULT_SIZE;
        params.add("categoryName", name);

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value((itemCount + defaultSize) / defaultSize))
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
        int defaultSize = CustomPageableArgumentResolver.DEFAULT_SIZE;

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(""))
            .andExpect(jsonPath("$.data.totalPages")
                .value((itemCount + defaultSize) / defaultSize))
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

      @Test
      @WithMockUser
      @DisplayName("타입에 맞지 않는 변수를 전달하면 400 Error를 반환한다.")
      void badRequest() throws Exception {
        // given
        String name = "name";
        String page = "not match type";
        String size = "2";
        params.add("categoryName", name);
        params.add("page", page);
        params.add("size", size);

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_PAGE_REQUEST))
            .andDo(print());
      }

      @Test
      @WithAnonymousUser
      @DisplayName(
          "로그인 하지 않으면 수정 API에 접근할 수 없다.")
      void findCategoriesWithNameAndPageAndSizeForItem() throws Exception {
        // given
        String name = "item";
        int page = 0;
        int size = 2;
        params.add("categoryName", name);
        params.add("page", Integer.toString(page));
        params.add("size", Integer.toString(size));

        // when
        ResultActions actions = getResultActions(url, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isForbidden());
      }
    }
  }

  @Nested
  @DisplayName("PUT /v1/categories - 카테고리 수정")
  class UpdateCategory {

    String url = "http://localhost:8080/v1/categories";
    Long categoryId = 1L;
    String oldName = "이전카테고리";
    String newName = "새카테고리";
    Category category = Category.builder().id(categoryId).categoryName(oldName).build();

    @BeforeEach
    void setUp() {
      categoryRepository.save(category);
    }

    @Test
    @WithMockUser
    @DisplayName("카테고리를 수정한다.")
    void updateCategory() throws Exception {
      //given
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().categoryId(categoryId)
          .categoryName(newName)
          .build();

      //when
      ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_CATEGORY))
          .andExpect(jsonPath("$.data.categoryId").value(categoryId))
          .andExpect(jsonPath("$.data.categoryName").value(newName));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인 하지 않으면 수정 API에 접근할 수 없다.")
    void updateCategoryAnonymous() throws Exception {
      //given
      UpdateCategoryRequest request = UpdateCategoryRequest.builder().categoryId(categoryId)
          .categoryName(newName)
          .build();

      //when
      ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

      //given
      actions.andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST /v1/categories - 카테고리 생성 API")
  class SaveCategory {

    private final String url = "http://localhost:8080/v1/categories";

    private String categoryName;

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
      ResultActions actions = getResultActions(url, HttpMethod.POST, request);

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
      ResultActions actions = getResultActions(url, HttpMethod.POST, request);

      //then

      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_CATEGORY_NAME));
    }
  }

  @Nested
  @DisplayName("DELETE /v1/categories/{categoryId} - 카테고리 삭제 API")
  class DeleteCategory {

    private String url(long id) {
      return "http://localhost:8080/v1/categories" + "/" + id;
    }

    private long categoryId;
    private String categoryName;

    @BeforeEach
    void setUp() {
      categoryName = "착화기";
      Category category = Category.builder().categoryName(categoryName).build();
      categoryRepository.save(category);
    }


    @Test
    @WithMockUser
    @DisplayName("존재하는 카테고리를 삭제할 수 있다.")
    void deleteCategory() throws Exception {
      //given
      categoryId = 1L;

      //when
      ResultActions actions = getResultActions(url(categoryId), HttpMethod.DELETE);

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_CATEGORY))
          .andExpect(jsonPath("$.data.categoryId").value(categoryId))
          .andExpect(jsonPath("$.data.categoryName").value(categoryName));
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 카테고리를 삭제하면 CATEGORY_NOT_FOUND를 반환한다.")
    void categoryNotFound() throws Exception {
      //given
      categoryId = 2L;

      //when
      ResultActions actions = getResultActions(url(categoryId), HttpMethod.DELETE);

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.CATEGORY_NOT_FOUND))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.CATEGORY_NOT_FOUND));
    }
  }
}
