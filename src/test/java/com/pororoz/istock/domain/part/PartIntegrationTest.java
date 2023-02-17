package com.pororoz.istock.domain.part;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.request.SavePartRequest;
import com.pororoz.istock.domain.part.dto.request.UpdatePartRequest;
import com.pororoz.istock.domain.part.dto.response.PartResponse;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class PartIntegrationTest extends IntegrationTest {

  @Autowired
  PartRepository partRepository;

  private final String url = "http://localhost:8080/v1/parts";

  private Long partId;

  @Nested
  @DisplayName("POST /v1/parts - 파트 추가")
  class SavePart {

    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
      Part part = Part.builder()
          .partName("oldPartName").spec("oldSpec")
          .build();
      partRepository.save(part);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 파트를 넘겨주면 파트 추가에 성공한다.")
      void savePart() throws Exception {
        //given
        SavePartRequest request = SavePartRequest.builder()
            .partName("BEAD").spec("BRD|A2D")
            .price(100000).stock(5)
            .build();
        PartResponse response = PartResponse.builder()
            .partId(2L)
            .partName("BEAD").spec("BRD|A2D")
            .price(100000).stock(5)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class failCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하는 파트를 넘겨주면 파트 추가에 실패한다.")
      void duplicatedPart() throws Exception {
        //given
        SavePartRequest request = SavePartRequest.builder()
            .partName("oldPartName").spec("oldSpec")
            .price(10000).stock(5)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PART_DUPLICATED))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PART_DUPLICATED));
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given
        SavePartRequest request = SavePartRequest.builder()
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }

    }
  }

  @Nested
  @DisplayName("DELETE /v1/parts/{partId} - 파트 삭제 API")
  class DeletePart {

    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
      Part part = Part.builder()
          .partName("oldPartName").spec("oldSpec")
          .build();
      partRepository.save(part);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하는 파트를 넘겨주면 파트 삭제에 성공한다.")
      void deletePart() throws Exception {
        //given
        partId = 1L;
        PartResponse response = PartResponse.builder()
            .partId(1L)
            .partName("oldPartName").spec("oldSpec")
            .build();

        //when
        ResultActions actions = getResultActions(url + "/" + partId, HttpMethod.DELETE);

        //then
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
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 파트를 넘겨주면 PART_NOT_FOUND를 반환한다.")
      void partNotFound() throws Exception {
        //given
        partId = 2L;

        //when
        ResultActions actions = getResultActions(url + "/" + partId, HttpMethod.DELETE);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PART_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PART_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given
        partId = 1L;

        //when
        ResultActions actions = getResultActions(url + "/" + partId, HttpMethod.DELETE);

        //then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("PUT /v1/parts - 파트 수정 API")
  class UpdatePart {
    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
      Part part = Part.builder().id(1L)
          .partName("oldPartName").spec("oldSpec").build();
      Part part2 = Part.builder().id(2L)
          .partName("oldPartName2").spec("oldSpec2").build();
      partRepository.save(part);
      partRepository.save(part2);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하는 파트의 정보 수정에 성공한다.")
      void updatePart() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(1L)
            .partName("newPartName").spec("newSpec")
            .build();
        PartResponse response = PartResponse.builder()
            .partId(1L)
            .partName("newPartName").spec("newSpec")
            .build();

        //when
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
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 파트를 넘겨주면 PART_NOT_FOUND를 반환한다.")
      void partNotFound() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(3L)
            .partName("newPartName").spec("newSpec")
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PART_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PART_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("변경 후 파트 정보가 이미 존재한다면 PART_DUPLICATED를 반환한다.")
      void partDuplicated() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(2L)
            .partName("oldPartName").spec("oldSpec")
            .build();
        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PART_DUPLICATED))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PART_DUPLICATED))
            .andDo(print());

      }


      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given
        UpdatePartRequest request = UpdatePartRequest.builder()
            .partId(1L)
            .partName("newPart").spec("newSpec")
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }
}
