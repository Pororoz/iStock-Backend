package com.pororoz.istock.domain.bom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.DeleteBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.BomNotFoundException;
import com.pororoz.istock.domain.bom.exception.DuplicateBomException;
import com.pororoz.istock.domain.bom.exception.InvalidProductBomException;
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
  @DisplayName("제품 BOM 행 조회 로직 테스트")
  class FindBom {

    Long bomId;
    String locationNumber;
    String codeNumber;
    Long quantity;
    String memo;
    String productNumber;
    Long partId;
    Long productId;

    @BeforeEach
    void setup() {
      bomId = 1L;
      locationNumber = "L5.L4";
      codeNumber = "";
      quantity = 3L;
      memo = "";
      productNumber = "1";
      partId = 1L;
      productId = 2L;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("요청을 보내면 해당 프로덕트의 Bom List를 반환해준다.")
      void findBom() {
        // given
        int page = 0;
        int size = 3;
        long total = 10;

        Category category = Category.builder()
            .id(1L)
            .categoryName("category")
            .build();
        Product product = Product.builder()
            .id(1L)
            .codeNumber("1")
            .productName("product")
            .category(category)
            .stock(3L)
            .companyName("pororoz")
            .build();

        List<Bom> bomList = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
          Part part = Part.builder()
              .id(i + 1)
              .spec("HBA3580PL" + i)
              .stock(i + 1)
              .price((i + 1) * 150)
              .build();

          bomList.add(Bom.builder()
              .id(i + 1)
              .locationNumber("L5.L" + i)
              .codeNumber(Long.toString(i))
              .quantity(quantity)
              .part(part)
              .product(product)
              .memo(memo)
              .build());
        }
        product.setBoms(bomList);
        PageImpl<Bom> bomPage = new PageImpl<>(bomList, PageRequest.of(page, size), total);

        FindBomServiceRequest request = FindBomServiceRequest.builder()
            .page(page).size(size)
            .productId(productId)
            .build();

        // when
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(bomRepository.findByProductIdWithPart(any(Pageable.class), eq(productId)))
            .thenReturn(bomPage);
        Page<FindBomServiceResponse> result = bomService.findBomList(request);

        //then
        assertThat(result.getTotalPages()).isEqualTo((total + size) / size);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.isFirst()).isEqualTo(true);
        assertThat(result.isLast()).isEqualTo(false);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }

  @Nested
  @DisplayName("제품 BOM 행 추가 로직 테스트")
  class SaveBom {

    Long bomId;
    String locationNumber;
    String codeNumber;
    Long quantity;
    String memo;
    String productNumber;
    Long partId;
    Long productId;

    @BeforeEach
    void setup() {
      bomId = 1L;
      locationNumber = "L5.L4";
      codeNumber = "";
      quantity = 3L;
      memo = "";
      productNumber = "1";
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

      @Test
      @DisplayName("sub assy인 BOM을 저장한다.")
      void saveSubAssyBom() {
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .productNumber(productNumber)
            .productId(productId)
            .build();
        BomServiceResponse response = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .productNumber(productNumber)
            .productId(productId)
            .build();

        Product product = Product.builder().id(productId).build();
        Product superProduct = Product.builder().id(productId).build();
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
            Optional.of(superProduct));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndPartId(anyString(), anyLong(),
            eq(null))).thenReturn(Optional.empty());
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
        assertThrows(PartNotFoundException.class,
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
      @DisplayName("Sub assy로 저장할 BOM의 상위 제품도 sub assy이면 예외가 발생한다.")
      void subAssy() {
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .productNumber(productNumber)
            .productId(productId)
            .build();

        Product superProduct = Product.builder().id(productId).codeNumber("11").build();

        // when
        when(productRepository.findByProductNumber(productNumber))
            .thenReturn(Optional.of(superProduct));

        //then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("Product BOM 저장 시, productNumber가 존재하면 InvalidProductBomException이 발생한다.")
      void saveProductBomWithProductNumber() {
        //given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .productNumber(productNumber)
            .partId(partId)
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

        DeleteBomServiceRequest request = DeleteBomServiceRequest.builder()
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
        DeleteBomServiceRequest request = DeleteBomServiceRequest.builder()
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
    String productNumber;
    String newLocationNumber;
    String newCodeNumber;
    Long newQuantity;
    String newMemo;
    Long newPartId;
    Long newProductId;
    String newProductNumber;

    @BeforeEach
    void setup() {
      bomId = 1L;
      locationNumber = "L5.L4";
      codeNumber = "";
      quantity = 3L;
      memo = "";
      partId = 1L;
      productId = 2L;
      productNumber = "number";
      newLocationNumber = "new location";
      newCodeNumber = "new code";
      newQuantity = 5L;
      newMemo = "new";
      newPartId = 3L;
      newProductId = 4L;
      newProductNumber = "new number";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("BOM 수정에 성공한다.")
      void updateBom() {
        // given
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
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("BOM id에 해당하는 BOM이 존재하지 않으면 예외가 발생한다.")
      void bomNotFound() {
        //given
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

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
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
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
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
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
        Part newPart = Part.builder().id(newPartId).build();

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
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
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
        Part newPart = Part.builder().id(newPartId).build();
        Product newProduct = Product.builder().id(newProductId).build();
        Bom ExistedBom = Bom.builder()
            .id(bomId)
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
      @DisplayName("Sub assy로 저장할 BOM의 상위 제품도 sub assy이면 예외가 발생한다.")
      void subAssy() {
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .locationNumber(newLocationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .productNumber(newProductNumber)
            .productId(newProductId)
            .build();

        Product superProduct = Product.builder().id(100L).codeNumber("11").build();

        // when
        when(productRepository.findByProductNumber(newProductNumber))
            .thenReturn(Optional.of(superProduct));

        //then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.updateBom(request));
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
    }
  }
}