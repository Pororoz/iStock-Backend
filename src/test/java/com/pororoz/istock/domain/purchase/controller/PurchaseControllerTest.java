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
import com.pororoz.istock.domain.purchase.dto.request.PurchasePartRequest;
import com.pororoz.istock.domain.purchase.dto.request.PurchaseProductRequest;
import com.pororoz.istock.domain.purchase.dto.response.ConfirmPurchasePartResponse;
import com.pororoz.istock.domain.purchase.dto.response.PurchasePartResponse;
import com.pororoz.istock.domain.purchase.dto.response.PurchaseProductResponse;
import com.pororoz.istock.domain.purchase.dto.service.ConfirmPurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
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

  final Long productId = 1L;
  final long quantity = 300L;
  final Long partId = 1L;
  final Long partIoId = 1L;

  @Nested
  @DisplayName("?????? ?????? ?????? ??????")
  class PurchaseProduct {

    String url(Long productId) {
      return String.format("http://localhost:8080/v1/purchase/products/%s/waiting", productId);
    }

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("?????? ????????? ???????????? ?????? ?????? ????????? ????????????.")
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
        when(purchaseService.purchaseProduct(any(PurchaseProductServiceRequest.class))).thenReturn(
            serviceDto);
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.PURCHASE_PRODUCT))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("productId??? null?????? ????????? ????????????.")
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
      @DisplayName("quantity??? 1?????? ????????? ????????? ????????????.")
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

  @Nested
  @DisplayName("?????? ?????? ?????? ??????")
  class PurchasePart {

    private String url(Long partId) {
      return String.format("http://localhost:8080/v1/purchase/parts/%s/waiting", partId);
    }

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("?????? ????????? ???????????? partI/O??? ?????? ?????? ????????? ????????????.")
      void purchasePart() throws Exception {
        // given
        PurchasePartRequest request = PurchasePartRequest.builder()
            .quantity(quantity)
            .build();
        PurchasePartServiceResponse serviceDto = PurchasePartServiceResponse.builder()
            .partId(partId)
            .quantity(quantity)
            .build();
        PurchasePartResponse response = PurchasePartResponse.builder()
            .partId(partId)
            .quantity(quantity)
            .build();

        // when
        when(purchaseService.purchasePart(any(PurchasePartServiceRequest.class))).thenReturn(
            serviceDto);
        ResultActions actions = getResultActions(url(partId), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.PURCHASE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("partId??? null?????? ????????? ????????????.")
      void partIdNullException() throws Exception {
        // given
        PurchasePartRequest request = PurchasePartRequest.builder()
            .quantity(quantity)
            .build();

        // when
        ResultActions actions = getResultActions(url(null), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("quantity??? 1?????? ????????? ????????? ????????????.")
      void quantityNullException() throws Exception {
        // given
        PurchasePartRequest request = PurchasePartRequest.builder()
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

  @Nested
  @DisplayName("?????? ?????? ?????? ??????")
  class ConfirmPurchasePart {

    private String url(Long partIoId) {
      return String.format("http://localhost:8080/v1/purchase/part-io/%s/confirm", partIoId);
    }

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("????????? ???????????? partIo??? ????????? ???????????? ???????????? ????????????.")
      void confirmPurchasePart() throws Exception {
        // given
        ConfirmPurchasePartServiceResponse serviceDto = ConfirmPurchasePartServiceResponse.builder()
            .partIoId((partIoId))
            .partId(partId)
            .quantity(quantity)
            .build();
        ConfirmPurchasePartResponse response = ConfirmPurchasePartResponse.builder()
            .partIoId(partIoId)
            .partId(partId)
            .quantity(quantity)
            .build();

        // when
        when(purchaseService.confirmPurchasePart(any())).thenReturn(
            serviceDto);
        ResultActions actions = getResultActions(url(partIoId), HttpMethod.POST);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.CONFIRM_PURCHASE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("partIoId??? null?????? ????????? ????????????.")
      void partIoIdNullException() throws Exception {
        // given

        // when
        ResultActions actions = getResultActions(url(null), HttpMethod.POST);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }
}
