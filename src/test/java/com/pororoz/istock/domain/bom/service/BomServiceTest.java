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
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
import com.pororoz.istock.domain.bom.exception.SubAssyCannotHaveSubAssyException;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.SubAssyNotFoundException;
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
  @DisplayName("?????? BOM ??? ?????? ?????? ?????????")
  class FindBom {

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("????????? ????????? ?????? ??????????????? Bom List??? ???????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("???????????? ?????? Product?????? ProductNotFoundException??? ???????????????.")
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
  @DisplayName("?????? BOM ??? ?????? ?????? ?????????")
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
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("??????????????? ????????? ?????? ???????????? BOM??? ??????????????? ????????????.")
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
      @DisplayName("sub assy??? BOM??? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("partId??? ???????????? part??? ???????????? ????????? ????????? ????????????.")
      void partNotExist() {
        //given
        //when
        when(partRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThrows(PartNotFoundException.class,
            () -> bomService.saveBom(request));
      }

      @Test
      @DisplayName("productId??? ???????????? product??? ???????????? ????????? ????????? ????????????.")
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
      @DisplayName("location_number, product_id, sub_assy_id, part_id??? ?????? ???????????? ???????????? ????????? ????????????.")
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
      @DisplayName("Sub assy??? ????????? BOM??? subAssyId??? ?????? ????????? sub assy??? ????????? ????????? ????????????.")
      void subAssy() {
        //given
        Product notSubAssy = Product.builder().id(productId).codeNumber("1").build();

        //when
        when(productRepository.findById(subAssyId)).thenReturn(Optional.of(notSubAssy));

        //then
        assertThrows(InvalidSubAssyBomException.class, () -> bomService.saveBom(subAssyRequest));
      }

      @Test
      @DisplayName("????????? ????????? null??? ????????? ????????? sub assy id??? ????????? BomProductNumberDuplicatedException??? ????????????.")
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
      @DisplayName("Sub assy??? sub assy??? BOM?????? ????????? ??? ??????.")
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

      @Test
      @DisplayName("???????????? ?????? sub assy id??? ???????????? SubAssyNotFound??? ????????????.")
      void subAssyNotFound() {
        SaveBomServiceRequest subAssyRequest = SaveBomServiceRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .subAssyId(10000L)
            .productId(productId)
            .build();

        // when
        when(productRepository.findById(10000L)).thenReturn(Optional.empty());

        // then
        assertThrows(SubAssyNotFoundException.class, () -> bomService.saveBom(subAssyRequest));
      }
    }
  }

  @Nested
  @DisplayName("?????? BOM ??? ?????? ?????? ?????????")
  class DeleteBom {

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("???????????? BOM ID??? ???????????? ?????? BOM??? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("?????? ???????????? ?????? BOM??? ??????????????? ?????? BomNotFoundException??? ????????????.")
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
  @DisplayName("?????? BOM ??? ?????? ?????? ?????????")
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
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("BOM ????????? ????????????.")
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
      @DisplayName("Sub assy??? BOM ????????? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("BOM id??? ???????????? BOM??? ???????????? ????????? ????????? ????????????.")
      void bomNotFound() {
        //given
        //when
        when(bomRepository.findById(bomId)).thenReturn(Optional.empty());

        //then
        assertThrows(BomNotFoundException.class,
            () -> bomService.updateBom(request));
      }

      @Test
      @DisplayName("partId??? ???????????? part??? ???????????? ????????? ????????? ????????????.")
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
      @DisplayName("productId??? ???????????? product??? ???????????? ????????? ????????? ????????????.")
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
      @DisplayName("location_number, product_id, part_id??? ?????? ???????????? ???????????? ????????? ????????????.")
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

      @Test
      @DisplayName("Sub assy??? ????????? BOM??? subAssyId??? ?????? ????????? sub assy??? ????????? ????????? ????????????.")
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
      @DisplayName("Sub assy??? sub assy??? BOM?????? ?????? ??? ??????.")
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
      @DisplayName("????????? ????????? null??? ????????? ????????? subAssyId??? ????????? BomProductNumberDuplicatedException??? ????????????.")
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