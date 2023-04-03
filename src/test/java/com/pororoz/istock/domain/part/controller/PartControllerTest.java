package com.pororoz.istock.domain.part.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.request.SavePartRequest;
import com.pororoz.istock.domain.part.dto.request.UpdatePartRequest;
import com.pororoz.istock.domain.part.dto.response.PartResponse;
import com.pororoz.istock.domain.part.dto.service.FindPartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.UpdatePartServiceRequest;
import com.pororoz.istock.domain.part.service.PartService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = PartController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
public class PartControllerTest extends ControllerTest {

  @MockBean
  PartService partService;

  private long partId = 1L;
  private String partName = "BEAD";
  private String spec = "BRD|A2D";
  private long price = 100000;
  private long stock = 5;

  @Nested
  @DisplayName("파트 추가")
  class SavePart {

    String url = "http://localhost:8080/v1/parts";

    @Nested
    @DisplayName("성공 케이스")
    class successCase {

      @Test
      @DisplayName("파트를 추가한다.")
      void savePart() throws Exception {
        //given
        SavePartRequest request = SavePartRequest.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        PartServiceResponse serviceResponse = PartServiceResponse.builder()
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
    class FailCase {

      @Test
      @DisplayName("partName을 적지 않으면 예외가 발생한다.")
      void partNameNullException() throws Exception {
        //given
        SavePartRequest request = SavePartRequest.builder()
            .partName(null).spec(spec)
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
        SavePartRequest request = SavePartRequest.builder()
            .partName(partName).spec(null)
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

  @Nested
  @DisplayName("파트 삭제")
  class deletePart {

    String url = "http://localhost:8080/v1/parts";

    @Nested
    @DisplayName("성공 케이스")
    class successCase {


      @Test
      @DisplayName("존재하는 파트를 삭제하면 파트 값을 반환한다.")
      void deletePart() throws Exception {
        //given
        PartServiceResponse serviceDto = PartServiceResponse.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partService.deletePart(partId)).thenReturn(serviceDto);
        ResultActions actions = getResultActions(url + "/" + partId, HttpMethod.DELETE);

        //then
        PartResponse response = PartResponse.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("partId가 1이상의 정수가 아니면 bad request 오류가 발생한다.")
      void partIdInvalid() throws Exception {
        //given

        //when
        ResultActions actions = getResultActions(url + "/" + 0, HttpMethod.DELETE);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("partId를 지정하지 않으면 not found 오류가 발생한다.")
      void partIdNotFound() throws Exception {
        //given

        //when
        ResultActions actions = getResultActions(url + "/", HttpMethod.DELETE);

        //then
        actions.andExpect(status().isNotFound())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("파트 수정 API")
  class UpdatePart {

    String url = "http://localhost:8080/v1/parts";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 파트를 수정하면 수정된 파트 값을 반환한다.")
      void updatePart() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(partId)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        PartServiceResponse serviceDto = PartServiceResponse.builder()
            .partId(partId)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        PartResponse response = PartResponse.builder()
            .partId(partId)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partService.updatePart(any(UpdatePartServiceRequest.class))).thenReturn(serviceDto);
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("partId가 1이상의 정수가 아니면 bad request 오류가 발생한다.")
      void partIdInvalid() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(0L)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("partId를 지정하지 않으면 bad request 오류가 발생한다.")
      void partIdNotFound() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("partName을 적지 않으면 bad request 오류가 발생한다.")
      void partNameNullException() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(partId)
            .partName(null).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("spec 적지 않으면 bad request 오류가 발생한다.")
      void specNullException() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(partId)
            .partName(partName).spec(null)
            .price(price).stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("파트 조회")
  class findParts {

    String uri = "http://localhost:8080/v1/parts";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      LocalDateTime now = LocalDateTime.now();
      PartServiceResponse serviceResponse = PartServiceResponse.builder()
          .partId(partId).partName(partName)
          .spec(spec).stock(stock).price(price)
          .createdAt(now).updatedAt(now)
          .build();

      @Test
      @DisplayName("id, name, spec, page, size 정보가 인자로 전달된다.")
      void allParamDelivered() throws Exception {
        //given
        int page = 0;
        int size = 3;
        long total = 1;
        String fullUri = uri + "?part-id=" + partId + "&part-name=" + partName + "&spec=" + spec
            + "&page=" + page + "&size=" + size;
        Page<PartServiceResponse> partServiceResponses = new PageImpl<>(
            List.of(serviceResponse), PageRequest.of(page, size), total);
        ArgumentCaptor<FindPartServiceRequest> partArgument =
            ArgumentCaptor.forClass(FindPartServiceRequest.class);
        ArgumentCaptor<Pageable> pageArgument =
            ArgumentCaptor.forClass(Pageable.class);

        //when
        when(partService.findParts(any(FindPartServiceRequest.class), any(Pageable.class)))
            .thenReturn(partServiceResponses);
        ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

        //then
        verify(partService).findParts(partArgument.capture(), pageArgument.capture());
        assertThat(partArgument.getValue().getPartId()).isEqualTo(partId);
        assertThat(partArgument.getValue().getPartName()).isEqualTo(partName);
        assertThat(partArgument.getValue().getSpec()).isEqualTo(spec);
        assertThat(pageArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(PageRequest.of(page, size));

        PageResponse<PartResponse> partResponses = new PageResponse<>(new PageImpl<>(
            List.of(serviceResponse.toResponse()), PageRequest.of(page, size), total));
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(partResponses))))
            .andDo(print());
      }

      @Test
      @DisplayName("id, name, spec에 null이 들어갈 수 있다.")
      void partInfoNullable() throws Exception {
        //given
        int page = 0;
        int size = 3;
        long total = 1;
        String fullUri = uri + "?page=" + page + "&size=" + size;

        Page<PartServiceResponse> partServiceResponses = new PageImpl<>(
            List.of(serviceResponse), PageRequest.of(page, size), total);
        ArgumentCaptor<FindPartServiceRequest> argument =
            ArgumentCaptor.forClass(FindPartServiceRequest.class);

        //when
        when(partService.findParts(any(FindPartServiceRequest.class), any(Pageable.class)))
            .thenReturn(partServiceResponses);
        ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

        //then
        verify(partService).findParts(argument.capture(), any(Pageable.class));
        assertThat(argument.getValue().getPartId()).isNull();
        assertThat(argument.getValue().getPartName()).isNull();
        assertThat(argument.getValue().getSpec()).isNull();

        PageResponse<PartResponse> partResponses = new PageResponse<>(new PageImpl<>(
            List.of(serviceResponse.toResponse()), PageRequest.of(page, size), total));
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(partResponses))))
            .andDo(print());
      }

      @Test
      @DisplayName("page, size에 null이 들어갈 수 있다.")
      void pageNullable() throws Exception {
        //given
        String fullUri = uri + "?part-id=" + partId + "&part-name=" + partName + "&spec=" + spec;

        Page<PartServiceResponse> partServiceResponses = new PageImpl<>(List.of(serviceResponse));
        ArgumentCaptor<Pageable> argument =
            ArgumentCaptor.forClass(Pageable.class);

        //when
        when(partService.findParts(any(FindPartServiceRequest.class), any(Pageable.class)))
            .thenReturn(partServiceResponses);
        ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

        //then
        verify(partService).findParts(any(FindPartServiceRequest.class), argument.capture());
        assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(Pageable.unpaged());

        PageResponse<PartResponse> partResponses = new PageResponse<>(
            new PageImpl<>(List.of(serviceResponse.toResponse())));
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(partResponses))))
            .andDo(print());
      }
    }
  }
}
