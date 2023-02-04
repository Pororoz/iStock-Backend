package com.pororoz.istock.domain.product;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class ProductIntegrationTest extends IntegrationTest {

  @Autowired
  CategoryRepository categoryRepository;

  Category category;

  @Nested
  @DisplayName("POST /v1/products")
  class SaveProduct {

    String uri = "http://localhost:8080/v1/products";
    String name = "product name";
    String number = "product number";
    String codeNumber = "code number";
    long stock = 10;
    String companyName = "company name";
    Long categoryId = 1L;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("제품을 저장한다.")
    void saveProduct() throws Exception {
      //given
      databaseCleanup.execute();
      category = categoryRepository.save(Category.builder().name("카테고리").build());
      SaveProductRequest request = SaveProductRequest.builder().productName(name)
          .productNumber(number).codeNumber(codeNumber).stock(stock).companyName(companyName)
          .categoryId(categoryId).build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
          .andExpect(jsonPath("$.data.productName").value(name))
          .andExpect(jsonPath("$.data.productNumber").value(number))
          .andExpect(jsonPath("$.data.codeNumber").value(codeNumber))
          .andExpect(jsonPath("$.data.stock").value(stock))
          .andExpect(jsonPath("$.data.companyName").value(companyName))
          .andExpect(jsonPath("$.data.categoryId").value(categoryId)).andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("user role은 POST api에 접근할 수 없다.")
    void cannotAccessUser() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder().build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 유저는 POST api에 접근할 수 없다.")
    void cannotAccessAnonymous() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder().build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isForbidden());
    }

  }
}
