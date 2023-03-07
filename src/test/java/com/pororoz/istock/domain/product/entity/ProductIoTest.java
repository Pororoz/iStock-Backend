package com.pororoz.istock.domain.product.entity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.production.exception.ChangeProductionStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductIoTest {

  @Nested
  class CreateProductIo {

    @Test
    @DisplayName("subAssyIo를 생성한다.")
    void createProductIo() {
      //given
      Bom mockBom = mock(Bom.class);
      Product mockSubAssy = mock(Product.class);
      ProductIo mockProductIo = mock(ProductIo.class);

      //when
      when(mockBom.getSubAssy()).thenReturn(mockSubAssy);

      //then
      ProductIo.createSubAssyIo(mockBom, mockProductIo, 1L, ProductStatus.사내출고대기);
    }

    @Test
    @DisplayName("bom의 subAssy가 null이면 IllegalArgumentException이 발생한다.")
    void getPartNull() {
      //given
      Bom mockBom = mock(Bom.class);
      ProductIo mockProductIo = mock(ProductIo.class);
      Product product = Product.builder().id(1L).build();

      //when
      when(mockBom.getSubAssy()).thenReturn(null);
      when(mockBom.getProduct()).thenReturn(product);
      when(mockBom.getId()).thenReturn(2L);
      when(mockBom.getLocationNumber()).thenReturn("location");

      //then
      assertThrows(IllegalArgumentException.class,
          () -> PartIo.createPartIo(mockBom, mockProductIo, 1L, PartStatus.생산대기));
    }
  }

  @Nested
  @DisplayName("생산 완료")
  class confirmProduction {

    @Test
    @DisplayName("생산 취소 상태는 생산 완료로 변경할 수 없다.")
    void cannotChangeStatusCancel() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.생산취소).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::confirmProduction);
    }

    @Test
    @DisplayName("생산 완료 상태는 생산 완료로 변경할 수 없다.")
    void cannotChangeStatusConfirm() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.생산완료).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::confirmProduction);
    }

    @Test
    @DisplayName("사내 출고 취소 상태는 사내 출고 완료로 변경할 수 없다.")
    void cannotChangeSubAssyStatusCancel() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.사내출고취소).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::confirmSubAssyProduction);
    }

    @Test
    @DisplayName("사내 출고 완료 상태는 사내 출고 완료로 변경할 수 없다.")
    void cannotChangeSubAssyStatusConfirm() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.사내출고완료).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::confirmProduction);
    }
  }

  @Nested
  @DisplayName("생산 취소")
  class cancelProduction {

    @Test
    @DisplayName("생산 취소 상태는 생산 취소로 변경할 수 없다.")
    void cannotChangeStatusCancel() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.생산취소).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::cancelProduction);
    }

    @Test
    @DisplayName("생산 완료 상태는 생산 취소로 변경할 수 없다.")
    void cannotChangeStatusConfirm() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.생산완료).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::cancelProduction);
    }

    @Test
    @DisplayName("사내 출고 취소 상태는 사내 출고 취소로 변경할 수 없다.")
    void cannotChangeSubAssyStatusCancel() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.사내출고취소).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::cancelSubAssyProduction);
    }

    @Test
    @DisplayName("사내 출고 완료 상태는 사내 출고 취소로 변경할 수 없다.")
    void cannotChangeSubAssyStatusConfirm() {
      //given
      ProductIo productIo = ProductIo.builder().id(1L).status(ProductStatus.사내출고완료).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, productIo::cancelSubAssyProduction);
    }
  }
}