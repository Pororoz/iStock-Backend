package com.pororoz.istock.domain.production.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductIoNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceRequest;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceResponse;
import com.pororoz.istock.domain.production.dto.service.UpdateProductionServiceResponse;
import com.pororoz.istock.domain.production.exception.ProductOrBomNotFoundException;
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
class ProductionServiceTest {

  @InjectMocks
  ProductionService productionService;

  @Mock
  ProductRepository productRepository;

  @Mock
  ProductIoRepository productIoRepository;

  @Mock
  PartIoRepository partIoRepository;

  final Long productId = 1L;
  final Long productIoId = 10L;
  final long quantity = 10;

  Bom createPartBom(long quantity, Product product, Part part) {
    return Bom.builder()
        .codeNumber("0").quantity(quantity)
        .product(product).part(part)
        .build();
  }

  Bom createSubAssyBom(long quantity, Product product, Product subAssy) {
    return Bom.builder()
        .codeNumber("11").quantity(quantity)
        .product(product).subAssy(subAssy)
        .build();
  }

  @Nested
  @DisplayName("제품 생산 대기 생성")
  class SaveProduction {

    SaveProductionServiceRequest request = SaveProductionServiceRequest.builder()
        .productId(productId).quantity(quantity)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("part를 소모하고 partIo, productIo에 생산 대기를 추가한다.")
      void saveProduction() {
        //given
        Part part = Part.builder().stock(20).build();
        Product product = Product.builder().id(productId).build();
        Bom bom = createPartBom(2, product, part);
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product)
            .build();
        ArgumentCaptor<List<PartIo>> listArgument = ArgumentCaptor.forClass(List.class);

        //when
        when(productRepository.findByIdWithPartsAndSubAssies(productId)).thenReturn(
            Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        SaveProductionServiceResponse response = productionService.saveWaitingProduction(request);

        //then
        PartIo savingPartIo = PartIo.builder()
            .status(PartStatus.생산대기).quantity(quantity * bom.getQuantity())
            .productIo(productIo).part(part)
            .build();
        verify(partIoRepository, times(1)).saveAll(listArgument.capture());
        assertThat(listArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(List.of(savingPartIo));
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(quantity);
        assertThat(part.getStock()).isZero();
        verifyProductIoSaved(product);
      }

      @Test
      @DisplayName("Product의 bom에 subassy가 있으면 subassy의 part도 저장한다.")
      void saveSubAssyProductIo() {
        //given
        Product subAssy = Product.builder()
            .id(productId + 1L).codeNumber("11")
            .stock(20).build();
        Product product = Product.builder().id(productId).build();
        Bom subAssyBom = createSubAssyBom(2, product, subAssy);
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();
        ArgumentCaptor<List<ProductIo>> listArgument = ArgumentCaptor.forClass(List.class);

        //when
        when(productRepository.findByIdWithPartsAndSubAssies(productId)).thenReturn(
            Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        SaveProductionServiceResponse response = productionService.saveWaitingProduction(request);

        //then
        ProductIo subAssyIo = ProductIo.builder()
            .status(ProductStatus.사내출고대기)
            .quantity(subAssyBom.getQuantity() * quantity)
            .product(subAssy)
            .superIo(productIo).build();
        verify(productIoRepository, times(1)).saveAll(listArgument.capture());
        assertThat(listArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(List.of(subAssyIo));
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(quantity);
        assertThat(subAssy.getStock()).isZero();
        verifyProductIoSaved(product);
      }

      private void verifyProductIoSaved(Product product) {
        ArgumentCaptor<ProductIo> productIoArgument = ArgumentCaptor.forClass(ProductIo.class);

        ProductIo savingProductIo = ProductIo.builder()
            .status(ProductStatus.생산대기)
            .quantity(quantity).product(product)
            .build();

        verify(productIoRepository).save(productIoArgument.capture());
        assertThat(productIoArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(savingProductIo);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("Product가 null이면 ProductOrBomNotFoundException이 발생한다.")
      void ProductNotFound() {
        //given
        //when
        when(productRepository.findByIdWithPartsAndSubAssies(productId)).thenReturn(
            Optional.empty());

        //then
        assertThrows(ProductOrBomNotFoundException.class, () ->
            productionService.saveWaitingProduction(request));
      }
    }
  }

  @Nested
  @DisplayName("제품 생산 확정")
  class ConfirmProduction {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("part와 subAssy가 포함된 제품을 구매 확정한다.")
      void confirmProductionIncludePartAndSubAssy() {
        //given
        Product product = Product.builder().id(1L).stock(0).build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId).quantity(quantity)
            .status(ProductStatus.생산대기).product(product)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .quantity(quantity).superIo(productIo)
            .status(ProductStatus.사내출고대기).build();
        PartIo partIo = PartIo.builder()
            .quantity(quantity * 2).productIo(productIo)
            .status(PartStatus.생산대기).build();

        //when
        when(productIoRepository.findById(productIoId)).thenReturn(Optional.of(productIo));
        UpdateProductionServiceResponse result = productionService.confirmProduction(productIoId);

        //then
        UpdateProductionServiceResponse response = UpdateProductionServiceResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(quantity).build();

        assertThat(result).usingRecursiveComparison().isEqualTo(response);
        assertThat(product.getStock()).isEqualTo(quantity);
        verifyIo(productIo, subAssyIo, partIo);
      }

      private void verifyIo(ProductIo productIo, ProductIo subAssyIo, PartIo partIo) {
        assertThat(productIo.getStatus()).isEqualTo(ProductStatus.생산완료);
        assertThat(subAssyIo.getStatus()).isEqualTo(ProductStatus.사내출고완료);
        assertThat(partIo.getStatus()).isEqualTo(PartStatus.생산완료);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 productIo로 조회하면 ProductIoNotFoundException이 발생한다.")
      void ProductIoNotFoundException() {
        //given
        //when
        when(productIoRepository.findById(productIoId)).thenReturn(Optional.empty());
        //then
        assertThrows(ProductIoNotFoundException.class,
            () -> productionService.confirmProduction(productIoId));
      }

    }
  }

  @Nested
  @DisplayName("제품 생산 취소")
  class CancelProduction {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("제품 생산을 취소하고 io 상태를 취소 상태로 변경한다.")
      void cancelProduction() {
        //given
        Part part = Part.builder().id(1L).stock(10).build();
        Product product = Product.builder().id(2L).stock(20).build();
        Product subAssy = Product.builder().id(3L).stock(30).build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId).quantity(quantity)
            .status(ProductStatus.생산대기).product(product)
            .build();
        PartIo partIo = PartIo.builder()
            .quantity(quantity).status(PartStatus.생산대기)
            .productIo(productIo).part(part)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .quantity(quantity).status(ProductStatus.사내출고대기)
            .superIo(productIo).product(subAssy)
            .build();

        //when
        when(productIoRepository.findById(productIoId)).thenReturn(Optional.of(productIo));
        when(productIoRepository.findBySuperIoWithProduct(any(ProductIo.class)))
            .thenReturn(List.of(subAssyIo));
        when(partIoRepository.findByProductIoWithPart(any(ProductIo.class)))
            .thenReturn(List.of(partIo));
        UpdateProductionServiceResponse result = productionService.cancelProduction(
            productIoId);

        //then
        UpdateProductionServiceResponse response = UpdateProductionServiceResponse.builder()
            .productIoId(productIoId)
            .productId(product.getId())
            .quantity(quantity).build();
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
        assertThat(part.getStock()).isEqualTo(10 + quantity);
        assertThat(subAssy.getStock()).isEqualTo(30 + quantity);
        verifyIoStatus(productIo, subAssyIo, partIo);
      }

      private void verifyIoStatus(ProductIo productIo, ProductIo subAssyIo, PartIo partIo) {
        assertThat(productIo.getStatus()).isEqualTo(ProductStatus.생산취소);
        assertThat(subAssyIo.getStatus()).isEqualTo(ProductStatus.사내출고취소);
        assertThat(partIo.getStatus()).isEqualTo(PartStatus.생산취소);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 productIoId로 찾으면 ProductIoNotFoundException이 발생한다.")
      void productIoNotFound() {
        //given
        //when
        when(productIoRepository.findById(productIoId)).thenReturn(Optional.empty());
        //then
        assertThrows(ProductIoNotFoundException.class,
            () -> productionService.cancelProduction(productIoId));
      }
    }
  }
}