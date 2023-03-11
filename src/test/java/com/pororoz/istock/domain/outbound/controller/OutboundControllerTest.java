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

  private Long productId = 1L;
  private Long productIoId = 10L;
  private Long quantity = 100L;

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
        OutboundServiceResponse serviceResponse = OutboundResponse.builder()
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
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }
}