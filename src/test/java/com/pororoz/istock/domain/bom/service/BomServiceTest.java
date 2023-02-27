package com.pororoz.istock.domain.bom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.BomNotFoundException;
import com.pororoz.istock.domain.bom.exception.BomProductNumberDuplicatedException;
import com.pororoz.istock.domain.bom.exception.DuplicateBomException;
import com.pororoz.istock.domain.bom.exception.InvalidProductBomException;
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
import com.pororoz.istock.domain.bom.exception.SubAssyCannotHaveSubAssyException;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BomServiceTest {

  @InjectMocks
  BomService bomService;

  @Mock
  BomRepository bomRepository;

  @Mock
  PartRepository partRepository;

  @Mock
  ProductRepository productRepository;

  String subAssyCodeNumber = "11";

  @Nested
  @DisplayName("제품 BOM 행 추가 로직 테스트")
  class SaveBom {

    final Long bomId = 1L;
    final String locationNumber = "L5.L4";
    final String codeNumber = "";
    final long quantity = 3L;
    final String memo = "";
    final String productNumber = "1";
    final Long partId = 1L;
    final Long productId = 2L;

    SaveBomServiceRequest request = SaveBomServiceRequest.builder()
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .productId(productId)
        .partId(partId)
        .build();

    SaveBomServiceRequest subAssyRequest = SaveBomServiceRequest.builder()
        .locationNumber(locationNumber)
        .codeNumber(subAssyCodeNumber)
        .quantity(quantity)
        .memo(memo)
        .productNumber(productNumber)
        .productId(productId)
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
        .product(product).build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("입력값으로 적절한 값이 들어오면 BOM이 정상적으로 추가된다.")
      void saveBom() {
        // given
        BomServiceResponse response = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        when(partRepository.findById(anyLong())).thenReturn(Optional.of(part));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            anyLong())).thenReturn(Optional.empty());
        when(bomRepository.save(any(Bom.class))).thenReturn(bom);

        BomServiceResponse result = bomService.saveBom(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("sub assy인 BOM을 저장한다.")
      void saveSubAssyBom() {
        BomServiceResponse response = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .productNumber(productNumber)
            .productId(productId)
            .build();

        Product subAssy = Product.builder()
            .id(productId + 10)
            .codeNumber(subAssyCodeNumber)
            .productNumber(productNumber).build();

        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .productNumber(productNumber)
            .product(product)
            .build();

        // when
        when(productRepository.findByProductNumber(productNumber)).thenReturn(
            Optional.of(subAssy));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(locationNumber, productId,
            null)).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndProductNumber(productId, productNumber)).thenReturn(
            Optional.empty());
        when(bomRepository.save(any(Bom.class))).thenReturn(bom);
        BomServiceResponse result = bomService.saveBom(subAssyRequest);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("partId에 해당하는 part가 존재하지 않으면 예외가 발생한다.")
      void partNotExist() {
        //given
        //when
        when(partRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThrows(PartNotFoundException.class,
            () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("productId에 해당하는 product가 존재하지 않으면 예외가 발생한다.")
      void productNotExist() {
        //given
        //when
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        //then
        assertThrows(ProductNotFoundException.class,
            () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("location_number, product_id, part_id이 이미 존재하는 조합이면 예외가 발생한다.")
      void duplicateBom() {
        //given
        //when
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            anyLong())).thenReturn(Optional.of(bom));

        //then
        assertThrows(DuplicateBomException.class,
            () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("sub assy BOM에 partId가 들어오면 InvalidSubAssyBomException이 발생한다.")
      void invalidSubAssyBomExceptionWithPartId() {
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .productNumber(productNumber)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        // then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("sub assy BOM에 productNumber가 없으면 InvalidSubAssyBomException이 발생한다.")
      void invalidSubAssyBomExceptionWithoutProductNumber() {
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .productId(productId)
            .build();

        // when
        // then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("Sub assy로 저장할 BOM의 productNumber로 찾은 제품이 sub assy가 아니면 예외가 발생한다.")
      void subAssy() {
        //given
        Product notSubAssy = Product.builder().id(productId).codeNumber("1").build();

        //when
        when(productRepository.findByProductNumber(productNumber))
            .thenReturn(Optional.of(notSubAssy));

        //then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.saveBom(subAssyRequest));
      }

      @Test
      @DisplayName("Product BOM 저장 시, productNumber가 존재하면 InvalidProductBomException이 발생한다.")
      void saveProductBomWithProductNumber() {
        //given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .productNumber(productNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .productId(productId)
            .build();
        //when
        //then
        assertThrows(InvalidProductBomException.class, () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("Product BOM 저장 시, partId가 null이면 InvalidProductBomException이 발생한다.")
      void saveProductBomWithoutPartId() {
        //given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .productId(productId)
            .build();
        //when
        //then
        assertThrows(InvalidProductBomException.class, () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("하나의 제품에 null을 제외한 중복된 product number가 있다면 BomProductNumberDuplicatedException이 발생한다.")
      void duplicateProductNumber() {
        //given
        Product subAssy = Product.builder().id(productId)
            .codeNumber(subAssyCodeNumber)
            .productNumber(productNumber).build();

        // when
        when(productRepository.findByProductNumber(productNumber)).thenReturn(
            Optional.of(subAssy));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            eq(null))).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndProductNumber(productId, productNumber)).thenReturn(
            Optional.of(mock(Bom.class)));

        //then
        assertThrows(BomProductNumberDuplicatedException.class,
            () -> bomService.saveBom(subAssyRequest));
      }

      @Test
      @DisplayName("Sub assy는 sub assy를 BOM으로 저장할 수 없다.")
      void subAssyCannotHaveSubAssy() {
        Product superSubAssy = Product.builder()
            .id(productId)
            .codeNumber(subAssyCodeNumber)
            .productNumber("super sub assy").build();
        Product subAssy = Product.builder()
            .id(productId + 10)
            .codeNumber(subAssyCodeNumber)
            .productNumber(productNumber).build();

        // when
        when(productRepository.findByProductNumber(productNumber)).thenReturn(
            Optional.of(subAssy));
        when(productRepository.findById(productId)).thenReturn(Optional.of(superSubAssy));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(locationNumber, productId,
            null)).thenReturn(Optional.empty());

        // then
        assertThrows(SubAssyCannotHaveSubAssyException.class,
            () -> bomService.saveBom(subAssyRequest));
      }
    }
  }

  @Nested
  @DisplayName("제품 BOM 행 삭제 로직 테스트")
  class DeleteBom {

    Long bomId;
    String locationNumber;
    String codeNumber;
    Long quantity;
    String memo;
    Long partId;
    Long productId;

    @BeforeEach
    void setup() {
      bomId = 1L;
      locationNumber = "L5.L4";
      codeNumber = "";
      quantity = 3L;
      memo = "";
      partId = 1L;
      productId = 2L;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 BOM ID를 전송하면 해당 BOM을 삭제한다.")
      void deleteBom() {
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

        BomServiceResponse response = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        BomServiceResponse result = bomService.deleteBom(bomId);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("만약 존재하지 않는 BOM을 삭제하려고 하면 BomNotFoundException를 반환한다.")
      void bomNotFound() {
        // given
        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.empty());

        // then
        assertThrows(BomNotFoundException.class,
            () -> bomService.deleteBom(bomId));
      }
    }
  }

  @Nested
  @DisplayName("제품 BOM 행 수정 로직 테스트")
  class UpdateBom {

    final Long bomId = 1L;
    final String locationNumber = "L5.L4";
    final String codeNumber = "";
    final Long quantity = 3L;
    final String memo = "";
    final Long partId = 1L;
    final Long productId = 2L;
    final String subAssyProductNumber = "number";
    final String newLocationNumber = "new location";
    final String newCodeNumber = "new code";
    final Long newQuantity = 5L;
    final String newMemo = "new";
    final Long newPartId = 3L;
    final Long newProductId = 4L;
    final String newProductNumber = "new number";

    Part part = Part.builder().id(partId).build();
    Product product = Product.builder().id(productId).build();
    Part newPart = Part.builder().id(newPartId).build();
    Product newProduct = Product.builder().id(newProductId).build();
    Bom bom = Bom.builder()
        .id(bomId)
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .part(part)
        .product(product)
        .build();

    UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
        .bomId(bomId)
        .locationNumber(newLocationNumber)
        .codeNumber(newCodeNumber)
        .quantity(newQuantity)
        .memo(newMemo)
        .partId(newPartId)
        .productId(newProductId)
        .build();

    UpdateBomServiceRequest subAssyRequest = UpdateBomServiceRequest.builder()
        .bomId(bomId)
        .locationNumber(newLocationNumber)
        .productNumber(subAssyProductNumber)
        .codeNumber(subAssyCodeNumber)
        .quantity(newQuantity)
        .memo(newMemo)
        .productId(newProductId)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("BOM 수정에 성공한다.")
      void updateBom() {
        // given
        BomServiceResponse response = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(partRepository.findById(any())).thenReturn(Optional.of(newPart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(newProduct));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            anyLong())).thenReturn(Optional.empty());
        BomServiceResponse result = bomService.updateBom(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("Sub assy로 BOM 수정에 성공한다.")
      void updateToSubAssyBom() {
        // given
        Product SubAssy = Product.builder()
            .id(1000L)
            .codeNumber(subAssyCodeNumber)
            .productNumber(subAssyProductNumber)
            .build();

        BomServiceResponse response = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .productNumber(subAssyProductNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .productId(newProductId)
            .build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(newProduct));
        when(productRepository.findByProductNumber(subAssyProductNumber))
            .thenReturn(Optional.of(SubAssy));
        when(
            bomRepository.findByLocationNumberAndProductIdAndPartId(newLocationNumber, newProductId,
                null)).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndProductNumber(newProductId, subAssyProductNumber))
            .thenReturn(Optional.empty());
        BomServiceResponse result = bomService.updateBom(subAssyRequest);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("BOM id에 해당하는 BOM이 존재하지 않으면 예외가 발생한다.")
      void bomNotFound() {
        //given
        //when
        when(bomRepository.findById(bomId)).thenReturn(Optional.empty());

        //then
        assertThrows(BomNotFoundException.class,
            () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("partId에 해당하는 part가 존재하지 않으면 예외가 발생한다.")
      void partNotExist() {
        //given
        //when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(partRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThrows(PartNotFoundException.class,
            () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("productId에 해당하는 product가 존재하지 않으면 예외가 발생한다.")
      void productNotExist() {
        //given
        //when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(partRepository.findById(any())).thenReturn(Optional.of(newPart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        //then
        assertThrows(ProductNotFoundException.class,
            () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("location_number, product_id, part_id이 이미 존재하는 조합이면 예외가 발생한다.")
      void duplicateBom() {
        //given
        Bom ExistedBom = Bom.builder()
            .id(bomId + 1)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .part(newPart)
            .product(newProduct)
            .build();

        //when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(partRepository.findById(any())).thenReturn(Optional.of(newPart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(newProduct));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            anyLong())).thenReturn(Optional.of(ExistedBom));

        //then
        assertThrows(DuplicateBomException.class,
            () -> bomService.updateBom(request));
      }

      // save와 같은 validateRequest를 사용하기 때문에 테스트 코드가 거의 같다.
      @Test
      @DisplayName("sub assy BOM에 partId가 들어오면 InvalidSubAssyBomException이 발생한다.")
      void invalidSubAssyBomExceptionWithPartId() {
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .productNumber(newProductNumber)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        // then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("sub assy BOM에 productNumber가 없으면 InvalidSubAssyBomException이 발생한다.")
      void invalidSubAssyBomExceptionWithoutProductNumber() {
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .locationNumber(newLocationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .productId(newProductId)
            .build();

        // when
        // then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("Sub assy로 저장할 BOM의 productNumber로 찾은 제품이 sub assy가 아니면 예외가 발생한다.")
      void subAssy() {
        Product notSubAssy = Product.builder().id(100L)
            .productNumber(subAssyProductNumber)
            .codeNumber("1").build();

        // when
        when(productRepository.findByProductNumber(subAssyProductNumber))
            .thenReturn(Optional.of(notSubAssy));

        //then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.updateBom(subAssyRequest));
      }

      @Test
      @DisplayName("Product BOM으로 수정 시, productNumber가 존재하면 InvalidProductBomException이 발생한다.")
      void saveProductBomWithProductNumber() {
        //given
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .locationNumber(newLocationNumber)
            .codeNumber(codeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .productNumber(newProductNumber)
            .partId(newPartId)
            .productId(newProductId)
            .build();
        //when
        //then
        assertThrows(InvalidProductBomException.class, () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("Product BOM으로 수정 시, partId가 null이면 InvalidProductBomException이 발생한다.")
      void saveProductBomWithoutPartId() {
        //given
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .productId(newProductId)
            .build();
        //when
        //then
        assertThrows(InvalidProductBomException.class, () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("Sub assy는 sub assy를 BOM으로 가질 수 없다.")
      void subAssyCannotHaveSubAssy() {
        // given
        Product superSubAssy = Product.builder()
            .id(newProductId)
            .codeNumber(subAssyCodeNumber)
            .productNumber("super sub assy")
            .build();
        Product subAssy = Product.builder()
            .id(1000L)
            .codeNumber(subAssyCodeNumber)
            .productNumber(subAssyProductNumber)
            .build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(superSubAssy));
        when(productRepository.findByProductNumber(subAssyProductNumber))
            .thenReturn(Optional.of(subAssy));
        when(
            bomRepository.findByLocationNumberAndProductIdAndPartId(newLocationNumber, newProductId,
                null)).thenReturn(Optional.empty());

        // then
        assertThrows(SubAssyCannotHaveSubAssyException.class,
            () -> bomService.updateBom(subAssyRequest));
      }

      @Test
      @DisplayName("하나의 제품에 null을 제외한 중복된 product number가 있다면 BomProductNumberDuplicatedException이 발생한다.")
      void duplicateProductNumber() {
        // given
        Product SubAssy = Product.builder()
            .id(1000L)
            .codeNumber(subAssyCodeNumber)
            .productNumber(subAssyProductNumber)
            .build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(newProduct));
        when(productRepository.findByProductNumber(subAssyProductNumber))
            .thenReturn(Optional.of(SubAssy));
        when(
            bomRepository.findByLocationNumberAndProductIdAndPartId(newLocationNumber, newProductId,
                null)).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndProductNumber(newProductId, subAssyProductNumber))
            .thenReturn(Optional.of(mock(Bom.class)));

        // then
        assertThrows(BomProductNumberDuplicatedException.class,
            () -> bomService.updateBom(subAssyRequest));
      }
    }
  }
}