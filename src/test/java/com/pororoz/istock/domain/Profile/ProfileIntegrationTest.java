package com.pororoz.istock.domain.Profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            profile.andExpect(status().isOk())
                    .andExpect(content().string("spring1"))
                    .andDo(print());
        }

//        @Test
//        @DisplayName("인증 받지 않아도 api에 접근 가능하다.")
//        void security() throws Exception {
//
//        }
    }
}
