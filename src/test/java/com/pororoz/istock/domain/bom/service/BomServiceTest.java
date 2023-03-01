package com.pororoz.istock.domain.bom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.BomNotFoundException;
import com.pororoz.istock.domain.bom.exception.BomSubAssyDuplicatedException;
import com.pororoz.istock.domain.bom.exception.DuplicateBomException;
import com.pororoz.istock.domain.bom.exception.InvalidProductBomException;
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
import com.pororoz.istock.domain.bom.exception.SubAssyCannotHaveSubAssyException;
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

  final Long bomId = 1L;
  final String locationNumber = "L5.L4";
  final String codeNumber = "";
  final Long quantity = 3L;
  final String memo = "";
  final Long partId = 1L;
  final Long productId = 2L;
  final Long subAssyId = 100L;
  final String subAssyNumber = "sub assy number";
  final String newLocationNumber = "new location";
  final String newCodeNumber = "new code";
  final Long newQuantity = 5L;
  final String newMemo = "new";
  final Long newPartId = 3L;
  final Long newProductId = 4L;
  final String subAssyCodeNumber = "11";

  @Nested
  @DisplayName("제품 BOM 행 조회 로직 테스트")
  class FindBom {

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
            .productId(productId)
            .build();
        Pageable pageable = PageRequest.of(page, size);

        // when
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(bomRepository.findByProductIdWithPart(any(Pageable.class), eq(productId)))
            .thenReturn(bomPage);
        Page<FindBomServiceResponse> result = bomService.findBomList(request, pageable);

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

      @Test
      @DisplayName("존재하지 않는 Product라면 ProductNotFoundException을 발생시킨다.")
      void productNotFound() {
        // given
        int page = 0;
        int size = 3;

        FindBomServiceRequest request = FindBomServiceRequest.builder()
            .productId(productId)
            .build();
        Pageable pageable = PageRequest.of(page, size);

        // when
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class,
            () -> bomService.findBomList(request, pageable));
      }
    }
  }

  @Nested
  @DisplayName("제품 BOM 행 추가 로직 테스트")
  class SaveBom {

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
        .subAssyId(subAssyId)
        .productId(productId)
        .build();

    Part part = Part.builder().id(partId).build();
    Product product = Product.builder().id(productId).build();
    Product subAssy = Product.builder()
        .id(subAssyId)
        .codeNumber(subAssyCodeNumber)
        .productNumber(subAssyNumber)
        .build();

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
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(locationNumber
            , productId, null, partId)).thenReturn(Optional.empty());
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
            .productId(productId)
            .subAssyId(subAssyId)
            .build();

        Bom bom = Bom.builder()
            .id(bomId)
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .subAssy(subAssy)
            .product(product)
            .build();

        // when
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(subAssy));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(locationNumber,
            productId, subAssyId, null)).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndSubAssyId(productId, subAssyId))
            .thenReturn(Optional.empty());
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
      @DisplayName("location_number, product_id, sub_assy_id, part_id이 이미 존재하는 조합이면 예외가 발생한다.")
      void duplicateBom() {
        //given
        //when
        when(partRepository.findById(any())).thenReturn(Optional.of(part));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(locationNumber,
            productId, null, partId)).thenReturn(Optional.of(bom));

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
            .subAssyId(subAssyId)
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
      @DisplayName("Sub assy로 저장할 BOM의 subAssyId로 찾은 제품이 sub assy가 아니면 예외가 발생한다.")
      void subAssy() {
        //given
        Product notSubAssy = Product.builder().id(productId).codeNumber("1").build();

        //when
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(notSubAssy));

        //then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.saveBom(subAssyRequest));
      }

      @Test
      @DisplayName("Product BOM 저장 시, productId가 존재하면 InvalidProductBomException이 발생한다.")
      void saveProductBomWithProductNumber() {
        //given
        SaveBomServiceRequest request = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .productId(productId)
            .subAssyId(subAssyId)
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
      @DisplayName("하나의 제품에 null을 제외한 중복된 sub assy id가 있다면 BomProductNumberDuplicatedException이 발생한다.")
      void duplicateProductNumber() {
        //given
        // when
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(subAssy));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(locationNumber,
            productId, subAssyId, null)).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndSubAssyId(productId, subAssyId)).thenReturn(
            Optional.of(mock(Bom.class)));

        //then
        assertThrows(BomSubAssyDuplicatedException.class,
            () -> bomService.saveBom(subAssyRequest));
      }

      @Test
      @DisplayName("Sub assy는 sub assy를 BOM으로 저장할 수 없다.")
      void subAssyCannotHaveSubAssy() {
        Product superSubAssy = Product.builder()
            .id(productId)
            .codeNumber(subAssyCodeNumber)
            .productNumber("super sub assy").build();

        // when
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(subAssy));
        when(productRepository.findById(productId)).thenReturn(Optional.of(superSubAssy));
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(locationNumber,
            productId, subAssyId, null)).thenReturn(Optional.empty());

        // then
        assertThrows(SubAssyCannotHaveSubAssyException.class,
            () -> bomService.saveBom(subAssyRequest));
      }
    }
  }

  @Nested
  @DisplayName("제품 BOM 행 삭제 로직 테스트")
  class DeleteBom {

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

    Part part = Part.builder().id(partId).build();
    Product product = Product.builder().id(productId).build();
    Product subAssy = Product.builder()
        .id(subAssyId).codeNumber(subAssyCodeNumber)
        .productNumber(subAssyNumber)
        .build();
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
        .subAssyId(subAssyId)
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
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(newLocationNumber,
            newProductId, null, newPartId)).thenReturn(Optional.empty());
        BomServiceResponse result = bomService.updateBom(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("Sub assy로 BOM 수정에 성공한다.")
      void updateToSubAssyBom() {
        // given
        BomServiceResponse response = BomServiceResponse.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .subAssyId(subAssyId)
            .codeNumber(subAssyCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .productId(newProductId)
            .build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(productRepository.findById(anyLong())).thenAnswer(invocation -> {
          Long id = invocation.getArgument(0);
          if (id.equals(subAssyId)) {
            return Optional.of(subAssy);
          } else if (id.equals(newProductId)) {
            return Optional.of(newProduct);
          } else {
            return Optional.empty();
          }
        });
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(newLocationNumber,
            newProductId, subAssyId, null)).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndSubAssyId(newProductId, subAssyId))
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
        when(partRepository.findById(newPartId)).thenReturn(Optional.of(newPart));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(newProduct));
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(newLocationNumber,
            newProductId, null, newPartId)).thenReturn(Optional.of(ExistedBom));

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
            .subAssyId(subAssyId)
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
      @DisplayName("Sub assy로 수정할 BOM의 subAssyId로 찾은 제품이 sub assy가 아니면 예외가 발생한다.")
      void subAssy() {
        Product notSubAssy = Product.builder().id(subAssyId)
            .productNumber("not sub assy")
            .codeNumber("1").build();

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(mock(Bom.class)));
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(notSubAssy));

        //then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.updateBom(subAssyRequest));
      }

      @Test
      @DisplayName("Product BOM으로 수정 시, subAssyId 존재하면 InvalidProductBomException이 발생한다.")
      void saveProductBomWithProductNumber() {
        //given
        UpdateBomServiceRequest request = UpdateBomServiceRequest.builder()
            .locationNumber(newLocationNumber)
            .codeNumber(codeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .subAssyId(subAssyId)
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

        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(superSubAssy));
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(subAssy));
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(newLocationNumber,
            newProductId, subAssyId, null)).thenReturn(Optional.empty());

        // then
        assertThrows(SubAssyCannotHaveSubAssyException.class,
            () -> bomService.updateBom(subAssyRequest));
      }

      @Test
      @DisplayName("하나의 제품에 null을 제외한 중복된 subAssyId가 있다면 BomProductNumberDuplicatedException이 발생한다.")
      void duplicateProductNumber() {
        // given
        // when
        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(newProduct));
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(subAssy));
        when(bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(newLocationNumber,
            newProductId, subAssyId, null)).thenReturn(Optional.empty());
        when(bomRepository.findByProductIdAndSubAssyId(newProductId, subAssyId)).thenReturn(
            Optional.of(mock(Bom.class)));

        // then
        assertThrows(BomSubAssyDuplicatedException.class,
            () -> bomService.updateBom(subAssyRequest));
      }
    }
  }
}