package com.pororoz.istock.controller.api;

import com.pororoz.istock.controller.dto.user.response.ResultFindUserReponse;
import com.pororoz.istock.entity.User;
import com.pororoz.istock.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

//    @Test
//    void findUser_test() {
//        // given
//        User user = User.builder().id(1).username("hello").password("1234").build();
//        userRepository.save(user);
//
//        // when
//        ResponseEntity<ResultFindUserReponse> response = userController.findUser("hello");
//
//        // then
//        int id = response.getBody().getData().getId();
//        String username = response.getBody().getData().getUsername();
//        Assertions.assertThat(id).isEqualTo(1);
//        Assertions.assertThat(username).isEqualTo("hello");
//    }
}
