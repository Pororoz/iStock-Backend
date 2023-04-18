package com.pororoz.istock.domain.file.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.domain.file.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = FileController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class FileControllerTest extends ControllerTest {

  @MockBean
  FileService fileService;

  final String baseUri = "/v1/files";

  String getUri(String param) {
    return baseUri + "?product-id-list=" + param;
  }

  @Nested
  class ExportFile {

    @Nested
    class SuccessCase {

      @Test
      @DisplayName("product id list를 string으로 받아 list로 변환하여 csv를 만든다.")
      void exportFile() throws Exception {
        //given
        String uri = getUri("1, 2,3");
        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);

        //when
        getResultActions(uri, HttpMethod.GET);

        //then
        verify(fileService, times(1)).exportFile(any(HttpServletResponse.class),
            argument.capture());
        assertThat(argument.getValue()).usingRecursiveComparison()
            .isEqualTo(List.of(1L, 2L, 3L));
      }
    }

    @Nested
    class FailCase {

      @Test
      @DisplayName("product-id-list가 없으면 예외가 발생한다.")
      void productIdListParamNotExit() throws Exception {
        //given
        //when
        ResultActions actions = getResultActions(baseUri, HttpMethod.GET);

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andDo(print());
      }

    }

  }

}