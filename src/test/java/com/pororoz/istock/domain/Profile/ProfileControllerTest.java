package com.pororoz.istock.domain.Profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {
    @InjectMocks
    ProfileController profileController;

    @Mock
    Environment env;

    @Nested
    class Profile {
        @Test
        @DisplayName("spring1 profile이 조회된다.")
        void getProfileSpring1(){
            //given
            String expectedProfile = "spring1";

            //when
            when(env.getActiveProfiles()).thenReturn(new String[]{expectedProfile, "build"});

            //then
            String profile = profileController.profile();
            assertThat(profile, equalTo(expectedProfile));
        }

        @Test
        @DisplayName("spring2 profile이 조회된다.")
        void getProfileSpring2(){
            //given
            String expectedProfile = "spring2";

            //when
            when(env.getActiveProfiles()).thenReturn(new String[]{expectedProfile, "build"});

            //then
            String profile = profileController.profile();
            assertThat(profile, equalTo(expectedProfile));
        }

        @Test
        @DisplayName("spring profile이 env에 없으면 spring1이 조회된다.")
        void getProfileSpring1EmptyEnv(){

            //when
            when(env.getActiveProfiles()).thenReturn(new String[]{"build"});

            //then
            String profile = profileController.profile();
            assertThat(profile, equalTo("spring1"));
        }
    }
}