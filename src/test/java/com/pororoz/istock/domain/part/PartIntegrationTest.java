package com.pororoz.istock.domain.part;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.request.SavePartRequest;
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
  @Nested
  @DisplayName("POST /v1/parts - 파트 추가")
  class SavePart {

    private final String url = "http://localhost:8080/v1/parts";
    private String partName;
    private String spec;
    private long price;
    private long stock;

    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
      Part part = Part.builder().partName("sth").spec("sth").build();
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
        partName = "BEAD";
        spec = "BRD|A2D";
        price = 100000;
        stock = 5;
        SavePartRequest request = SavePartRequest.builder()
            .partName(partName)
            .spec(spec)
            .price(price)
            .stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PART))
            .andExpect(jsonPath("$.data.partName").value(partName));
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
        partName = "sth";
        spec = "sth";
        price = 100000;
        stock = 5;
        SavePartRequest request = SavePartRequest.builder()
            .partName(partName)
            .spec(spec)
            .price(price)
            .stock(stock)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PART_NAME_DUPLICATED))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PART_NAME_DUPLICATED));
      }
    }
  }
}
