package com.pororoz.istock.domain.production.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pororoz.istock.common.exception.BomAndSubAssyNotMatchedException;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceRequest;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceResponse;
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
  final long quantity = 100;

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
        Part part = Part.builder()
            .stock(2).build();
        Bom bom = Bom.builder()
            .quantity(1)
            .part(part).build();
        Product product = Product.builder()
            .id(productId)
            .boms(List.of(bom)).build();
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();
        ArgumentCaptor<List<PartIo>> listArgument = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<ProductIo> productIoArgument = ArgumentCaptor.forClass(ProductIo.class);

        //when
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        SaveProductionServiceResponse response = productionService.saveWaitingProduction(request);

        //then
        ProductIo savingProductIo = ProductIo.builder()
            .status(ProductStatus.생산대기)
            .quantity(quantity).product(product)
            .build();
        PartIo savingPartIo = PartIo.builder()
            .status(PartStatus.생산대기).quantity(1)
            .productIo(productIo).part(part)
            .build();
        verify(productIoRepository).save(productIoArgument.capture());
        verify(partIoRepository, times(1)).saveAll(listArgument.capture());
        assertThat(productIoArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(savingProductIo);
        assertThat(listArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(List.of(savingPartIo));
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(quantity);
      }

      @Test
      @DisplayName("Product의 bom에 subassy가 있으면 subassy의 part도 저장한다.")
      void saveSubAssyProductIo() {
        //given
        String subAssyNumber = "product number";
        Bom subAssyBom = Bom.builder()
            .codeNumber("11").productNumber(subAssyNumber)
            .quantity(1).build();
        Product product = Product.builder()
            .id(productId)
            .boms(List.of(subAssyBom)).build();
        Product subAssy = Product.builder()
            .id(productId + 1L).codeNumber("11")
            .productNumber(subAssyNumber)
            .stock(10).build();
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();
        ArgumentCaptor<List<ProductIo>> listArgument = ArgumentCaptor.forClass(List.class);

        //when
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        when(productRepository.findByProductNumberIn(anyList())).thenReturn(List.of(subAssy));
        SaveProductionServiceResponse response = productionService.saveWaitingProduction(request);

        //then
        ProductIo subAssyIo = ProductIo.builder()
            .status(ProductStatus.사내출고대기)
            .quantity(subAssyBom.getQuantity())
            .product(subAssy)
            .superIo(productIo).build();
        verify(productIoRepository, times(1)).saveAll(listArgument.capture());
        assertThat(listArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(List.of(subAssyIo));
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(quantity);
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
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.empty());

        //then
        assertThrows(ProductOrBomNotFoundException.class, () ->
            productionService.saveWaitingProduction(request));
      }

      @Test
      @DisplayName("Part의 stock이 음수가 되면 PartStockMinusException이 발생한다.")
      void partStockMinus() {
        //given
        Part part = Part.builder()
            .stock(2).build();
        Bom bom1 = Bom.builder()
            .quantity(1)
            .part(part).build();
        Bom bom2 = Bom.builder()
            .quantity(2)
            .part(part).build();
        Product product = Product.builder()
            .id(productId)
            .boms(List.of(bom1, bom2)).build();
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();

        //when
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);

        //then
        assertThrows(PartStockMinusException.class,
            () -> productionService.saveWaitingProduction(request));
      }

      @Test
      @DisplayName("Product의 stock이 음수가 되면 ProductStockMinusException이 발생한다.")
      void productStockMinus() {
        //given
        String subAssyNumber = "product number";
        Bom subAssyBom = Bom.builder()
            .codeNumber("11").productNumber(subAssyNumber)
            .quantity(2).build();
        Product product = Product.builder()
            .id(productId)
            .boms(List.of(subAssyBom)).build();
        Product subAssy = Product.builder()
            .id(productId + 1L).codeNumber("11")
            .productNumber(subAssyNumber)
            .stock(1).build();
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();

        //when
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        when(productRepository.findByProductNumberIn(anyList())).thenReturn(List.of(subAssy));

        //then
        assertThrows(ProductStockMinusException.class,
            () -> productionService.saveWaitingProduction(request));
        verify(productIoRepository, times(0)).saveAll(anyList());
      }

      @Test
      @DisplayName("SubAssy Bom의 productNumber로 product를 찾을 수 없으면 SubAssyNotFoundByProductNameException이 발생한다.")
      void subAssyNotFoundByProductName() {
        //given
        String subAssyNumber = "product number";
        Bom subAssyBom = Bom.builder()
            .codeNumber("11").productNumber(subAssyNumber)
            .quantity(2).build();
        Product product = Product.builder()
            .id(productId)
            .boms(List.of(subAssyBom)).build();
        Product subAssy = Product.builder()
            .id(productId + 1L).codeNumber("11")
            .productNumber(subAssyNumber)
            .stock(1).build();
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();

        //when
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        when(productRepository.findByProductNumberIn(anyList())).thenReturn(List.of(subAssy));

        //then
        assertThrows(ProductStockMinusException.class,
            () -> productionService.saveWaitingProduction(request));
        verify(productIoRepository, times(0)).saveAll(anyList());
      }

      @Test
      @DisplayName("Sub assy로 등록된 BOM과 sub assy가 일치하지 않으면 예외가 발생한다.")
      void bomAndSubAssyNotMatch() {
        //given
        String subAssyNumber = "product number";
        Bom subAssyBom = Bom.builder()
            .codeNumber("11").productNumber(subAssyNumber)
            .quantity(1).build();
        Product product = Product.builder()
            .id(productId)
            .boms(List.of(subAssyBom)).build();
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();

        //when
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);
        when(productRepository.findByProductNumberIn(anyList())).thenReturn(List.of());

        //then
        assertThrows(BomAndSubAssyNotMatchedException.class,
            () -> productionService.saveWaitingProduction(request));
      }

      @Test
      @DisplayName("BOM을 partIo에 저장할 때 part가 null이라면 IllegalArgumentException이 발생한다.")
      void partNull() {
        //given
        Bom bom = Bom.builder()
            .quantity(1).build();
        Product product = Product.builder()
            .id(productId)
            .boms(List.of(bom)).build();
        ProductIo productIo = ProductIo.builder()
            .quantity(quantity).product(product).id(1L)
            .build();
        ArgumentCaptor<ProductIo> productIoArgument = ArgumentCaptor.forClass(ProductIo.class);

        //when
        when(productRepository.findByIdWithParts(productId)).thenReturn(Optional.of(product));
        when(productIoRepository.save(any(ProductIo.class))).thenReturn(productIo);

        //then
        assertThrows(IllegalArgumentException.class,
            () -> productionService.saveWaitingProduction(request));
        ProductIo savingProductIo = ProductIo.builder()
            .status(ProductStatus.생산대기)
            .quantity(quantity).product(product)
            .build();
        verify(productIoRepository).save(productIoArgument.capture());
        assertThat(productIoArgument.getValue()).usingRecursiveComparison()
            .isEqualTo(savingProductIo);
      }
    }
  }
}