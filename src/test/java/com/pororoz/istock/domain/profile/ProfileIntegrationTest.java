package com.pororoz.istock.domain.profile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Nested
  class Profile {

    @Test
    @DisplayName("spring1 profile이 조회된다.")
    void getProfileSpring1() throws Exception {
      //when
      ResultActions profile = mockMvc.perform(get("/profile"));

      //then
      profile.andExpect(status().isOk()).andExpect(content().string("spring1")).andDo(print());
    }
  }
}
