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
import com.pororoz.istock.domain.part.exception.PartIoNotFoundException;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.ConfirmPurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import com.pororoz.istock.domain.purchase.exception.ChangePurchaseStatusException;
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
  long stock = 10L;
  long quantity = 3L;
  String memo = "";
  ProductStatus productStatus = ProductStatus.????????????;
  PartStatus partStatus = PartStatus.????????????;
  String SUB_ASSY_CODE_NUMBER = "11";

  @Nested
  @DisplayName("?????? ?????? ?????? ?????? ?????????")
  class purchaseProduct {

    PurchaseProductServiceRequest request = PurchaseProductServiceRequest.builder()
        .productId(productId)
        .quantity(quantity)
        .build();

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("???????????? Product??? ????????? Part??? ?????? ?????? ?????? ????????? ProductI/O??? PartI/O??? ????????????.")
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
      @DisplayName("???????????? Product??? ????????? Part??? SubAssy?????? ?????? ?????? ????????? ProductI/O?????? ????????????.")
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
            .status(ProductStatus.??????????????????)
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("???????????? ?????? Product??? ???????????? ????????? ????????????.")
      void productNotFound() {
        // given
        // when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class,
            () -> purchaseService.purchaseProduct(request));
      }
    }
  }

  @Nested
  @DisplayName("?????? ?????? ?????? ??????")
  class PurchasePart {

    PurchasePartServiceRequest request = PurchasePartServiceRequest.builder()
        .partId(partId)
        .quantity(quantity)
        .build();

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("?????? ????????? ???????????? partI/O??? ?????? ?????? ????????? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("???????????? ?????? Part??? ???????????? ????????? ????????????.")
      void partNotFound() {
        // given
        // when
        when(partRepository.findById(request.getPartId())).thenReturn(Optional.empty());

        // then
        assertThrows(PartNotFoundException.class,
            () -> purchaseService.purchasePart(request));
      }
    }
  }

  @Nested
  @DisplayName("?????? ?????? ?????? ??????")
  class ConfirmPurchasePart {

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("?????? ?????? ????????? ????????? ?????? ?????? ????????? ????????????.")
      void ConfirmPurchasePart() {
        // given
        Part part = Part.builder().id(partId).stock(stock).build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(partStatus)
            .part(part)
            .build();

        ConfirmPurchasePartServiceResponse response = ConfirmPurchasePartServiceResponse.builder()
            .partIoId(partIoId)
            .partId(partId)
            .quantity(quantity)
            .build();

        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.of(partIo));
        ConfirmPurchasePartServiceResponse result = purchaseService.confirmPurchasePart(partIoId);

        // then
        assertThat(part.getStock()).isEqualTo(stock + quantity);
        assertThat(partIo.getStatus()).isEqualTo(partStatus.????????????);
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("???????????? ?????? PartIo??? ???????????? ????????? ????????????.")
      void partIoNotFound() {
        // given
        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.empty());

        // then
        assertThrows(PartIoNotFoundException.class,
            () -> purchaseService.confirmPurchasePart(partIoId));
      }

      @Test
      @DisplayName("???????????? ????????? ?????? ??????, ?????????????????? ????????? ?????? ??? ??????.")
      void notPurchaseWaiting() {
        // given
        Part part = Part.builder().id(partId).stock(stock).build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(PartStatus.????????????)
            .part(part)
            .build();

        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.of(partIo));

        // then
        assertThrows(ChangePurchaseStatusException.class,
            () -> purchaseService.confirmPurchasePart(partIoId));

      }
    }
  }
}
