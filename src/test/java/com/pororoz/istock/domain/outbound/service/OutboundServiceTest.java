package com.pororoz.istock.domain.outbound.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.outbound.dto.service.OutboundUpdateServiceRequest;
import com.pororoz.istock.domain.outbound.dto.service.OutboundUpdateServiceResponse;
import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceRequest;
import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceResponse;
import com.pororoz.istock.domain.outbound.exception.ChangeOutboundStatusException;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductIoNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

      @Test
      @DisplayName("정상적인 값을 전달하면 ProductIo가 출고대기 상태로 저장된다.")
      void outbound() {
        // given
        Product product = Product.builder()
            .id(productId)
            .stock(150)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
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

      @Test
      @DisplayName("product가 존재하지 않으면 에러가 발생한다.")
      void notFoundProduct() {
        // given
        // when
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class, () -> outboundService.outbound(request));
      }
    }
  }

  @Nested
  @DisplayName("제품 출고 확정")
  class OutboundConfirm {

    OutboundUpdateServiceRequest request = OutboundUpdateServiceRequest.builder()
        .productIoId(productIoId)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("정상적인 값으로 요청 받으면 productIoId, productId, quantity 등의 정보를 전달한다.")
      void outboundConfirm() {
        // given
        Product product = Product.builder()
            .id(productId)
            .stock(50)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .status(ProductStatus.출고대기)
            .quantity(quantity)
            .product(product)
            .build();
        OutboundUpdateServiceResponse response = OutboundUpdateServiceResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(productIoRepository.findById(productIoId)).thenReturn(Optional.of(productIo));
        OutboundUpdateServiceResponse result = outboundService.outboundConfirm(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("productIo가 존재하지 않으면 에러가 발생한다.")
      void notFoundProductIo() {
        // given
        // when
        when(productIoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductIoNotFoundException.class,
            () -> outboundService.outboundConfirm(request));
      }

      @Test
      @DisplayName("ProductIo의 status의 값이 출고대기가 아니라면 Exception이 발생한다.")
      void productIoStatusError() {
        // given
        Product product = Product.builder()
            .id(productId)
            .stock(50)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .status(ProductStatus.생산완료)
            .quantity(quantity)
            .product(product)
            .build();

        // when
        when(productIoRepository.findById(productIoId)).thenReturn(Optional.of(productIo));

        // then
        assertThrows(ChangeOutboundStatusException.class,
            () -> outboundService.outboundConfirm(request));
      }
    }
  }

  @Nested
  @DisplayName("제품 출고 취소")
  class CancelOutbound {

    OutboundUpdateServiceRequest request = OutboundUpdateServiceRequest.builder()
        .productIoId(productIoId)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("제품 출고 취소 로직을 처리한다.")
      void cancelOutbound() {
        // given
        Product product = Product.builder()
            .id(productId)
            .stock(50)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .status(ProductStatus.출고대기)
            .quantity(quantity)
            .product(product)
            .build();
        OutboundUpdateServiceResponse response = OutboundUpdateServiceResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(productIoRepository.findById(anyLong())).thenReturn(Optional.of(productIo));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        OutboundUpdateServiceResponse result = outboundService.outboundCancel(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
      @Test
      @DisplayName("productIo가 존재하지 않으면 에러가 발생한다.")
      void notFoundProductIo() {
        // given
        // when
        when(productIoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductIoNotFoundException.class,
            () -> outboundService.outboundCancel(request));
      }

      @Test
      @DisplayName("product가 존재하지 않으면 에러가 발생한다.")
      void notFoundProduct() {
        // given
        Product product = Product.builder()
            .id(productId)
            .stock(50)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .status(ProductStatus.출고대기)
            .quantity(quantity)
            .product(product)
            .build();

        // when
        when(productIoRepository.findById(anyLong())).thenReturn(Optional.of(productIo));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class,
            () -> outboundService.outboundCancel(request));
      }

      @Test
      @DisplayName("ProductIo의 status의 값이 출고대기가 아니라면 Exception이 발생한다.")
      void productIoStatusError() {
        // given
        Product product = Product.builder()
            .id(productId)
            .stock(50)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .status(ProductStatus.출고완료)
            .quantity(quantity)
            .product(product)
            .build();

        // when
        when(productIoRepository.findById(productIoId)).thenReturn(Optional.of(productIo));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // then
        assertThrows(ChangeOutboundStatusException.class,
            () -> outboundService.outboundCancel(request));
      }
    }
  }
}