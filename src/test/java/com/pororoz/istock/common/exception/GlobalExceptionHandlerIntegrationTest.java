package com.pororoz.istock.common.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  @DisplayName("Exception Handling")
  class ErrorHandling {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("404 페이지")
    void updateUser() throws Exception {
      // given

      // when
      ResultActions actions = mockMvc.perform(get("/sjkfjeiowniognw")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.status").value(ExceptionStatus.PAGE_NOT_FOUND))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.PAGE_NOT_FOUND))
          .andDo(print());
    }

    @Test
    @DisplayName("test-runtime-error")
    void testRuntimeError() throws Exception {
      // given
      String url = "/v1/test/test-runtime-error";

      // when
      ResultActions actions = mockMvc.perform(get(url)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.status").value(ExceptionStatus.RUNTIME_ERROR))
          .andDo(print());
    }

    @Test
    @DisplayName("test-internal-server-error")
    void testInternalServerError() throws Exception {
      // given
      String url = "/v1/test/test-internal-server-error";

      // when
      ResultActions actions = mockMvc.perform(get(url)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isInternalServerError())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.status").value(ExceptionStatus.INTERNAL_SERVER_ERROR))
          .andDo(print());
    }
  }
}