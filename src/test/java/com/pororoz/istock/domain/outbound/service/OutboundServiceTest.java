package com.pororoz.istock.domain.outbound.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceRequest;
import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboundServiceTest {

  @InjectMocks
  OutboundService outboundService;

  @Mock
  ProductRepository productRepository;

  @Mock
  ProductIoRepository productIoRepository;

  final Long productId = 1L;
  final Long productIoId = 10L;
  final Long quantity = 10L;

  @Nested
  @DisplayName("제품 출고")
  class Outbound {

    OutboundServiceRequest request = OutboundServiceRequest.builder()
        .productId(productId).quantity(quantity)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
      void outbound() {
        // given
        Product product = Product.builder()
            .id(productId)
            .stock(150)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productId)
            .status(ProductStatus.출고대기)
            .quantity(quantity)
            .product(product)
            .build();
        OutboundServiceResponse response = OutboundServiceResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        OutboundServiceResponse result = outboundService.outbound(request);

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