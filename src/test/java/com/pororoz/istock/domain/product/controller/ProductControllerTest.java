package com.pororoz.istock.domain.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceResponse;
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

  @Nested
  @DisplayName("product 저장")
  class SaveProduct {

    String name = "productName";
    String number = "productNumber";
    String codeNumber = "codeNumber";
    long stock = 1;
    String companyName = "companyName";
    Long categoryId = 1L;

    String uri = "http://localhost:8080/v1/products";

    @Test
    @DisplayName("product를 정상적으로 저장한다.")
    void saveProduct() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder().productName(name)
          .productNumber(number).codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId).build();
      SaveProductServiceResponse serviceResponse = SaveProductServiceResponse.builder()
          .productName(name).productNumber(number).codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId).build();

      //when
      when(productService.saveProduct(any(SaveProductServiceRequest.class))).thenReturn(
          serviceResponse);
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
          .andExpect(jsonPath("$.data.productName").value(name))
          .andExpect(jsonPath("$.data.productNumber").value(number))
          .andExpect(jsonPath("$.data.codeNumber").value(codeNumber))
          .andExpect(jsonPath("$.data.stock").value(stock))
          .andExpect(jsonPath("$.data.companyName").value(companyName))
          .andExpect(jsonPath("$.data.categoryId").value(categoryId))
          .andDo(print());
    }

    @Test
    @DisplayName("productName은 null이면 예외가 발생한다.")
    void productNameNullException() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder().productName(null)
          .productNumber(number).codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId).build();

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
      SaveProductRequest request = SaveProductRequest.builder().productName(name)
          .productNumber(null).codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId).build();

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
      SaveProductRequest request = SaveProductRequest.builder().productName(name)
          .productNumber(number).codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(null).build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }
  }
}