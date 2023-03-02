package com.pororoz.istock.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        ArgumentCaptor<List<PartIo>> partArgument = ArgumentCaptor.forClass(List.class);

        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productIoRepository.save(any())).thenReturn(productIo);
        when(bomRepository.findByProductId(productId)).thenReturn(List.of(bom));
        when(partIoRepository.saveAll(anyList())).thenReturn(List.of(partIo));
        PurchaseProductServiceResponse result = purchaseService.purchaseProduct(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
        verify(partIoRepository).saveAll(partArgument.capture());
        assertThat(partArgument.getValue()).hasSize(1);
      }


      @Test
      @DisplayName("입력받은 Product에 포함된 Part가 SubAssy이면 구매 대기 상태가 ProductI/O에만 추가된다.")
      void purchaseProductIncludeSubAssy() {
        // given
        Part part = Part.builder().id(partId).build();
        Product product = Product.builder().id(productId).build();
        Product subAssy = Product.builder()
            .id(10000L).codeNumber(SUB_ASSY_CODE_NUMBER)
            .build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(SUB_ASSY_CODE_NUMBER)
            .quantity(quantity)
            .subAssy(subAssy)
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
        ProductIo subAssyIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(ProductStatus.외주구매대기)
            .product(product)
            .superIo(productIo)
            .product(subAssy)
            .build();

        PurchaseProductServiceResponse response = PurchaseProductServiceResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();
        ArgumentCaptor<List<ProductIo>> productArgument = ArgumentCaptor.forClass(List.class);

        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        when(bomRepository.findByProductId(productId)).thenReturn(List.of(bom));
        when(productIoRepository.saveAll(anyList())).thenReturn(List.of(subAssyIo));
        PurchaseProductServiceResponse result = purchaseService.purchaseProduct(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
        verify(productIoRepository).saveAll(productArgument.capture());
        assertThat(productArgument.getValue()).hasSize(1);
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
      @DisplayName("검색한 bom이 sub assy가 아닌 경우, 해당 bom의 part가 null이면 오류가 발생한다.")
      void partNullException() {
        // given
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
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

        //then
        assertThrows(IllegalArgumentException.class,
            () -> purchaseService.purchaseProduct(request));
      }

      @Test
      @DisplayName("검색한 bom이 sub assy인 경우, 해당 bom의 subAssy가 null이면 오류가 발생한다.")
      void subAssyNullException() {
        // given
        Product product = Product.builder().id(productId).build();
        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(SUB_ASSY_CODE_NUMBER)
            .quantity(quantity)
            .memo(memo)
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

        //then
        assertThrows(IllegalArgumentException.class,
            () -> purchaseService.purchaseProduct(request));
      }
    }
  }
}
