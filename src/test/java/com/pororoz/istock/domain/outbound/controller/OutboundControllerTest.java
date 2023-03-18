package com.pororoz.istock.domain.outbound.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.outbound.dto.request.OutboundRequest;
import com.pororoz.istock.domain.outbound.dto.response.OutboundConfirmResponse;
import com.pororoz.istock.domain.outbound.dto.response.OutboundResponse;
import com.pororoz.istock.domain.outbound.dto.service.OutboundConfirmServiceRequest;
import com.pororoz.istock.domain.outbound.dto.service.OutboundConfirmServiceResponse;
import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceRequest;
import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceResponse;
import com.pororoz.istock.domain.outbound.service.OutboundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = OutboundController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class OutboundControllerTest extends ControllerTest {

  @MockBean
  OutboundService outboundService;

  final Long productId = 1L;
  final Long productIoId = 10L;
  final Long quantity = 100L;

  @Nested
  @DisplayName("제품 출고 대기 API")
  class Outbound {

    String url(Long productId) {
      return "http://localhost:8080/v1/outbounds/products/" + productId + "/waiting";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("제품 출고를 요청하면 출고된 productId와 quantity를 반환한다.")
      void outbound() throws Exception {
        // given
        OutboundRequest request = OutboundRequest.builder()
            .quantity(quantity)
            .build();
        OutboundServiceResponse serviceResponse = OutboundServiceResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(outboundService.outbound(any(OutboundServiceRequest.class)))
            .thenReturn(serviceResponse);
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        // then
        OutboundResponse response = OutboundResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.OUTBOUND_WAIT))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("productId값이 0이하면 예외처리를 한다.")
      void productIdNotPositive() throws Exception {
        // given
        OutboundRequest request = OutboundRequest.builder()
            .quantity(quantity)
            .build();

        // when
        ResultActions actions = getResultActions(url(-1L), HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("quantity값을 0이하로 보내면 예외처리를 한다.")
      void quantityNull() throws Exception {
        // given
        OutboundRequest request = OutboundRequest.builder()
            .quantity(-1)
            .build();

        // when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("제품 출고 확정 API")
  class OutboundConfirm {

    String url(Long productIoId) {
      return "http://localhost:8080/v1/outbounds/product-io/" + productIoId + "/confirm";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("제품 출고 확정을 요청하면 productIO와 출고된 productId, quantity를 반환한다.")
      void outboundConfirm() throws Exception {
        // given
        OutboundConfirmServiceResponse serviceResponse = OutboundConfirmServiceResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(outboundService.outboundConfirm(any(OutboundConfirmServiceRequest.class)))
            .thenReturn(serviceResponse);
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        OutboundConfirmResponse response = OutboundConfirmResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.OUTBOUND_CONFIRM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
      @Test
      @DisplayName("productIoId값이 0이하면 예외처리를 한다.")
      void productIoIdNotPositive() throws Exception {
        // given

        // when
        ResultActions actions = getResultActions(url(-1L), HttpMethod.POST);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("제품 출고 취소 API")
  class OutboundCancel {

    String url(long productIoId) {
      return "http://localhost:8080/v1/outbounds/product-io/" + productIoId + "/cancel";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
      @Test
      @DisplayName("제품 출고 취소를 하면 200 OK값과 해당 productIo와 product에 대한 정보를 제공한다.")
      void cancelOutbound() throws Exception {
        // given
        OutboundConfirmServiceResponse serviceResponse = OutboundConfirmServiceResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(outboundService.outboundCancel(any(OutboundConfirmServiceRequest.class)))
            .thenReturn(serviceResponse);
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        OutboundConfirmResponse response = OutboundConfirmResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.OUTBOUND_CANCEL))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
      @Test
      @DisplayName("productIoId값이 0이하면 예외처리를 한다.")
      void productIoIdNotPositive() throws Exception {
        // given

        // when
        ResultActions actions = getResultActions(url(-1L), HttpMethod.POST);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }
}