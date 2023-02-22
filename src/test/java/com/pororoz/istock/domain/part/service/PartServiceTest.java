package com.pororoz.istock.domain.part.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.part.dto.request.FindPartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.UpdatePartServiceRequest;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartDuplicatedException;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PartServiceTest {

  @InjectMocks
  PartService partService;

  @Mock
  PartRepository partRepository;

  Long partId = 1L;
  String partName = "BEAD";
  String spec = "BID|E2";
  long price = 100000;
  long stock = 5;

  Part part = Part.builder()
      .id(partId)
      .partName(partName).spec(spec)
      .price(price).stock(stock)
      .build();

  @Nested
  @DisplayName("파트 추가")
  class SavePart {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("파트를 추가한다.")
      void savePart() {
        //given
        SavePartServiceRequest request = SavePartServiceRequest.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        PartServiceResponse response = PartServiceResponse.builder()
            .partId(1L)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partRepository.save(any())).thenReturn(part);
        PartServiceResponse result = partService.savePart(request);

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
        SavePartServiceRequest request = SavePartServiceRequest.builder()
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partRepository.findByPartNameAndSpec(request.getPartName(),
            request.getSpec())).thenReturn(
            Optional.of(mock(Part.class)));

        //then
        assertThrows(PartDuplicatedException.class,
            () -> partService.savePart(request));
      }
    }
  }

  @Nested
  @DisplayName("파트 삭제")
  class DeletePart {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("파트를 삭제한다.")
      void deletePart() {
        //given
        PartServiceResponse response = PartServiceResponse.builder()
            .partId(1L)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partRepository.findById(partId)).thenReturn(Optional.of(part));

        //then
        PartServiceResponse result = partService.deletePart(1L);
        assertThat(result).usingRecursiveComparison().isEqualTo(response);

      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 파트를 삭제하려고 하면 오류가 발생한다.")
      void partNotFound() {
        //given

        //when
        when(partRepository.findById(2L)).thenReturn(Optional.empty());

        //then
        assertThrows(PartNotFoundException.class,
            () -> partService.deletePart(2L));
      }
    }
  }

  @Nested
  @DisplayName("파트 수정")
  class UpdatePart {

    Long newPartId = 1L;
    String newPartName = "BEAD";
    String newSpec = "HBA3580PL";
    long newPrice = 50000;
    long newStock = 1;


    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("파트를 수정한다.")
      void updatePart() {
        //given
        UpdatePartServiceRequest request = UpdatePartServiceRequest.builder()
            .partId(newPartId)
            .partName(newPartName).spec(newSpec)
            .price(newPrice).stock(newStock)
            .build();
        PartServiceResponse response = PartServiceResponse.builder()
            .partId(newPartId)
            .partName(newPartName).spec(newSpec)
            .price(newPrice).stock(newStock)
            .build();

        //when
        when(partRepository.findById(newPartId)).thenReturn(Optional.of(part));

        //then
        PartServiceResponse result = partService.updatePart(request);
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 파트를 수정하려고 하면 오류가 발생한다.")
      void partNotFound() {
        //given
        UpdatePartServiceRequest request = UpdatePartServiceRequest.builder()
            .partId(newPartId)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partRepository.findById(newPartId)).thenReturn(Optional.empty());

        //then
        assertThrows(PartNotFoundException.class,
            () -> partService.updatePart(request));
      }


      @Test
      @DisplayName("존재하는 partName, spec의 파트를 수정하려고 하면 오류가 발생한다.")
      void partDuplicated() {
        //given
        UpdatePartServiceRequest request = UpdatePartServiceRequest.builder()
            .partId(newPartId)
            .partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();

        //when
        when(partRepository.findById(newPartId)).thenReturn(Optional.of(part));
        when(partRepository.findByPartNameAndSpec(request.getPartName(),
            request.getSpec())).thenReturn(
            Optional.of(mock(Part.class)));

        //then
        assertThrows(PartDuplicatedException.class,
            () -> partService.updatePart(request));

      }
    }
  }

  @Nested
  @DisplayName("파트 조회")
  class FindParts {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("파트를 조회한다.")
      void findParts() {
        //given
        int page = 0;
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);
        FindPartServiceRequest request = FindPartServiceRequest.builder()
            .partId(partId).partName(partName)
            .spec(spec)
            .build();
        Part part = Part.builder()
            .id(partId).partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        PageImpl<Part> parts = new PageImpl<>(List.of(part), pageable, 1);
        //when
        when(partRepository.findByIdAndPartNameAndSpecIgnoreNull(eq(partId), eq(partName), eq(spec),
            any(Pageable.class)))
            .thenReturn(parts);
        Page<PartServiceResponse> result = partService.findParts(request, pageable);

        //then
        PartServiceResponse partServiceResponse = PartServiceResponse.builder()
            .partId(partId).partName(partName).spec(spec)
            .price(price).stock(stock)
            .build();
        assertThat(result.getTotalElements()).isEqualTo(parts.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(parts.getTotalPages());
        assertThat(result.getContent()).usingRecursiveComparison()
            .isEqualTo(List.of(partServiceResponse));
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }
}