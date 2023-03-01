package com.pororoz.istock.domain.purchase.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.purchase.dto.request.PurchaseProductRequest;
import com.pororoz.istock.domain.purchase.dto.response.PurchaseProductResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import com.pororoz.istock.domain.purchase.service.PurchaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = PurchaseController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
public class PurchaseControllerTest extends ControllerTest {

  @MockBean
  PurchaseService purchaseService;

  private Long productId = 1L;
  private long quantity = 300L;

  @Nested
  @DisplayName("제품 자재 일괄 구매")
  class PurchaseProduct {

    private final String url(Long productId) {
      return String.format("http://localhost:8080/v1/purchase/products/%s/waiting",productId);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("일괄 구매를 요청하면 구매 대기 내역을 생성한다.")
      void purchaseProduct() throws Exception {
        // given
        PurchaseProductRequest request = PurchaseProductRequest.builder()
            .quantity(quantity)
            .build();
        PurchaseProductServiceResponse serviceDto = PurchaseProductServiceResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();
        PurchaseProductResponse response = PurchaseProductResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(purchaseService.purchaseProduct(any(PurchaseProductServiceRequest.class))).thenReturn(serviceDto);
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.PURCHASE_PRODUCT))
            .andExpect(jsonPath("$.data",equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
      @Test
      @DisplayName("productId가 null이면 오류가 발생한다.")
      void productIdNullException() throws Exception {
        // given
        PurchaseProductRequest request = PurchaseProductRequest.builder()
            .quantity(quantity)
            .build();

        // when
        ResultActions actions = getResultActions(url(null), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("quantity가 1보다 작은면 오류가 발생한다.")
      void quantityNullException() throws Exception {
        // given
        PurchaseProductRequest request = PurchaseProductRequest.builder()
            .quantity(0L)
            .build();

        // when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }
}
