package com.pororoz.istock.domain.bom.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceResponse;
import com.pororoz.istock.domain.bom.service.BomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = BomController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class BomControllerTest extends ControllerTest {

  @MockBean
  BomService bomService;

  @Nested
  @DisplayName("제품 Bom 행 추가")
  class SaveBom {
    Long bomId = 1L;
    String locationNumber = "L5.L4";
    String codeNumber = "";
    Long quantity = 3L;
    String memo = "";
    Long partId = 1L;
    Long productId = 2L;
    String uri = "http://localhost:8080/v1/bom";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
      @Test
      @DisplayName("Bom 정보를 넣으면 저장된다.")
      void saveBom() throws Exception {
        // given
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();
        SaveBomServiceResponse serviceResponse = SaveBomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        when(bomService.saveBom(any(SaveBomServiceRequest.class))).thenReturn(serviceResponse);
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
            .andExpect(jsonPath("$.data.bomId").value(bomId))
            .andExpect(jsonPath("$.data.locationNumber").value(locationNumber))
            .andExpect(jsonPath("$.data.codeNumber").value(codeNumber))
            .andExpect(jsonPath("$.data.quantity").value(quantity))
            .andExpect(jsonPath("$.data.memo").value(memo))
            .andExpect(jsonPath("$.data.partId").value(partId))
            .andExpect(jsonPath("$.data.productId").value(productId))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }
}