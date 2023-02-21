package com.pororoz.istock.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseBulkServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseBulkServiceResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

  @InjectMocks
  PurchaseService purchaseService;

  @Mock
  ProductRepository productRepository;
  @Mock
  BomRepository bomRepository;
  @Mock
  PartRepository partRepository;
  @Mock
  PartIoRepository partIoRepository;

  @Nested
  @DisplayName("제품 자재 일괄 구매 테스트")
  class purchaseBulk {

    Long productId = 1L;
    Long partId = 1L;
    Long bomId = 1L;
    Long partIoId = 1L;
    String locationNumber = "L5.L4";
    String codeNumber = "";
    long quantity = 3L;
    String memo = "";
    long amount = 100L;

    PartStatus status = PartStatus.구매대기;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("입력받은 Product에 포함된 Part에 대한 구매 대기 상태가 Part I/O에 추가된다.")
      void purchaseBulk() {
        // given
        PurchaseBulkServiceRequest request = PurchaseBulkServiceRequest.builder()
            .productId(productId)
            .amount(amount)
            .build();
        PurchaseBulkServiceResponse response = PurchaseBulkServiceResponse.builder()
            .productId(productId)
            .amount(amount)
            .build();

        Part part = Part.builder().id(partId).build();
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(amount)
            .status(status)
            .part(part)
            .build();

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByProductId(productId)).thenReturn(List.of(bom));
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(partIoRepository.save(any())).thenReturn(partIo);
        PurchaseBulkServiceResponse result = purchaseService.purchaseBulk(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
    }
  }
}
