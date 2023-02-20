package com.pororoz.istock.domain.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRepositoryTest extends RepositoryTest {

  @Autowired
  RoleRepository roleRepository;
  @Autowired
  UserRepository userRepository;


  @Nested
  @DisplayName("Commit time 확인")
  class TestEntitySaveTime {

    User user;
    Role role;

    @BeforeEach
    void setUp() {
      role = roleRepository.findByRoleName("ROLE_USER").orElseThrow();
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

    @Test
    @DisplayName("영속성 컨텍스트에 저장된 시간과 DB에 저장된 시간이 같다")
    void persistentContextTimeSame() {
      User save = em.persist(User.builder().role(role).username("nnn").password("ppp").build());
      em.flush();
      em.clear();
      User find = userRepository.findById(save.getId()).orElseThrow();
      assertEquals(save.getCreatedAt(), find.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("유저 저장")
  class SaveUser {

    Role role;

    @BeforeEach
    void setUp() {
      role = roleRepository.findByRoleName("ROLE_USER").orElseThrow();
    }

    @Test
    @DisplayName("username의 대소문자를 구분하여 저장한다.")
    void saveUser() {
      em.persist(User.builder().username("aa").password("1234").role(role).build());
      em.persist(User.builder().username("Aa").password("1234").role(role).build());
      em.flush();
      em.clear();
      User aa = (User) em.getEntityManager()
          .createQuery("select u from User u where username = 'aa'").getSingleResult();
      User Aa = (User) em.getEntityManager()
          .createQuery("select u from User u where username = 'Aa'").getSingleResult();
      assertEquals(aa.getUsername(), "aa");
      assertEquals(Aa.getUsername(), "Aa");
    }
  }
}