package com.pororoz.istock.domain.bom.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.request.SaveBomRequest;
import com.pororoz.istock.domain.bom.dto.request.UpdateBomRequest;
import com.pororoz.istock.domain.bom.dto.response.BomResponse;
import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.DeleteBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.service.BomService;
import com.pororoz.istock.domain.part.dto.PartDto;
import com.pororoz.istock.domain.part.entity.Part;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(value = BomController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class BomControllerTest extends ControllerTest {

  @MockBean
  BomService bomService;

  @Nested
  @DisplayName("제품 Bom 행 조회")
  class FindBom {
    String locationNumber = "L5.L4";
    String codeNumber = "";
    long quantity = 3;
    String memo = "";
    Long productId = 2L;
    String uri = "http://localhost:8080/v1/bom";
    MultiValueMap<String, String> params;

    @BeforeEach
    void setup() {
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("Bom 정보를 넣으면 저장된다.")
      void saveBom() throws Exception {
        // given
        int page = 1;
        int size = 2;
        String now = TimeEntity.formatTime(LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(page, size);

        Part part1 = Part.builder()
            .id(1L)
            .price(580)
            .partName("part1")
            .stock(2L)
            .spec("spec1")
            .build();
        Part part2 = Part.builder()
            .id(2L)
            .price(580)
            .partName("part2")
            .stock(2L)
            .spec("spec2")
            .build();
        FindBomServiceResponse serviceResponse1 = FindBomServiceResponse.builder()
            .bomId(1L)
            .locationNumber(locationNumber + "0")
            .codeNumber(codeNumber + "0")
            .quantity(quantity)
            .memo(memo)
            .createdAt(now)
            .updatedAt(now)
            .part(PartDto.of(part1))
            .build();
        FindBomServiceResponse serviceResponse2 = FindBomServiceResponse.builder()
            .bomId(2L)
            .locationNumber(locationNumber + "1")
            .codeNumber(codeNumber + "1")
            .quantity(quantity)
            .memo(memo)
            .createdAt(now)
            .updatedAt(now)
            .part(PartDto.of(part2))
            .build();
        Page<FindBomServiceResponse> dtoPage =
            new PageImpl<>(List.of(serviceResponse1, serviceResponse2), pageRequest, 4);

        params.add("product-id", Long.toString(productId));

        // when
        when(bomService.findBomList(any(FindBomServiceRequest.class))).thenReturn(dtoPage);
        ResultActions actions = getResultActions(uri, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_BOM))
            .andExpect(jsonPath("$.data.contents[0].bomId").value(1))
            .andExpect(jsonPath("$.data.contents[0].locationNumber").value(locationNumber + "0"))
            .andExpect(jsonPath("$.data.contents[0].codeNumber").value(codeNumber + "0"))
            .andExpect(jsonPath("$.data.contents[0].quantity").value(quantity))
            .andExpect(jsonPath("$.data.contents[0].memo").value(memo))
            .andExpect(jsonPath("$.data.contents[0].part.id").value(1))
            .andExpect(jsonPath("$.data.contents[0].part.partName").value("part1"))
            .andExpect(jsonPath("$.data.contents[0].part.spec").value("spec1"))
            .andExpect(jsonPath("$.data.contents[0].part.price").value(580))
            .andExpect(jsonPath("$.data.contents[0].part.stock").value(2))
            .andExpect(jsonPath("$.data.contents[1].bomId").value(2))
            .andExpect(jsonPath("$.data.contents[1].locationNumber").value(locationNumber + "1"))
            .andExpect(jsonPath("$.data.contents[1].codeNumber").value(codeNumber + "1"))
            .andExpect(jsonPath("$.data.contents[1].quantity").value(quantity))
            .andExpect(jsonPath("$.data.contents[1].memo").value(memo))
            .andExpect(jsonPath("$.data.contents[1].part.id").value(2))
            .andExpect(jsonPath("$.data.contents[1].part.partName").value("part2"))
            .andExpect(jsonPath("$.data.contents[1].part.spec").value("spec2"))
            .andExpect(jsonPath("$.data.contents[1].part.price").value(580))
            .andExpect(jsonPath("$.data.contents[1].part.stock").value(2))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("productId가 비어있으면 Bad Request 오류를 반환한다.")
      void emptyProductId() throws Exception {
        // given
        params.add("page", "2");
        params.add("size", "2");

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("productId가 문자열로 넘어오면 Bad Request 오류를 반환한다.")
      void productIdString() throws Exception {
        // given
        params.add("page", "2");
        params.add("size", "2");
        params.add("product-id", "string");

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("제품 Bom 행 추가")
  class SaveBom {

    Long bomId = 1L;
    String locationNumber = "L5.L4";
    String codeNumber = "";
    long quantity = 3;
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
        BomServiceResponse serviceResponse = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();
        BomResponse response = BomResponse.builder()
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
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("productId가 비어있으면 Bad Request 오류를 반환한다.")
      void emptyProductId() throws Exception {
        // given
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(null)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("BOM Delete Controller Test")
  class DeleteBom {

    Long bomId = 1L;
    String locationNumber = "L5.L4";
    String codeNumber = "";
    Long quantity = 3L;
    String memo = "";
    Long partId = 1L;
    Long productId = 2L;

    String uri = "http://localhost:8080/v1/bom";
    MultiValueMap<String, String> params;

    @BeforeEach
    void setup() {
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 BOM을 삭제하면 Response가 돌아온다.")
      void deleteBom() throws Exception {
        // given
        BomServiceResponse serviceResponse = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();
        params.add("bomId", Long.toString(bomId));
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        when(bomService.deleteBom(any(DeleteBomServiceRequest.class))).thenReturn(serviceResponse);
        ResultActions actions = getResultActions(uri, HttpMethod.DELETE, params);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("bomId가 null 값이면 400 Bad Request를 반환한다.")
      void bomIdNull() throws Exception {
        // given
        // when
        ResultActions actions = getResultActions(uri, HttpMethod.DELETE, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("bomId가 숫자가 아닌 값이면 400 Bad Request를 반환한다.")
      void bomIdNotNumber() throws Exception {
        // given
        params.add("bomId", "not-number");

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.DELETE, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("bomId가 마이너스 값이면 400 Bad Request를 반환한다.")
      void bomIdMinusNumber() throws Exception {
        // given
        params.add("bomId", "-1");

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.DELETE, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("BOM Update Controller Test")
  class UpdateBom {

    Long bomId = 1L;
    String newLocationNumber = "new location";
    String newCodeNumber = "new code";
    Long newQuantity = 5L;
    String newMemo = "new";
    Long newPartId = 3L;
    Long newProductId = 4L;

    String uri = "http://localhost:8080/v1/bom";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 BOM 값을 수정하면 200 OK를 반환한다.")
      void saveBom() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();
        BomServiceResponse serviceResponse = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        when(bomService.updateBom(any(UpdateBomServiceRequest.class))).thenReturn(serviceResponse);
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("bomId가 null 값이면 400 Bad Request를 반환한다.")
      void bomIdNull() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(null)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("bomId가 마이너스 값이면 400 Bad Request를 반환한다.")
      void bomIdMinusNumber() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(-1L)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("productId가 비어있으면 Bad Request 오류를 반환한다.")
      void emptyProductId() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(-1L)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(null)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }
}