package com.pororoz.istock.domain.product.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.request.UpdateProductRequest;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class ProductControllerTest extends ControllerTest {

  @MockBean
  ProductService productService;

  String uri = "http://localhost:8080/v1/products";
  Long id = 1L;
  String name = "productName";
  String number = "productNumber";
  String codeNumber = "codeNumber";
  long stock = 1;
  String companyName = "companyName";
  Long categoryId = 1L;
  Category category = Category.builder().id(categoryId).build();

  @Nested
  @DisplayName("product 저장")
  class SaveProduct {

    @Test
    @DisplayName("product를 정상적으로 저장한다.")
    void saveProduct() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(name).productNumber(number)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId)
          .build();
      ProductServiceResponse serviceResponse = ProductServiceResponse.builder()
          .productId(id).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      when(productService.saveProduct(any(SaveProductServiceRequest.class))).thenReturn(
          serviceResponse);
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(id).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("productName은 null이면 예외가 발생한다.")
    void productNameNullException() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(null).productNumber(number)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("productNumber는 null이면 예외가 발생한다.")
    void productNumberNullException() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .productNumber(null).productName(name)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("cagegoryId는 null이면 예외가 발생한다.")
    void categoryIdNullException() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .categoryId(null).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("product 수정")
  class ProductUpdate {

    @Test
    @DisplayName("product를 수정한다.")
    void updateProduct() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();
      ProductServiceResponse serviceDto = ProductServiceResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      when(productService.updateProduct(any(UpdateProductServiceRequest.class))).thenReturn(
          serviceDto);
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("id가 null이면 예외가 발생한다.")
    void idNullException() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(null).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }

    @Test
    @DisplayName("product name이 null이면 예외가 발생한다.")
    void productNameNullException() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(1L).productName(null)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }

    @Test
    @DisplayName("categoryId가 null이면 예외가 발생한다.")
    void categoryIdNullException() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(null)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("product 삭제")
  class DeleteProduct {

    @Test
    @DisplayName("product를 삭제한다.")
    void deleteProduct() throws Exception {
      //given
      ProductServiceResponse serviceDto = ProductServiceResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      //when
      when(productService.deleteProduct(1L)).thenReturn(serviceDto);
      ResultActions actions = getResultActions(uri + "/1", HttpMethod.DELETE);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("product id를 지정하지 않으면 not found가 발생한다.")
    void productIdNull() throws Exception {
      //given
      //when
      ResultActions actions = getResultActions(uri + "/", HttpMethod.DELETE);

      //then
      actions.andExpect(status().isNotFound())
          .andDo(print());
    }

    @Test
    @DisplayName("product id가 음수이면 bad request가 발생한다.")
    void productIdNegative() throws Exception {
      //given
      //when
      ResultActions actions = getResultActions(uri + "/-1", HttpMethod.DELETE);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }
  }
}