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
import com.pororoz.istock.domain.purchase.dto.service.PurchaseBulkServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseBulkServiceResponse;
import com.pororoz.istock.domain.purchase.service.PurchaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = PurchaseControllerTest.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
public class PurchaseControllerTest extends ControllerTest {

  @MockBean
  PurchaseService purchaseService;

  private Long productId = 1L;

  private long amount = 300L;

  @Nested
  @DisplayName("제품 자재 일괄 구매")
  class PurchaseBulk {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      String url = "https://localhost:8080/v1/purchase/product";
      @Test
      @DisplayName("존재하는 Product와 1이상의 amount를 요청하면 구매 대기 내역을 생성한다.")
      void purchaseBulk() throws Exception {
        // given
        PurchaseBulkRequest request = PurchaseBulkRequest.builder()
            .productId(productId)
            .amount(amount)
            .build();
        PurchaseBulkServiceResponse serviceDto = PurchaseBulkServiceResponse.builder()
            .productId(productId)
            .amount(amount)
            .build();
        PurchaseBulkResponse response = PurchaseBulkService.builder()
            .productId(productId)
            .amount(amount)
            .buid();

        // when
        when(purchaseService.purchaseBulk(any(PurchaseBulkServiceRequest.class))).thenReturn(serviceDto);
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.PURCHASE_BULK))
            .andExpect(jsonPath("$.data"),equalTo(asParsedJson(response)))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }
}
