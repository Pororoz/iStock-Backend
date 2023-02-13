package com.pororoz.istock.domain.bom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.dto.service.DeleteBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.BomNotFoundException;
import com.pororoz.istock.domain.bom.exception.DuplicateBomException;
import com.pororoz.istock.domain.bom.exception.NotExistedPartException;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
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

  @Nested
  @DisplayName("제품 BOM 행 추가 로직 테스트")
  class SaveBom {

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
      @DisplayName("입력값으로 적절한 값이 들어오면 BOM이 정상적으로 추가된다.")
      void saveBom() {
        // given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
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

        // when
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            anyLong())).thenReturn(Optional.empty());
        when(bomRepository.save(any())).thenReturn(bom);
        BomServiceResponse result = bomService.saveBom(request);

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
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        //when
        when(partRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThrows(NotExistedPartException.class,
            () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("productId에 해당하는 product가 존재하지 않으면 예외가 발생한다.")
      void productNotExist() {
        //given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        //when
        Part part = Part.builder().id(partId).build();
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
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
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
            .product(product)
            .build();

        //when
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            anyLong())).thenReturn(Optional.of(bom));

        //then
        assertThrows(DuplicateBomException.class,
            () -> bomService.saveBom(request));
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

        DeleteBomServiceRequest request =  DeleteBomServiceRequest.builder()
            .bomId(bomId)
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
        BomServiceResponse result = bomService.deleteBom(request);

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
        DeleteBomServiceRequest request =  DeleteBomServiceRequest.builder()
            .bomId(bomId)
            .build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.empty());

        // then
        assertThrows(BomNotFoundException.class,
            () -> bomService.deleteBom(request));
      }
    }
  }

  @Nested
  @DisplayName("제품 BOM 행 수정 로직 테스트")
  class UpdateBom {

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
      @DisplayName("BOM 수정에 성공한다.")
      void updateBom() {
        // given
        String newLocationNumber = "new location";
        String newCodeNumber = "new code";
        Long newQuantity = 5L;
        String newMemo = "new";
        Long newPartId = 3L;
        Long newProductId = 4L;

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

        Part newPart = Part.builder().id(newPartId).build();
        Product newProduct = Product.builder().id(newProductId).build();
        Bom newBom = Bom.builder()
            .id(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .part(newPart)
            .product(newProduct)
            .build();

        UpdateBomServiceRequest request =  UpdateBomServiceRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

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
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            anyLong())).thenReturn(Optional.empty());
        when(bomRepository.save(any())).thenReturn(newBom);
        BomServiceResponse result = bomService.updateBom(request);

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