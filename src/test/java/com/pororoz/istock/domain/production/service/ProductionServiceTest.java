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
import com.pororoz.istock.domain.production.exception.PartStockMinusException;
import com.pororoz.istock.domain.production.exception.ProductOrBomNotFoundException;
import com.pororoz.istock.domain.production.exception.ProductStockMinusException;
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
  @DisplayName("?????? ?????? ?????? ??????")
  class SaveProduction {

    SaveProductionServiceRequest request = SaveProductionServiceRequest.builder()
        .productId(productId).quantity(quantity)
        .build();

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("part??? ???????????? partIo, productIo??? ?????? ????????? ????????????.")
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
            .status(PartStatus.????????????).quantity(quantity * bom.getQuantity())
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
      @DisplayName("Product??? bom??? subassy??? ????????? subassy??? part??? ????????????.")
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
            .status(ProductStatus.??????????????????)
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
            .status(ProductStatus.????????????)
            .quantity(quantity).product(product)
            .build();

        verify(productIoRepository).save(productIoArgument.capture());
        assertThat(productIoArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(savingProductIo);
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("Product??? null?????? ProductOrBomNotFoundException??? ????????????.")
      void ProductNotFound() {
        //given
        //when
        when(productRepository.findByIdWithPartsAndSubAssies(productId)).thenReturn(
            Optional.empty());

        //then
        assertThrows(ProductOrBomNotFoundException.class, () ->
            productionService.saveWaitingProduction(request));
      }

      @Test
      @DisplayName("Part??? stock??? ????????? ?????? PartStockMinusException??? ????????????.")
      void partStockMinus() {
        //given
        Part part = Part.builder().stock(2).build();
        Product product = Product.builder().id(productId).build();
        createPartBom(1, product, part);
        createPartBom(2, product, part);
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();

        //when
        when(productRepository.findByIdWithPartsAndSubAssies(productId)).thenReturn(
            Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);

        //then
        assertThrows(PartStockMinusException.class,
            () -> productionService.saveWaitingProduction(request));
      }

      @Test
      @DisplayName("Product??? stock??? ????????? ?????? ProductStockMinusException??? ????????????.")
      void productStockMinus() {
        //given
        Product subAssy = Product.builder()
            .id(productId + 1L).codeNumber("11")
            .stock(1).build();
        Product product = Product.builder().id(productId).build();
        createSubAssyBom(2, product, subAssy);
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product)
            .build();

        //when
        when(productRepository.findByIdWithPartsAndSubAssies(productId)).thenReturn(
            Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);

        //then
        assertThrows(ProductStockMinusException.class,
            () -> productionService.saveWaitingProduction(request));
      }
    }
  }

  @Nested
  @DisplayName("?????? ?????? ??????")
  class ConfirmProduction {

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("part??? subAssy??? ????????? ????????? ?????? ????????????.")
      void confirmProductionIncludePartAndSubAssy() {
        //given
        Product product = Product.builder().id(1L).stock(0).build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId).quantity(quantity)
            .status(ProductStatus.????????????).product(product)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .quantity(quantity).superIo(productIo)
            .status(ProductStatus.??????????????????).build();
        PartIo partIo = PartIo.builder()
            .quantity(quantity * 2).productIo(productIo)
            .status(PartStatus.????????????).build();

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
        assertThat(productIo.getStatus()).isEqualTo(ProductStatus.????????????);
        assertThat(subAssyIo.getStatus()).isEqualTo(ProductStatus.??????????????????);
        assertThat(partIo.getStatus()).isEqualTo(PartStatus.????????????);
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("???????????? ?????? productIo??? ???????????? ProductIoNotFoundException??? ????????????.")
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
  @DisplayName("?????? ?????? ??????")
  class CancelProduction {

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("?????? ????????? ???????????? io ????????? ?????? ????????? ????????????.")
      void cancelProduction() {
        //given
        Part part = Part.builder().id(1L).stock(10).build();
        Product product = Product.builder().id(2L).stock(20).build();
        Product subAssy = Product.builder().id(3L).stock(30).build();
        ProductIo productIo = ProductIo.builder()
            .id(productIoId).quantity(quantity)
            .status(ProductStatus.????????????).product(product)
            .build();
        PartIo partIo = PartIo.builder()
            .quantity(quantity).status(PartStatus.????????????)
            .productIo(productIo).part(part)
            .build();
        ProductIo subAssyIo = ProductIo.builder()
            .quantity(quantity).status(ProductStatus.??????????????????)
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
        assertThat(productIo.getStatus()).isEqualTo(ProductStatus.????????????);
        assertThat(subAssyIo.getStatus()).isEqualTo(ProductStatus.??????????????????);
        assertThat(partIo.getStatus()).isEqualTo(PartStatus.????????????);
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("???????????? ?????? productIoId??? ????????? ProductIoNotFoundException??? ????????????.")
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