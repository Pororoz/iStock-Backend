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
import com.pororoz.istock.domain.purchase.dto.response.PurchasePartResponse;
import com.pororoz.istock.domain.purchase.dto.response.PurchaseProductResponse;
import com.pororoz.istock.domain.purchase.dto.response.UpdateSubAssyPurchaseResponse;
import com.pororoz.istock.domain.purchase.dto.response.UpdatePurchaseResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.UpdateSubAssyPurchaseServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.UpdatePurchaseServiceResponse;
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
  final Long productIoId = 1L;
  final long quantity = 300L;
  final Long partId = 1L;
  final Long partIoId = 1L;

  @Nested
  @DisplayName("제품 자재 일괄 구매")
  class PurchaseProduct {

    String url(Long productId) {
      return String.format("http://localhost:8080/v1/purchase/products/%s/waiting", productId);
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

  @Nested
  @DisplayName("제품 자재 개별 구매")
  class PurchasePart {

    private String url(Long partId) {
      return String.format("http://localhost:8080/v1/purchase/parts/%s/waiting", partId);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("개별 구매를 요청하면 partI/O에 구매 대기 내역을 생성한다.")
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
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("partId가 null이면 오류가 발생한다.")
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
      @DisplayName("quantity가 1보다 작은면 오류가 발생한다.")
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
  @DisplayName("제품 자재 구매 확정")
  class ConfirmPurchasePart {

    private String url(Long partIoId) {
      return String.format("http://localhost:8080/v1/purchase/part-io/%s/confirm", partIoId);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("구매를 확정하면 partIo의 상태가 대기에서 확정으로 변경된다.")
      void confirmPurchasePart() throws Exception {
        // given
        UpdatePurchaseServiceResponse serviceDto = UpdatePurchaseServiceResponse.builder()
            .partIoId((partIoId))
            .partId(partId)
            .quantity(quantity)
            .build();
        UpdatePurchaseResponse response = UpdatePurchaseResponse.builder()
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
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("partIoId가 null이면 오류가 발생한다.")
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

  @Nested
  @DisplayName("제품 자재 구매 취소")
  class CancelPurchasePart {

    private String url(Long partIoId) {
      return String.format("http://localhost:8080/v1/purchase/part-io/%s/cancel", partIoId);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("구매를 취소하면 partIo의 상태가 대기에서 취소로 변경된다.")
      void cancelPurchasePart() throws Exception {
        // given
        UpdatePurchaseServiceResponse serviceDto = UpdatePurchaseServiceResponse.builder()
            .partIoId((partIoId))
            .partId(partId)
            .quantity(quantity)
            .build();
        UpdatePurchaseResponse response = UpdatePurchaseResponse.builder()
            .partIoId(partIoId)
            .partId(partId)
            .quantity(quantity)
            .build();

        // when
        when(purchaseService.cancelPurchasePart(any())).thenReturn(
            serviceDto);
        ResultActions actions = getResultActions(url(partIoId), HttpMethod.POST);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.CANCEL_PURCHASE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("partIoId가 null이면 오류가 발생한다.")
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

  @Nested
  @DisplayName("Sub Assy 구매 확정")
  class ConfirmSubAssyPurchase {

    private String url(Long productIoId) {
      return String.format("http://localhost:8080/v1/purchase/subassy-io/%s/confirm", partIoId);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("subAssy인 productIo의 상태가 구매대기에서 구매확정으로 변경된다.")
      void confirmSubAssyPurchase() throws Exception {
        // given
        UpdateSubAssyPurchaseServiceResponse serviceDto = UpdateSubAssyPurchaseServiceResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();
        UpdateSubAssyPurchaseResponse response = UpdateSubAssyPurchaseResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(purchaseService.confirmSubAssyPurchase(any())).thenReturn(
            serviceDto);
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.CONFIRM_SUB_ASSY_PURCHASE))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("productIoId가 null이면 오류가 발생한다.")
      void productIoIdNullException() throws Exception {
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
