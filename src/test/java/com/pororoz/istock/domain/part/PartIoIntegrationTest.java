package com.pororoz.istock.domain.part;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.response.FindPartIoResponse;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class PartIoIntegrationTest extends IntegrationTest {

  @Autowired
  PartRepository partRepository;

  @Autowired
  PartIoRepository partIoRepository;

  Part part1, part2;

  @BeforeEach
  void setUp() {
    part1 = partRepository.save(Part.builder()
        .partName("name1").spec("spec1")
        .build());
    part2 = partRepository.save(Part.builder()
        .partName("name2").spec("spec2")
        .build());
  }

  @Nested
  @DisplayName("GET /api/v1/part-io - 부품 IO 조회")
  class FindPartIo {

    String getUri(int page, int size, String status, Long partId) {
      return "/v1/part-io?page=" + page + "&size=" + size + "&status=" + status + "&part-id="
          + partId;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @WithMockUser
      @DisplayName("상태에 '완료'가 포함된 부품의 2페이지를 조회한다.")
      void findPartIoConfirm() throws Exception {
        // given
        int page = 1, size = 1;
        String uri = getUri(page, size, "완료", part1.getId());

        PartIo partIo1 = partIoRepository.save(PartIo.builder()
            .status(PartStatus.생산완료)
            .quantity(10)
            .part(part1)
            .build());
        PartIo partIo2 = partIoRepository.save(PartIo.builder()
            .status(PartStatus.입고완료)
            .quantity(10)
            .part(part1)
            .build());
        PartIo partIo3 = partIoRepository.save(PartIo.builder()
            .status(PartStatus.입고완료)
            .quantity(10)
            .part(part2)
            .build());

        FindPartIoResponse findPartIoResponse = FindPartIoResponse.builder()
            .partIoId(partIo2.getId())
            .quantity(partIo2.getQuantity())
            .status(partIo2.getStatus())
            .createdAt(TimeEntity.formatTime(partIo2.getCreatedAt()))
            .updatedAt(TimeEntity.formatTime(partIo2.getUpdatedAt()))
            .partId(partIo2.getPart().getId())
            .partName(partIo2.getPart().getPartName())
            .spec(partIo2.getPart().getSpec())
            .build();

        PageResponse<FindPartIoResponse> response = new PageResponse<>(
            new PageImpl<>(List.of(findPartIoResponse), PageRequest.of(page, size), 2)
        );

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.GET);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PART_IO))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("로그인하지 않은 유저는 접근할 수 없다.")
      void cannotAccessAnonymous() throws Exception {
        //given
        int page = 1, size = 1;
        String uri = getUri(page, size, "완료", part1.getId());

        //when
        ResultActions actions = getResultActions(uri, HttpMethod.GET);

        //then
        actions.andExpect(status().isForbidden());
      }
    }
  }
}