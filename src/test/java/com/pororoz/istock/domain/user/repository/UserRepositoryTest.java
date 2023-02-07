package com.pororoz.istock.domain.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

  @Autowired
  TestEntityManager em;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  UserRepository userRepository;


  @Nested
  @DisplayName("Commit time 확인")
  class TestEntitySaveTime {

    User user;

    @BeforeEach
    void setUp() {
      Role role = roleRepository.findByRoleName("ROLE_USER").orElseThrow();
      user = User.builder().username("user1").password("12345678").role(role).build();
    }

    @Test
    @DisplayName("createdAt에 entity가 저장된 시간이 저장된다.")
    void createdAt() {
      //given
      //when
      User save = userRepository.save(user);

      //then
      assertEquals(save.getCreatedAt(), save.getUpdatedAt());
      assertEquals(save.getUpdatedAt().getYear(), LocalDateTime.now().getYear());
      assertEquals(save.getUpdatedAt().getMonth(), LocalDateTime.now().getMonth());
      assertEquals(save.getUpdatedAt().getDayOfMonth(), LocalDateTime.now().getDayOfMonth());
      assertEquals(save.getUpdatedAt().getHour(), LocalDateTime.now().getHour());
      assertEquals(save.getUpdatedAt().getMinute(), LocalDateTime.now().getMinute());
    }

    @Test
    @DisplayName("updateAt에 entity가 변경된 시간이 저장된다.")
    void updatedAt() {
      //given
      User save = userRepository.save(user);

      //when
      save.update("aaaaaaa", save.getRole());
      em.flush();
      em.clear();

      //then
      assertNotEquals(save.getCreatedAt(), save.getUpdatedAt());
      assertTrue(save.getCreatedAt().isBefore(save.getUpdatedAt()));
      assertEquals(save.getUpdatedAt().getYear(), LocalDateTime.now().getYear());
      assertEquals(save.getUpdatedAt().getMonth(), LocalDateTime.now().getMonth());
      assertEquals(save.getUpdatedAt().getDayOfMonth(), LocalDateTime.now().getDayOfMonth());
      assertEquals(save.getUpdatedAt().getHour(), LocalDateTime.now().getHour());
      assertEquals(save.getUpdatedAt().getMinute(), LocalDateTime.now().getMinute());
    }
  }

}