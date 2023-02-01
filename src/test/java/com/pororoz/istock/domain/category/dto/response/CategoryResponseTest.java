package com.pororoz.istock.domain.category.dto.response;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CategoryResponseTest {

  @Nested
  @DisplayName("equals() test")
  class Equals {

    @Test
    @DisplayName("인스턴스가 같을 때 true 반환")
    void instanceIsSame() {
      // given
      CategoryResponse response = CategoryResponse.builder().build();
      Object obj = response;

      // when
      // then
      assertTrue(response.equals(obj));
    }

    @Test
    @DisplayName("null일 때 false 반환")
    void ObjectIsNull() {
      // given
      CategoryResponse response = CategoryResponse.builder().build();
      Object obj = null;

      // when
      // then
      assertFalse(response.equals(obj));
    }

    @Test
    @DisplayName("인스턴스가 다르지만 값이 같으면 true 리턴")
    void sameContents() {
      // given
      Long id = 1L;
      String item = "item";
      CategoryResponse response1 = CategoryResponse.builder().id(id).categoryName(item).build();
      CategoryResponse response2 = CategoryResponse.builder().id(id).categoryName(item).build();

      // when
      // then
      assertTrue(response1.equals(response2));
    }
  }
}