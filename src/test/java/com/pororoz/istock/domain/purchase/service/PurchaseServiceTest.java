package com.pororoz.istock.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.exception.PartNullException;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
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
  PartRepository partRepository;
  @Mock
  BomRepository bomRepository;
  @Mock
  ProductIoRepository productIoRepository;
  @Mock
  PartIoRepository partIoRepository;

  Long productId = 1L;
  Long bomId = 1L;
  Long partId = 1L;
  Long productIoId = 1L;
  Long partIoId = 1L;
  String locationNumber = "L5.L4";
  String codeNumber = "";
  String productNumber = "GNS-TG01";
  long quantity = 3L;
  String memo = "";
  ProductStatus productStatus = ProductStatus.구매대기;
  PartStatus partStatus = PartStatus.구매대기;
  String SUB_ASSY_CODE_NUMBER = "11";

  @Nested
  @DisplayName("제품 자재 일괄 구매 테스트")
  class purchaseProduct {

    PurchaseProductServiceRequest request = PurchaseProductServiceRequest.builder()
        .productId(productId)
        .quantity(quantity)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("입력받은 Product에 포함된 Part에 대한 구매 대기 상태가 ProductI/O와 PartI/O에 추가된다.")
      void purchaseProduct() {
        // given
        Part part = Part.builder().id(partId).build();
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .productNumber(productNumber)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(partStatus)
            .part(part)
            .build();

        PurchaseProductServiceResponse response = PurchaseProductServiceResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productIoRepository.save(any())).thenReturn(productIo);
        when(bomRepository.findByProductId(productId)).thenReturn(List.of(bom));
        when(partIoRepository.save(any())).thenReturn(partIo);
        PurchaseProductServiceResponse result = purchaseService.purchaseProduct(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }


      @Test
      @DisplayName("입력받은 Product에 포함된 Part가 SubAssy이면 구매 대기 상태가 ProductI/O에만 추가된다.")
      void purchaseProductIncludeSubAssy() {
        // given
        Part part = Part.builder().id(partId).build();
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(SUB_ASSY_CODE_NUMBER)
            .quantity(quantity)
            .productNumber(productNumber)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();

        PurchaseProductServiceResponse response = PurchaseProductServiceResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();

        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productIoRepository.save(any())).thenReturn(productIo);
        when(bomRepository.findByProductId(productId)).thenReturn(List.of(bom));
        when(productRepository.findByProductNumber(any())).thenReturn(
            Optional.of(product));
        PurchaseProductServiceResponse result = purchaseService.purchaseProduct(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 Product를 요청하면 오류가 발생한다.")
      void productNotFound() {
        // given
        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class,
            () -> purchaseService.purchaseProduct(request));
      }

      @Test
      @DisplayName("sub assy의 품번이 product 테이블에 존재하지 않으면 오류가 발생한다.")
      void productWithSubAssyNotFound() {
        // given
        Part part = Part.builder().id(partId).build();
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(SUB_ASSY_CODE_NUMBER)
            .quantity(quantity)
            .productNumber(productNumber)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();

        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productIoRepository.save(any())).thenReturn(productIo);
        when(bomRepository.findByProductId(productId)).thenReturn(List.of(bom));
        when(productRepository.findByProductNumber(any())).thenReturn(
            Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class,
            () -> purchaseService.purchaseProduct(request));


      }

      @Test
      @DisplayName("검색한 bom이 sub assy가 아닌 경우, 해당 bom의 part가 null이면 오류가 발생한다.")
      void partNullException() {
        // given
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .productNumber(productNumber)
            .memo(memo)
            .product(product)
            .part(null)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();

        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productIoRepository.save(any())).thenReturn(productIo);
        when(bomRepository.findByProductId(productId)).thenReturn(List.of(bom));

        //then
        assertThrows(PartNullException.class,
            () -> purchaseService.purchaseProduct(request));
      }
    }
  }

  @Nested
  @DisplayName("제품 자재 개별 구매")
  class PurchasePart {

    PurchasePartServiceRequest request = PurchasePartServiceRequest.builder()
        .partId(partId)
        .quantity(quantity)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("개별 구매를 요청하면 partI/O에 구매 대기 내역을 생성한다.")
      void purchasePart() {
        // given
        Part part = Part.builder().id(partId).build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(partStatus)
            .part(part)
            .build();

        PurchasePartServiceResponse response = PurchasePartServiceResponse.builder()
            .partId(partId)
            .quantity(quantity)
            .build();

        // when
        when(partRepository.findById(request.getPartId())).thenReturn(Optional.of(part));
        when(partIoRepository.save(any())).thenReturn(partIo);
        PurchasePartServiceResponse result = purchaseService.purchasePart(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 Part를 요청하면 오류가 발생한다.")
      void productNotFound() {
        // given
        // when
        when(partRepository.findById(request.getPartId())).thenReturn(Optional.empty());

        // then
        assertThrows(PartNotFoundException.class,
            () -> purchaseService.purchasePart(request));
      }
    }
  }
}
