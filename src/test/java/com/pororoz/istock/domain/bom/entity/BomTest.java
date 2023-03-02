package com.pororoz.istock.domain.bom.entity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.pororoz.istock.domain.bom.exception.InvalidProductBomException;
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BomTest {

  @Nested
  class Constructor {

    @Test
    @DisplayName("part를 가지고 subAssy가 null인 product bom을 생성한다.")
    void createProductBom() {
      //given
      Part mockPart = mock(Part.class);

      //when
      //then
      Bom.builder()
          .codeNumber("0")
          .part(mockPart)
          .build();
    }

    @Test
    @DisplayName("part가 null이고 subAssy를 가진 subAssy bom을 생성한다.")
    void createSubAssyBom() {
      //given
      Product mockSubAssy = mock(Product.class);

      //when
      //then
      Bom.builder()
          .codeNumber("11")
          .subAssy(mockSubAssy)
          .build();
    }

    @Test
    @DisplayName("subAssy bom은 part가 null이면 InvalidProductBomException이 발생한다.")
    void invalidSubAssyBomPartNull() {
      //given
      //when
      //then
      assertThrows(InvalidProductBomException.class, () -> Bom.builder()
          .codeNumber("0").build());
    }

    @Test
    @DisplayName("product bom은 subAssy가 존재하면 InvalidProductBomException이 발생한다.")
    void invalidProductBomSubAssyExist() {
      //given
      Part mockPart = mock(Part.class);
      Product mockSubAssy = mock(Product.class);

      //when
      //then
      assertThrows(InvalidProductBomException.class, () -> Bom.builder()
          .codeNumber("0")
          .subAssy(mockSubAssy)
          .part(mockPart).build());
    }

    @Test
    @DisplayName("subAssy bom은 part가 null이 아니면 InvalidSubAssyBomException이 발생한다.")
    void invalidSubAssyBomPartExist() {
      //given
      Product mockSubAssy = mock(Product.class);
      Part mockPart = mock(Part.class);

      //when
      //then
      assertThrows(InvalidSubAssyBomException.class, () -> Bom.builder()
          .codeNumber("11")
          .part(mockPart)
          .subAssy(mockSubAssy).build());
    }

    @Test
    @DisplayName("subAssy bom은 subAssy가 null이면 InvalidSubAssyBomException이 발생한다.")
    void invalidSubAssyBomSubAssyNull() {
      //given
      //when
      //then
      assertThrows(InvalidSubAssyBomException.class, () -> Bom.builder()
          .codeNumber("11").build());
    }
  }
}