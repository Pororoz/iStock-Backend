package com.pororoz.istock.domain.user.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.exception.UserNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  UserService userService;

  @Mock
  UserRepository userRepository;

  @Mock
  RoleRepository roleRepository;

  @Nested
  @DisplayName("계정 삭제 API")
  class DeleteUser {

    private Long id;
    private String username;
    private String password;
    private String roleName;
    private Role role;

    @BeforeEach
    void setup() {
      id = 1L;
      username = "test";
      password = "1234";
      roleName = "admin";
      role = Role.builder().name("admin").build();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 유저를 삭제한다.")
      void deleteUser() {
        // given
        DeleteUserServiceRequest deleteUserServiceRequest = DeleteUserServiceRequest.builder()
            .id(id).build();

        User resultUser = User.builder().id(id).username(username).password(password).role(role)
            .build();

        UserServiceResponse userServiceResponse = UserServiceResponse.builder().id(id)
            .roleName(roleName).username(username).build();
        UserResponse response = userServiceResponse.toResponse();

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(resultUser));
        UserResponse result = userService.deleteUser(deleteUserServiceRequest);

        // then
        assertEquals(result, response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("없는 ID로 요청했을 때 UserNotFoundException을 반환한다.")
      void notExistedId() {
        // given
        DeleteUserServiceRequest deleteUserServiceRequest = DeleteUserServiceRequest.builder()
            .id(10000L).build();

        // when

        // then
        assertThrows(UserNotFoundException.class,
            () -> userService.deleteUser(deleteUserServiceRequest));
      }
    }
  }

  @Nested
  @DisplayName("유저 생성 API Test")
  class SaveUser {

    private Long id;
    private String username;
    private String password;
    private String roleName;
    private Role role;

    @BeforeEach
    void setup() {
      id = 1L;
      username = "test";
      password = "1234";
      roleName = "admin";
      role = Role.builder().name("admin").build();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("유저를 생성한다.")
      void saveUser() {
        // given
        SaveUserServiceRequest saveUserServiceRequest = SaveUserServiceRequest.builder()
            .username(username).password(password).roleName(roleName).build();

        User resultUser = User.builder().id(id).username(username).password(password).role(role)
            .build();

        UserServiceResponse userServiceResponse = UserServiceResponse.builder().id(id)
            .roleName(roleName).username(username).build();

        UserResponse response = userServiceResponse.toResponse();

        // when
        when(userRepository.save(any())).thenReturn(resultUser);
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        UserResponse result = userService.saveUser(saveUserServiceRequest);

        // then
        assertEquals(result, response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("잘못된 role name은 RoleNotFoundExceptin이 발생한다.")
      void notFoundUser() {
        String invalidRole = "a";

        //given
        SaveUserServiceRequest saveUserServiceRequest = SaveUserServiceRequest.builder()
            .username(username).password(password).roleName(invalidRole).build();

        //then
        assertThrows(RoleNotFoundException.class,
            () -> userService.saveUser(saveUserServiceRequest));
      }
    }
  }

  @Nested
  @DisplayName("유저 조회 API Test")
  class FindUser {

    Role userRole = Role.builder().name("user").build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      User user1 = User.builder().id(1L).username("user1").password("1q2w3e4r").role(userRole)
          .build();
      User user2 = User.builder().id(2L).username("user2").password("1q2w3e4r").role(userRole)
          .build();

      @Test
      @DisplayName("두번째 계정 페이지를 조회한다.")
      void findUsers() {
        //given
        long totalUsers = 11L;
        int countPerPages = 2;
        PageRequest pageRequest = PageRequest.of(3, countPerPages);
        List<User> users = List.of(user1, user2);
        PageImpl<User> pages = new PageImpl<>(users, pageRequest, totalUsers);
        List<UserServiceResponse> userServiceResponses = users.stream().map(
            user -> UserServiceResponse.builder().id(user.getId()).username(user.getUsername())
                .roleName(user.getRole().getName()).build()).toList();

        //when
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(pages);
        Page<UserServiceResponse> result = userService.findUsers(pageRequest);

        //then
        assertThat(result.getTotalElements(), equalTo(totalUsers));
        assertThat(result.getTotalPages(),
            equalTo((int) (totalUsers + countPerPages) / countPerPages));
        assertThat(result.getContent().size(), equalTo(2));
        assertThat(result.getContent().get(0), equalTo(userServiceResponses.get(0)));
        assertThat(result.getContent().get(1), equalTo(userServiceResponses.get(1)));
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }
}