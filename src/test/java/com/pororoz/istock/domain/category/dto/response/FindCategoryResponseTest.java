package com.pororoz.istock.domain.category.dto.response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindCategoryResponseTest {

  @Nested
  @DisplayName("FindCategoryResponse - equals()")
  class EqualsTest {

    @Test
    @DisplayName("서로 다른 인스턴스지만 인스턴스화한 클래스와 그 내용이 같다면 true를 반환한다.")
    void similarObj() {
      // given
      String now = TimeEntity.formatTime(LocalDateTime.now());
      FindCategoryResponse findCategoryResponse1 = FindCategoryResponse.builder().id(1L)
          .categoryName("item").createdAt(now).updatedAt(now).build();
      FindCategoryResponse findCategoryResponse2 = FindCategoryResponse.builder().id(1L)
          .categoryName("item").createdAt(now).updatedAt(now).build();

      // when
      // then
      assertTrue(findCategoryResponse1.equals(findCategoryResponse2));
    }

    @Test
    @DisplayName("아예 같은 Object라면 true를 반환한다.")
    void sameObj() {
      // given
      FindCategoryResponse findCategoryResponse = FindCategoryResponse.builder().id(1L)
          .categoryName("item")
          .createdAt(TimeEntity.formatTime(LocalDateTime.now()))
          .updatedAt(TimeEntity.formatTime(LocalDateTime.now())).build();
      Object obj = findCategoryResponse;

      // when
      // then
      assertTrue(findCategoryResponse.equals(obj));
    }

    @Test
    @DisplayName("Object가 null이면 false를 반환한다.")
    void nullObj() {
      // given
      FindCategoryResponse findCategoryResponse = FindCategoryResponse.builder().id(1L)
          .categoryName("item")
          .createdAt(TimeEntity.formatTime(LocalDateTime.now()))
          .updatedAt(TimeEntity.formatTime(LocalDateTime.now())).build();
      Object obj = null;

      // when
      // then
      assertFalse(findCategoryResponse.equals(obj));
    }

    @Test
    @DisplayName("Object가 내용물이 같아도 클래스가 다르면 false를 반환한다.")
    void notSameClass() {
      // given
      FindCategoryResponse findCategoryResponse = FindCategoryResponse.builder().id(1L)
          .categoryName("item")
          .createdAt(null).updatedAt(null).build();
      FindCategoryServiceResponse findCategoryServiceResponse = FindCategoryServiceResponse.builder()
          .id(1L).name("item").createdAt(null).updatedAt(null).build();

      // when
      // then
      assertFalse(findCategoryResponse.equals(findCategoryServiceResponse));
    }
  }

  @Nested
  @DisplayName("FindCategoryResponse - hashCode()")
  class HashCode {

    @Test
    @DisplayName("다른 인스턴스여도 내용이 같다면 같은 해시코드를 반환한다.")
    void similarObj() {
      // given
      String now = TimeEntity.formatTime(LocalDateTime.now());
      FindCategoryResponse findCategoryResponse1 = FindCategoryResponse.builder().id(1L)
          .categoryName("item").createdAt(now).updatedAt(now).build();
      FindCategoryResponse findCategoryResponse2 = FindCategoryResponse.builder().id(1L)
          .categoryName("item").createdAt(now).updatedAt(now).build();

      // when
      // then
      assertThat(findCategoryResponse1.hashCode(), equalTo(findCategoryResponse2.hashCode()));
    }
  }
}