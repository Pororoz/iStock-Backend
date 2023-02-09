package com.pororoz.istock.domain.part.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.request.SavePartRequest;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceResponse;
import com.pororoz.istock.domain.part.service.PartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = PartController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
public class PartControllerTest extends ControllerTest {

  @MockBean
  PartService partService;

  @Nested
  @DisplayName("파트 추가")
  class SavePart {

    String url = "http://localhost:8080/v1/parts";
    private String partName;
    private String spec;
    private long price;
    private long stock;

    @Nested
    @DisplayName("성공 케이스")
    class successCase {

      @Test
      @DisplayName("파트를 추가한다.")
      void savePart() throws Exception {
        //given
        partName = "BEAD";
        spec = "BRD|A2D";
        price = 100000;
        stock = 5;

        SavePartRequest request = SavePartRequest.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        SavePartServiceResponse serviceResponse = SavePartServiceResponse.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partService.savePart(any(SavePartServiceRequest.class))).thenReturn(
            serviceResponse);
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PART))
            .andExpect(jsonPath("$.data.partName").value(partName))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class failCase {

      @Test
      @DisplayName("partName을 적지 않으면 예외가 발생한다.")
      void partNameNullException() throws Exception {
        //given
        partName = null;
        spec = "BRD|A2D";
        price = 100000;
        stock = 5;

        SavePartRequest request = SavePartRequest.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        SavePartServiceResponse serviceResponse = SavePartServiceResponse.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("spec 적지 않으면 예외가 발생한다.")
      void specNullException() throws Exception {
        //given
        partName = "BEAD";
        spec = null;
        price = 100000;
        stock = 5;

        SavePartRequest request = SavePartRequest.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        SavePartServiceResponse serviceResponse = SavePartServiceResponse.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }
}
