package com.pororoz.istock.domain.part.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.part.dto.service.FindPartIoServiceResponse;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.product.entity.ProductIo;
import java.util.List;
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
public class PartIoServiceTest {

  @InjectMocks
  PartIoService partIoService;

  @Mock
  PartIoRepository partIoRepository;

  @Nested
  @DisplayName("partIo 조회")
  class FindPartIo {

    Part part = Part.builder().id(1L).build();

    ProductIo productIo = ProductIo.builder().id(1L).build();

    @Test
    @DisplayName("'구매대기'의 partIo를 페이지네이션하여 조회한다.")
    void findPartIo() {
      // given
      PageRequest pageRequest = PageRequest.of(0, 5);
      PartIo partIo = PartIo.builder()
          .id(1L).quantity(1)
          .status(PartStatus.구매대기).part(part).productIo(productIo)
          .build();

      // when
      when(partIoRepository.findByStatusContainingWithPart(eq("구매대기"), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(partIo), pageRequest, 1L));
      Page<FindPartIoServiceResponse> partIoPage = partIoService.findPartIo("구매대기",
          pageRequest);

      //then
      FindPartIoServiceResponse serviceResponse = FindPartIoServiceResponse.builder()
          .partIoId(partIo.getId())
          .quantity(partIo.getQuantity())
          .status(partIo.getStatus())
          .productIoId(partIo.getProductIo().getId())
          .partServiceResponse(PartServiceResponse.of(part))
          .build();
      assertThat(partIoPage.getTotalPages()).isEqualTo(1);
      assertThat(partIoPage.getTotalElements()).isEqualTo(1);
      assertThat(partIoPage.getContent()).usingRecursiveComparison()
          .isEqualTo(List.of(serviceResponse));
    }
  }
}
