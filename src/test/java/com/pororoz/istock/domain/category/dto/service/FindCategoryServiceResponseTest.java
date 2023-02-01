package com.pororoz.istock.domain.category.dto.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pororoz.istock.domain.category.dto.response.FindCategoryResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindCategoryServiceResponseTest {

  @Nested
  @DisplayName("FindCategoryServiceResponse - equals()")
  class EqualsTest {

    @Test
    @DisplayName("서로 다른 인스턴스지만 인스턴스화한 클래스와 그 내용이 같다면 true를 반환한다.")
    void similarObj() {
      // given
      LocalDateTime now = LocalDateTime.now();
      FindCategoryServiceResponse findCategoryServiceResponse1 = FindCategoryServiceResponse.builder()
          .id(1L)
          .name("item")
          .createdAt(now)
          .updatedAt(now)
          .build();
      FindCategoryServiceResponse findCategoryServiceResponse2 = FindCategoryServiceResponse.builder()
          .id(1L)
          .name("item")
          .createdAt(now)
          .updatedAt(now)
          .build();

      // when
      // then
      assertTrue(findCategoryServiceResponse1.equals(findCategoryServiceResponse2));
    }

    @Test
    @DisplayName("아예 같은 Object라면 true를 반환한다.")
    void sameObj() {
      // given
      LocalDateTime now = LocalDateTime.now();
      FindCategoryServiceResponse findCategoryServiceResponse = FindCategoryServiceResponse.builder()
          .id(1L)
          .name("item")
          .createdAt(now)
          .updatedAt(now)
          .build();
      Object obj = findCategoryServiceResponse;

      // when
      // then
      assertTrue(findCategoryServiceResponse.equals(obj));
    }

    @Test
    @DisplayName("Object가 null이면 false를 반환한다.")
    void nullObj() {
      // given
      LocalDateTime now = LocalDateTime.now();
      FindCategoryServiceResponse findCategoryServiceResponse = FindCategoryServiceResponse.builder()
          .id(1L)
          .name("item")
          .createdAt(now)
          .updatedAt(now)
          .build();
      Object obj = null;

      // when
      // then
      assertFalse(findCategoryServiceResponse.equals(obj));
    }

    @Test
    @DisplayName("Object가 내용물이 같아도 클래스가 다르면 false를 반환한다.")
    void notSameClass() {
      // given
      FindCategoryResponse findCategoryResponse = FindCategoryResponse.builder()
          .id(1L)
          .categoryName("item")
          .createdAt(null)
          .updatedAt(null)
          .build();
      FindCategoryServiceResponse findCategoryServiceResponse = FindCategoryServiceResponse.builder()
          .id(1L)
          .name("item")
          .createdAt(null)
          .updatedAt(null)
          .build();

      // when
      // then
      assertFalse(findCategoryServiceResponse.equals(findCategoryResponse));
    }
  }

  @Nested
  @DisplayName("FindCategoryResponse - hashCode()")
  class HashCode {

    @Test
    @DisplayName("다른 인스턴스여도 내용이 같다면 같은 해시코드를 반환한다.")
    void similarObj() {
      // given
      LocalDateTime now = LocalDateTime.now();
      FindCategoryServiceResponse findCategoryServiceResponse1 = FindCategoryServiceResponse.builder()
          .id(1L)
          .name("item")
          .createdAt(now)
          .updatedAt(now)
          .build();
      FindCategoryServiceResponse findCategoryServiceResponse2 = FindCategoryServiceResponse.builder()
          .id(1L)
          .name("item")
          .createdAt(now)
          .updatedAt(now)
          .build();

      // when
      // then
      assertThat(findCategoryServiceResponse1.hashCode(),
          equalTo(findCategoryServiceResponse2.hashCode()));
    }
  }
}