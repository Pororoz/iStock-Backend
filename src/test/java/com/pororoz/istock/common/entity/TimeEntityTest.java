package com.pororoz.istock.common.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeEntityTest {

  @Test
  @DisplayName("null은 null을 반환한다.")
  void formatTime() {
    //given
    LocalDateTime time = null;

    //when
    String result = TimeEntity.formatTime(time);

    //then
    assertThat(result, equalTo(time));
  }
}