package com.pororoz.istock.domain.product.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.product.dto.response.FindProductIoResponse;
import com.pororoz.istock.domain.product.dto.service.FindProductIoServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.service.ProductIoService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ProductIoController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class ProductIoControllerTest extends ControllerTest {

  @MockBean
  ProductIoService productIoService;

  String getUri(int page, int size, String status) {
    return "/v1/product-io?page=" + page + "&size=" + size + "&status=" + status;
  }

  LocalDateTime now = LocalDateTime.now();

  @Nested
  @DisplayName("productIo 조회")
  class FindProductIo {

    @Test
    @DisplayName("'대기'를 입력받아 productIo를 페이지네이션하여 조회한다.")
    void findProductIo() throws Exception {
      //given
      int page = 1;
      int size = 3;
      String status = "대기";
      String uri = getUri(page, size, status);
      FindProductIoServiceResponse serviceResponse = FindProductIoServiceResponse.builder()
          .productIoId(1L).quantity(10)
          .status(ProductStatus.생산대기)
          .createdAt(now).updatedAt(now)
          .productServiceResponse(ProductServiceResponse.builder()
              .productName("name").productNumber("number")
              .build())
          .build();
      PageImpl<FindProductIoServiceResponse> productIoPage = new PageImpl<>(
          List.of(serviceResponse), PageRequest.of(page, size), 4);

      //when
      when(productIoService.findProductIo(eq(status), any(Pageable.class))).thenReturn(
          productIoPage);
      ResultActions actions = getResultActions(uri, HttpMethod.GET);

      //then
      FindProductIoResponse findProductIoResponse = FindProductIoResponse.builder()
          .productIoId(1L).quantity(10)
          .status(ProductStatus.생산대기)
          .createdAt(TimeEntity.formatTime(now))
          .updatedAt(TimeEntity.formatTime(now))
          .productName("name")
          .productNumber("number").build();
      PageResponse<FindProductIoResponse> response = new PageResponse<>(
          new PageImpl<>(List.of(findProductIoResponse), PageRequest.of(page, size), 4));
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT_IO))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }
  }

}