package com.pororoz.istock.domain.part.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceResponse;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNameDuplicatedException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PartServiceTest {

  @InjectMocks
  PartService partService;

  @Mock
  PartRepository partRepository;

  @Nested
  @DisplayName("파트 추가 API")
  class SavePart {

    String partName = "BEAD";
    String spec = "BID|E2";
    long price = 100000;
    long stock = 5;
    SavePartServiceRequest request = SavePartServiceRequest.builder()
        .partName(partName).spec(spec)
        .price(price).stock(stock)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class successCase {

      @Test
      @DisplayName("파트를 추가한다.")
      void savePart() {
        //given
        Part part = Part.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        SavePartServiceResponse response = SavePartServiceResponse.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partRepository.save(any())).thenReturn(part);
        SavePartServiceResponse result = partService.savePart(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class failCase {

      @Test
      @DisplayName("존재하는 파트를 추가하려고 하면 오류가 발생한다.")
      void partNameDuplicated() {
        //given
        Part part = Part.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partRepository.findByPartNameAndSpec(request.getPartName(),
            request.getSpec())).thenReturn(
            Optional.of(mock(Part.class)));

        //then
        assertThrows(PartNameDuplicatedException.class,
            () -> partService.savePart(request));
      }
    }
  }
}