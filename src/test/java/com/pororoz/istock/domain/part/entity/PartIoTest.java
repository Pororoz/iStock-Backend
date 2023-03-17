package com.pororoz.istock.domain.part.entity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.production.exception.ChangeProductionStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PartIoTest {

  @Nested
  class CreatePartIo {

    @Test
    @DisplayName("partIo를 생성한다.")
    void createPartIo() {
      //given
      Bom mockBom = mock(Bom.class);
      Part mockPart = mock(Part.class);
      ProductIo mockProductIo = mock(ProductIo.class);

      //when
      when(mockBom.getPart()).thenReturn(mockPart);

      //then
      PartIo.createPartIo(mockBom, mockProductIo, 1L, PartStatus.생산대기);
    }

    @Test
    @DisplayName("bom의 part가 null이면 IllegalArgumentException이 발생한다.")
    void getPartNull() {
      //given
      Bom mockBom = mock(Bom.class);
      ProductIo mockProductIo = mock(ProductIo.class);
      Product product = Product.builder().id(1L).build();

      //when
      when(mockBom.getPart()).thenReturn(null);
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
      PartIo partIo = PartIo.builder().id(1L).status(PartStatus.생산취소).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, partIo::confirmPartProduction);
    }

    @Test
    @DisplayName("생산 완료 상태는 생산 완료로 변경할 수 없다.")
    void cannotChangeStatusConfirm() {
      //given
      PartIo partIo = PartIo.builder().id(1L).status(PartStatus.생산완료).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, partIo::confirmPartProduction);
    }
  }

  @Nested
  @DisplayName("생산 취소")
  class cancelProduction {

    @Test
    @DisplayName("생산 취소 상태는 생산 취소로 변경할 수 없다.")
    void cannotChangeStatusCancel() {
      //given
      PartIo partIo = PartIo.builder().id(1L).status(PartStatus.생산취소).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, partIo::cancelPartProduction);
    }

    @Test
    @DisplayName("생산 완료 상태는 생산 취소로 변경할 수 없다.")
    void cannotChangeStatusConfirm() {
      //given
      PartIo partIo = PartIo.builder().id(1L).status(PartStatus.생산완료).build();

      //when
      //then
      assertThrows(ChangeProductionStatusException.class, partIo::cancelPartProduction);
    }
  }
}