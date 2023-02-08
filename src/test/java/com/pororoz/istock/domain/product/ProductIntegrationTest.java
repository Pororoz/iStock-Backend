package com.pororoz.istock.domain.product;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.request.UpdateProductRequest;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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
  ProductRepository productRepository;

  @Autowired
  CategoryRepository categoryRepository;

  final String uri = "http://localhost:8080/v1/products";
  final String name = "product name";
  final String number = "product number";
  final String codeNumber = "code number";
  final long stock = 10;
  final String companyName = "company name";

  @Nested
  @DisplayName("POST /v1/products - 제품 생성")
  class SaveProduct {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("제품을 저장한다.")
    void saveProductAdmin() throws Exception {
      //given
      databaseCleanup.execute();
      Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(name).productNumber(number)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(category.getId())
          .build();

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
          .andExpect(jsonPath("$.data.categoryId").value(category.getId())).andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("user role로 제품을 저장한다.")
    void saveProductUser() throws Exception {
      //given
      databaseCleanup.execute();
      Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(name).productNumber(number)
          .categoryId(category.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
          .andExpect(jsonPath("$.data.productName").value(name))
          .andExpect(jsonPath("$.data.productNumber").value(number))
          .andExpect(jsonPath("$.data.codeNumber").value(nullValue()))
          .andExpect(jsonPath("$.data.stock").value(0))
          .andExpect(jsonPath("$.data.companyName").value(nullValue()))
          .andExpect(jsonPath("$.data.categoryId").value(category.getId()))
          .andDo(print());
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

  @Nested
  @DisplayName("PUT /v1/products - 제품 수정")
  class UpdateProduct {

    long newStock = stock + 2;
    String newName = "product name";
    String newNumber = "product number";
    String newCodeNumber = "code number";
    String newCompanyName = "company name";
    Product product;
    Category category1, category2;

    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
      category1 = categoryRepository.save(Category.builder().categoryName("카테고리1").build());
      category2 = categoryRepository.save(Category.builder().categoryName("카테고리2").build());
      product = productRepository.save(
          Product.builder().productName(name)
              .productNumber(number).codeNumber(codeNumber)
              .companyName(companyName).stock(stock)
              .category(category1)
              .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("제품을 수정한다.")
    void saveProduct() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response)))).andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("stock이 null이면 0으로 수정된다.")
    void defaultStockZeroProduct() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .companyName(newCompanyName).categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(product.getId())
          .productName(newName).productNumber(newNumber)
          .codeNumber(newCodeNumber).stock(0)
          .companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response)))).andDo(print());
    }
  }
}
