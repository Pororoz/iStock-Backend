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
import com.pororoz.istock.domain.product.exception.InvalidSubAssyTypeException;
import com.pororoz.istock.domain.product.exception.ProductIoNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.UpdatePurchaseServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.UpdateSubAssyOutsourcingServiceResponse;
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
  Long productIdAsSubAssy = 100L;
  Long bomId = 1L;
  Long partId = 1L;
  Long productIoId = 1L;
  Long productIoIdAsSubAssy = 100L;
  Long partIoId = 1L;
  String locationNumber = "L5.L4";
  String codeNumber = "";
  long stock = 10L;
  long quantity = 3L;
  String memo = "";
  ProductStatus productStatus = ProductStatus.구매대기;
  ProductStatus subAssyStatus = ProductStatus.외주생산대기;
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
      @DisplayName("입력받은 Product에 포함된 Part의 구매대기 상태가 PartI/O에 추가되고, 제품의 구매대기 상태가 ProductI/O에 추가된다.")
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
      @DisplayName("입력받은 Product에 포함된 Part가 SubAssy이면 외주생산대기 상태가 ProductI/O에 추가된다.")
      void purchaseProductIncludeSubAssy() {
        // given
        Product product = Product.builder().id(productId).build();
        Product subAssy = Product.builder()
            .id(productIdAsSubAssy).codeNumber(SUB_ASSY_CODE_NUMBER)
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
            .status(ProductStatus.외주생산대기)
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
  @DisplayName("제품 자재 구매 확정")
  class ConfirmPurchasePart {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("구매 대기상태의 자재를 구매확정 상태로 변경한다. 다른 모든 자재(SubAssy 포함)의 상태가 구매확정 상태라면, 해당 제품의 상태가 구매완료 상태로 변경된다")
      void confirmPurchasePart() {
        // given
        ProductIo productIo = ProductIo.builder().id(productId).status(productStatus).build();
        Part part = Part.builder().id(partId).stock(stock).build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(partStatus)
            .part(part)
            .productIo(productIo)
            .build();
        // 제품 구매완료 확인을 위한 PartIo
        PartIo partIo1 = PartIo.builder()
            .status(PartStatus.구매확정)
            .part(part)
            .productIo(productIo)
            .build();

        UpdatePurchaseServiceResponse response = UpdatePurchaseServiceResponse.builder()
            .partIoId(partIoId)
            .partId(partId)
            .quantity(quantity)
            .build();

        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.of(partIo));
        when(partIoRepository.findByProductIoWithPart(any(ProductIo.class)))
            .thenReturn(List.of(partIo, partIo1));
        UpdatePurchaseServiceResponse result = purchaseService.confirmPurchasePart(partIoId);

        // then
        assertThat(part.getStock()).isEqualTo(stock + quantity);
        assertThat(partIo.getStatus()).isEqualTo(partStatus.구매확정);
        assertThat(productIo.getStatus()).isEqualTo(productStatus.구매완료);
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 PartIo를 요청하면 오류가 발생한다.")
      void partIoNotFound() {
        // given
        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.empty());

        // then
        assertThrows(PartIoNotFoundException.class,
            () -> purchaseService.confirmPurchasePart(partIoId));
      }

      @Test
      @DisplayName("구매대기 상태가 아닌 경우, 구매확정으로 상태를 바꿀 수 없다.")
      void notPurchaseWaiting() {
        // given
        Part part = Part.builder().id(partId).stock(stock).build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(PartStatus.구매확정)
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

  @Nested
  @DisplayName("제품 자재 구매 취소")
  class CancelPurchasePart {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("구매대기 상태의 자재를 구매취소 상태로 변경한다.")
      void cancelPurchasePart() {
        // given
        Part part = Part.builder().id(partId).stock(stock).build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(partStatus)
            .part(part)
            .build();

        UpdatePurchaseServiceResponse response = UpdatePurchaseServiceResponse.builder()
            .partIoId(partIoId)
            .partId(partId)
            .quantity(quantity)
            .build();

        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.of(partIo));
        UpdatePurchaseServiceResponse result = purchaseService.cancelPurchasePart(partIoId);

        // then
        assertThat(part.getStock()).isEqualTo(stock);
        assertThat(partIo.getStatus()).isEqualTo(partStatus.구매취소);
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 PartIo를 요청하면 오류가 발생한다.")
      void partIoNotFound() {
        // given
        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.empty());

        // then
        assertThrows(PartIoNotFoundException.class,
            () -> purchaseService.cancelPurchasePart(partIoId));
      }

      @Test
      @DisplayName("구매대기 상태가 아닌 경우, 구매취소로 상태를 바꿀 수 없다.")
      void notPurchaseWaiting() {
        // given
        Part part = Part.builder().id(partId).stock(stock).build();
        PartIo partIo = PartIo.builder()
            .id(partIoId)
            .quantity(quantity)
            .status(PartStatus.구매확정)
            .part(part)
            .build();

        // when
        when(partIoRepository.findById(partIoId)).thenReturn(Optional.of(partIo));

        // then
        assertThrows(ChangePurchaseStatusException.class,
            () -> purchaseService.cancelPurchasePart(partIoId));

      }
    }
  }

  @Nested
  @DisplayName("SubAssy 외주 생산 확정")
  class ConfirmSubAssyOutsourcing {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("외주생산대기 상태의 SubAssy를 외주생산확정 상태로 변경한다. 다른 모든 SubAssy(자재 포함)의 상태가 외주생산확정 상태라면, 해당 제품의 상태가 구매완료 상태로 변경된다")
      void confirmSubAssyOutsourcing() {
        // given
        Product product = Product.builder().id(productId).build();
        Product subAssy = Product.builder()
            .id(productIdAsSubAssy).codeNumber(SUB_ASSY_CODE_NUMBER)
            .stock(stock)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .id(productIoIdAsSubAssy)
            .quantity(quantity)
            .status(subAssyStatus)
            .superIo(productIo)
            .product(subAssy)
            .build();
        // 제품 구매완료 확인을 위한 subAssyIo
        ProductIo subAssyIo1 = ProductIo.builder()
            .status(ProductStatus.외주생산확정)
            .superIo(productIo)
            .product(subAssy)
            .build();

        UpdateSubAssyOutsourcingServiceResponse response = UpdateSubAssyOutsourcingServiceResponse.builder()
            .productIoId(productIoIdAsSubAssy)
            .productId(productIdAsSubAssy)
            .quantity(quantity)
            .build();

        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.of(subAssyIo));
        when(productIoRepository.findBySuperIoWithProduct(any(ProductIo.class)))
            .thenReturn(List.of(subAssyIo, subAssyIo1));
        UpdateSubAssyOutsourcingServiceResponse result = purchaseService.confirmSubAssyOutsourcing(
            productIoIdAsSubAssy);

        // then
        assertThat(subAssy.getStock()).isEqualTo(stock + quantity);
        assertThat(subAssyIo.getStatus()).isEqualTo(productStatus.외주생산확정);
        assertThat(productIo.getStatus()).isEqualTo(productStatus.구매완료);
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 ProductIo를 요청하면 오류가 발생한다.")
      void partIoNotFound() {
        // given
        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.empty());

        // then
        assertThrows(ProductIoNotFoundException.class,
            () -> purchaseService.confirmSubAssyOutsourcing(productIoIdAsSubAssy));
      }

      @Test
      @DisplayName("해당 ProductIo가 SubAssy가 아니면 오류가 발생한다.")
      void invalidSubAssyType() {
        // given
        Product product = Product.builder().id(productId).build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();

        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.of(productIo));

        // then
        assertThrows(InvalidSubAssyTypeException.class,
            () -> purchaseService.confirmSubAssyOutsourcing(productIoIdAsSubAssy));
      }

      @Test
      @DisplayName("외주생산대기 상태가 아닌 경우, 외주생산확정 상태로 변경할 수 없다.")
      void invalidSubAssyStatus() {
        // given
        Product product = Product.builder().id(productId).build();
        Product subAssy = Product.builder()
            .id(productIdAsSubAssy).codeNumber(SUB_ASSY_CODE_NUMBER)
            .stock(stock)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .id(productIoIdAsSubAssy)
            .quantity(quantity)
            .status(ProductStatus.외주생산확정)
            .product(product)
            .superIo(productIo)
            .product(subAssy)
            .build();

        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.of(subAssyIo));

        // then
        assertThrows(ChangePurchaseStatusException.class,
            () -> purchaseService.confirmSubAssyOutsourcing(productIoIdAsSubAssy));
      }
    }
  }

  @Nested
  @DisplayName("SubAssy 구매 취소")
  class CancelSubAssyOutsourcing {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("외주생산대기 상태의 SubAssy를 외주생산취소 상태로 변경한다.")
      void cancelSubAssyOutsourcing() {
        // given
        Product product = Product.builder().id(productId).build();
        Product subAssy = Product.builder()
            .id(productIdAsSubAssy).codeNumber(SUB_ASSY_CODE_NUMBER)
            .stock(stock)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .id(productIoIdAsSubAssy)
            .quantity(quantity)
            .status(ProductStatus.외주생산대기)
            .superIo(productIo)
            .product(subAssy)
            .build();

        UpdateSubAssyOutsourcingServiceResponse response = UpdateSubAssyOutsourcingServiceResponse.builder()
            .productIoId(productIoIdAsSubAssy)
            .productId(productIdAsSubAssy)
            .quantity(quantity)
            .build();

        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.of(subAssyIo));
        UpdateSubAssyOutsourcingServiceResponse result = purchaseService.cancelSubAssyOutsourcing(
            productIoIdAsSubAssy);

        // then
        assertThat(subAssyIo.getStatus()).isEqualTo(productStatus.외주생산취소);
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 ProductIo를 요청하면 오류가 발생한다.")
      void partIoNotFound() {
        // given
        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.empty());

        // then
        assertThrows(ProductIoNotFoundException.class,
            () -> purchaseService.cancelSubAssyOutsourcing(productIoIdAsSubAssy));
      }

      @Test
      @DisplayName("해당 ProductIo가 SubAssy가 아니면 오류가 발생한다.")
      void invalidSubAssyType() {
        // given
        Product product = Product.builder().id(productId).build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();

        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.of(productIo));

        // then
        assertThrows(InvalidSubAssyTypeException.class,
            () -> purchaseService.cancelSubAssyOutsourcing(productIoIdAsSubAssy));
      }

      @Test
      @DisplayName("외주생산대기 상태가 아닌 경우, 외주생산취소 상태로 변경할 수 없다.")
      void invalidSubAssyStatus() {
        // given
        Product product = Product.builder().id(productId).build();
        Product subAssy = Product.builder()
            .id(productIdAsSubAssy).codeNumber(SUB_ASSY_CODE_NUMBER)
            .stock(stock)
            .build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId)
            .quantity(quantity)
            .status(productStatus)
            .product(product)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .id(productIoIdAsSubAssy)
            .quantity(quantity)
            .status(ProductStatus.외주생산확정)
            .product(product)
            .superIo(productIo)
            .product(subAssy)
            .build();

        // when
        when(productIoRepository.findById(productIoIdAsSubAssy)).thenReturn(Optional.of(subAssyIo));

        // then
        assertThrows(ChangePurchaseStatusException.class,
            () -> purchaseService.cancelSubAssyOutsourcing(productIoIdAsSubAssy));
      }
    }
  }
}
