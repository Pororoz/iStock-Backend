package com.pororoz.istock.domain.part.controller;

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
import com.pororoz.istock.domain.part.dto.response.FindPartIoResponse;
import com.pororoz.istock.domain.part.dto.service.FindPartIoServiceResponse;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.service.PartIoService;
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


@WebMvcTest(value = PartIoController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class PartIoControllerTest extends ControllerTest {

  @MockBean
  PartIoService partIoService;

  String getUri(int page, int size, String status) {
    return "/v1/part-io?page=" + page + "&size=" + size + "&status=" + status;
  }

  LocalDateTime now = LocalDateTime.now();

  @Nested
  @DisplayName("partIo 조회")
  class FindPartIo {

    @Test
    @DisplayName("'대기'를 입력받아 partIo를 페이지네이션하여 조회한다.")
    void findPartIo() throws Exception {
      // given
      int page = 1;
      int size = 3;
      String status = "대기";
      String uri = getUri(page, size, status);
      FindPartIoServiceResponse serviceResponse = FindPartIoServiceResponse.builder()
          .partIoId(1L).quantity(10)
          .status(PartStatus.구매대기)
          .createdAt(now).updatedAt(now)
          .partServiceResponse(PartServiceResponse.builder()
              .partName("name").spec("spec")
              .build())
          .build();
      PageImpl<FindPartIoServiceResponse> partIoPage = new PageImpl<>(
          List.of(serviceResponse), PageRequest.of(page, size), 4);

      // when
      when(partIoService.findPartIo(eq(status), any(Pageable.class)))
          .thenReturn(partIoPage);
      ResultActions actions = getResultActions(uri, HttpMethod.GET);

      // then
      FindPartIoResponse findPartIoResponse = FindPartIoResponse.builder()
          .partIoId(1L).quantity(10)
          .status(PartStatus.구매대기)
          .createdAt(TimeEntity.formatTime(now))
          .updatedAt(TimeEntity.formatTime(now))
          .partName("name")
          .spec("spec").build();
      PageResponse<FindPartIoResponse> response = new PageResponse<>(
          new PageImpl<>(List.of(findPartIoResponse), PageRequest.of(page, size), 4));
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PART_IO))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }
  }
}
