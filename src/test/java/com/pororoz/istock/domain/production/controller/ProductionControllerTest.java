package com.pororoz.istock.domain.production.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.production.dto.response.UpdateProductionResponse;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceRequest;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceResponse;
import com.pororoz.istock.domain.production.dto.service.UpdateProductionServiceResponse;
import com.pororoz.istock.domain.production.service.ProductionService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ProductionController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class ProductionControllerTest extends ControllerTest {

  @MockBean
  ProductionService productionService;

  @Nested
  @DisplayName("제품 생산 대기")
  class SaveWaitProduction {

    Long productId = 1L;
    long quantity = 10L;

    String getUri(Long productId) {
      return "/v1/production/products/" + productId + "/waiting";
    }

    @Test
    @DisplayName("제품 생산 대기를 저장한다.")
    void saveWaitProduction() throws Exception {
      //given
      Map<String, String> request = new HashMap<>();
      request.put("quantity", "10");
      SaveProductionServiceResponse responseDto = SaveProductionServiceResponse.builder()
          .productId(productId).quantity(quantity)
          .build();
      ArgumentCaptor<SaveProductionServiceRequest> argument =
          ArgumentCaptor.forClass(SaveProductionServiceRequest.class);

      //when
      when(productionService.saveWaitingProduction(any(SaveProductionServiceRequest.class)))
          .thenReturn(responseDto);
      ResultActions actions = getResultActions(getUri(productId), HttpMethod.POST, request);

      //then
      SaveProductionServiceRequest requestDto = SaveProductionServiceRequest.builder()
          .productId(productId).quantity(quantity)
          .build();
      verify(productionService).saveWaitingProduction(argument.capture());
      assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(requestDto);
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.WAIT_PRODUCTION))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(responseDto))))
          .andDo(print());
    }

    @Test
    @DisplayName("productId에 0 이하의 정수가 들어갈 수 없다.")
    void productIdCannotZeroOrNegative() throws Exception {
      //given
      Map<String, String> request = new HashMap<>();
      request.put("quantity", "10");

      //when
      ResultActions actions = getResultActions(getUri(0L), HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }

    @Test
    @DisplayName("quantity에 0 이하의 정수가 들어갈 수 없다.")
    void quantityCannotZeroOrNegative() throws Exception {
      //given
      Map<String, String> request = new HashMap<>();
      request.put("quantity", "0");

      //when
      ResultActions actions = getResultActions(getUri(productId), HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }

    @Test
    @DisplayName("quantity에 null이 들어갈 수 없다.")
    void quantityNotNull() throws Exception {
      //given
      Map<String, String> request = new HashMap<>();
      request.put("quantity", null);

      //when
      ResultActions actions = getResultActions(getUri(productId), HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("제품 생산 확정")
  class ConfirmProduction {

    Long productIoId = 1L;
    Long productId = 1L;
    long quantity = 10;

    String getUri(Long productIoId) {
      return "/v1/production/product-io/" + productIoId + "/confirm";
    }

    @Test
    @DisplayName("제품 생산 확정 요청을 받는다")
    void confirmProduction() throws Exception {
      //given
      String uri = getUri(productIoId);
      UpdateProductionServiceResponse serviceResponse = UpdateProductionServiceResponse.builder()
          .productIoId(productIoId)
          .productId(productId)
          .quantity(quantity).build();

      //when
      when(productionService.confirmProduction(productIoId)).thenReturn(serviceResponse);
      ResultActions actions = getResultActions(uri, HttpMethod.POST);

      //then
      UpdateProductionResponse response = UpdateProductionResponse.builder()
          .productIoId(productIoId)
          .productId(productId)
          .quantity(quantity).build();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.CONFIRM_PRODUCTION))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("productIoId는 null이면 안된다.")
    void productIoIdNotNull() throws Exception {
      //given
      String uri = getUri(null);

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST);

      //then
      actions.andExpect(status().isBadRequest()).andDo(print());
    }
  }

  @Nested
  @DisplayName("제품 생산 취소")
  class CancelProduction {

    Long productIoId = 1L;
    Long productId = 1L;
    long quantity = 10;

    String getUri(Long productIoId) {
      return "/v1/production/product-io/" + productIoId + "/cancel";
    }

    @Test
    @DisplayName("제품 생산 취소 요청을 보내고 응답을 받는다.")
    void cancelProduction() throws Exception {
      //given
      UpdateProductionServiceResponse serviceResponse = UpdateProductionServiceResponse.builder()
          .quantity(quantity)
          .productId(productId)
          .productIoId(productIoId).build();

      //when
      when(productionService.cancelProduction(productIoId)).thenReturn(serviceResponse);
      ResultActions actions = getResultActions(getUri(productIoId), HttpMethod.POST);

      //then
      UpdateProductionResponse response = UpdateProductionResponse.builder()
          .quantity(quantity)
          .productId(productId)
          .productIoId(productIoId).build();
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.CANCEL_PRODUCTION))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("ProductIoId로 0을 보내면 BadRequest가 발생한다.")
    void cancelProductionIoIdNotZero() throws Exception {
      //given
      //when
      ResultActions actions = getResultActions(getUri(0L), HttpMethod.POST);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }

    @Test
    @DisplayName("ProductIoId로 null을 보내면 BadRequest가 발생한다.")
    void cancelProductionIoIdNotNull() throws Exception {
      //given
      //when
      ResultActions actions = getResultActions(getUri(null), HttpMethod.POST);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }
  }
}