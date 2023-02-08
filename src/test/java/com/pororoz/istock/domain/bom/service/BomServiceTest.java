package com.pororoz.istock.domain.bom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceResponse;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.NotExistedPart;
import com.pororoz.istock.domain.bom.exception.NotExistedProduct;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BomServiceTest {

  @InjectMocks
  BomService bomService;

  @Mock
  BomRepository bomRepository;

  @Mock
  PartRepository partRepository;

  @Mock
  ProductRepository productRepository;

  @Nested
  @DisplayName("제품 BOM 행 추가 로직 테스트")
  class SaveBom {

    Long bomId;
    String locationNumber;
    String codeNumber;
    Long quantity;
    String memo;
    Long partId;
    Long productId;

    @BeforeEach
    void setup() {
      bomId = 1L;
      locationNumber = "L5.L4";
      codeNumber = "";
      quantity = 3L;
      memo = "";
      partId = 1L;
      productId = 2L;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
      @Test
      @DisplayName("입력값으로 적절한 값이 들어오면 BOM이 정상적으로 추가된다.")
      void saveBom() {
        // given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();
        SaveBomServiceResponse response = SaveBomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        Part part = Part.builder().id(partId).build();
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .part(part)
            .productId(productId)
            .product(product)
            .build();

        // when
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.save(any())).thenReturn(bom);
        SaveBomServiceResponse result = bomService.saveBom(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
      @Test
      @DisplayName("partId에 해당하는 part가 존재하지 않으면 예외가 발생한다.")
      void partNotExist() {
        //given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        //when
        when(partRepository.findById(anyLong())).thenThrow(
            NotExistedPart.class);
        //then
        assertThrows(NotExistedPart.class,
            () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("productId에 해당하는 product가 존재하지 않으면 예외가 발생한다.")
      void productNotExist() {
        //given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        //when
        Part part = Part.builder().id(partId).build();
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(anyLong())).thenThrow(
            NotExistedProduct.class);
        //then
        assertThrows(NotExistedProduct.class,
            () -> bomService.saveBom(request));
      }
    }
  }
}