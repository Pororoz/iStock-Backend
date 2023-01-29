package com.pororoz.istock.domain.user.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.FindUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.exception.UserNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import java.util.ArrayList;
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
  @DisplayName("계정 수정 API")
  class UpdateUser {

    private Long id;
    private String username;
    private String password;
    private String newPassword;
    private String roleName;
    private String newRoleName;
    private Role role;

    @BeforeEach
    void setup() {
      id = 1L;
      username = "test";
      password = "ab1234";
      newPassword = "abc123";
      roleName = "ROLE_USER";
      newRoleName = "ROLE_ADMIN";
      role = Role.builder().name("ROLE_USER").build();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 유저를 업데이트한다.")
      void updateUser() {
        // given
        UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
            .id(id)
            .roleName(newRoleName)
            .password(newPassword)
            .build();

        User targetUser = User.builder()
            .id(id)
            .username(username)
            .password(password)
            .role(role)
            .build();

        UserServiceResponse response = UserServiceResponse.builder()
            .id(id)
            .username(username)
            .roleName(roleName)
            .build();

        // when
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(userRepository.findById(any())).thenReturn(Optional.of(targetUser));
        UserServiceResponse result = userService.updateUser(updateUserServiceRequest);

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
        long invalidId = 10000L;
        UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
            .id(invalidId)
            .password(password)
            .roleName(roleName)
            .build();

        // when
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        // then
        assertThrows(UserNotFoundException.class,
            () -> userService.updateUser(updateUserServiceRequest));
      }

      @Test
      @DisplayName("잘못된 role name은 RoleNotFoundExceptin이 발생한다.")
      void notFoundUser() {
        //given
        String invalidRole = "a";
        UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
            .id(id)
            .password(password)
            .roleName(invalidRole)
            .build();

        // when

        //then
        assertThrows(RoleNotFoundException.class,
            () -> userService.updateUser(updateUserServiceRequest));
      }
    }
  }

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
      role = Role.builder().name("ROLE_ADMIN").build();
      roleName = "ROLE_ADMIN";
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

        User resultUser = User.builder()
            .id(id)
            .username(username)
            .password(password)
            .role(role)
            .build();

        UserServiceResponse response = UserServiceResponse.builder()
            .id(id)
            .username(username)
            .roleName(roleName)
            .build();

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(resultUser));
        UserServiceResponse result = userService.deleteUser(deleteUserServiceRequest);

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
        long invalidId = 10000L;
        DeleteUserServiceRequest deleteUserServiceRequest = DeleteUserServiceRequest.builder()
            .id(invalidId)
            .build();

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
      roleName = "ROLE_ADMIN";
      role = Role.builder().name("ROLE_ADMIN").build();
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

        User resultUser = User.builder()
            .id(id)
            .username(username)
            .password(password)
            .role(role)
            .build();

        UserServiceResponse response = UserServiceResponse.builder()
            .id(id)
            .username(username)
            .roleName(roleName)
            .build();

        // when
        when(userRepository.save(any())).thenReturn(resultUser);
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        UserServiceResponse result = userService.saveUser(saveUserServiceRequest);

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

    Role userRole = Role.builder().name("ROLE_USER").build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      User user1 = User.builder().id(1L).username("user1").password("1q2w3e4r").role(userRole)
          .build();
      User user2 = User.builder().id(2L).username("user2").password("1q2w3e4r").role(userRole)
          .build();
      List<User> users = List.of(user1, user2);

      @Test
      @DisplayName("두번째 계정 페이지를 조회한다.")
      void findUsers() {
        //given
        long totalUsers = 11L;
        int size = 2;
        int page = 3;
        FindUserServiceRequest request = FindUserServiceRequest.builder().page(page).size(size)
            .build();
        PageImpl<User> pages = new PageImpl<>(users, PageRequest.of(page, size), totalUsers);
        List<UserServiceResponse> userServiceResponses = getUserServiceResponses(users);

        //when
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(pages);
        Page<UserServiceResponse> result = userService.findUsers(request);

        //then
        assertThat(result.getTotalElements(), equalTo(totalUsers));
        assertThat(result.getTotalPages(),
            equalTo((int) (totalUsers + size) / size));
        assertThat(result.getContent().size(), equalTo(2));
        assertThat(result.getContent().get(0), equalTo(userServiceResponses.get(0)));
        assertThat(result.getContent().get(1), equalTo(userServiceResponses.get(1)));
      }

      @Test
      @DisplayName("page와 size가 null이면 전체를 조회한다.")
      void findAll() {
        //given
        FindUserServiceRequest request = FindUserServiceRequest.builder().build();
        List<UserServiceResponse> userServiceResponses = getUserServiceResponses(users);

        //when
        when(userRepository.findAll()).thenReturn(users);
        Page<UserServiceResponse> result = userService.findUsers(request);

        //then
        assertIterableEquals(result.getContent(), userServiceResponses);
        assertEquals(result.getTotalElements(), result.getNumberOfElements());
        assertEquals(result.getTotalPages(), 1);
      }

      @Test
      @DisplayName("DB에 유저가 없어도 정상적으로 조회한다.")
      void findEmptyAll() {
        //given
        List<User> empty = List.of();
        FindUserServiceRequest request = FindUserServiceRequest.builder().build();
        List<UserServiceResponse> userServiceResponses = List.of();

        //when
        when(userRepository.findAll()).thenReturn(empty);
        Page<UserServiceResponse> result = userService.findUsers(request);

        //then
        assertIterableEquals(result.getContent(), userServiceResponses);
        assertEquals(result.getTotalElements(), 0);
        assertEquals(result.getNumberOfElements(), 0);
        assertEquals(result.getTotalPages(), 1);
      }

      @Test
      @DisplayName("page만 null이면 default 값(첫 페이지)으로 조회한다.")
      void pageNull() {
        //given
        long totalUsers = 11L;
        int size = 2;
        int defaultPage = FindUserServiceRequest.DEFAULT_PAGE;
        FindUserServiceRequest request = FindUserServiceRequest.builder().size(size)
            .build();
        PageImpl<User> pages = new PageImpl<>(users, PageRequest.of(defaultPage, size), totalUsers);
        List<UserServiceResponse> userServiceResponses = getUserServiceResponses(users);

        //when
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(pages);
        Page<UserServiceResponse> result = userService.findUsers(request);

        //then
        assertTrue(result.isFirst());
        assertFalse(result.isLast());
        assertThat(result.getTotalElements(), equalTo(totalUsers));
        assertThat(result.getTotalPages(),
            equalTo((int) (totalUsers + size) / size));
        assertThat(result.getContent().size(), equalTo(2));
        assertThat(result.getContent().get(0), equalTo(userServiceResponses.get(0)));
        assertThat(result.getContent().get(1), equalTo(userServiceResponses.get(1)));
      }

      @Test
      @DisplayName("size만 null이면 default 값으로 조회한다.")
      void sizeNull() {
        //given
        long totalUsers = 45L;
        int page = 1;
        int defaultSize = FindUserServiceRequest.DEFAULT_SIZE;
        List<User> findUsers = new ArrayList<>();
        for (int i = 0; i < defaultSize; i++) {
          findUsers.add(
              User.builder().id((long) i).username("ROLE_USER" + i).password("1q2w3e4r")
                  .role(userRole)
                  .build());
        }
        FindUserServiceRequest request = FindUserServiceRequest.builder().page(page)
            .build();
        PageImpl<User> pages = new PageImpl<>(findUsers, PageRequest.of(page, defaultSize),
            totalUsers);
        List<UserServiceResponse> userServiceResponses = getUserServiceResponses(findUsers);

        //when
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(pages);
        Page<UserServiceResponse> result = userService.findUsers(request);

        //then
        assertFalse(result.isFirst());
        assertFalse(result.isLast());
        assertThat(result.getTotalElements(), equalTo(totalUsers));
        assertThat(result.getTotalPages(),
            equalTo((int) (totalUsers + defaultSize) / defaultSize));
        assertThat(result.getContent().size(), equalTo(defaultSize));
        assertIterableEquals(result.getContent(), userServiceResponses);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }

    private List<UserServiceResponse> getUserServiceResponses(List<User> users) {
      return users.stream().map(
          user -> UserServiceResponse.builder().id(user.getId()).username(user.getUsername())
              .roleName(user.getRole().getName()).build()).toList();
    }
  }
}