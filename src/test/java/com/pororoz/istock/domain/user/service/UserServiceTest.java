package com.pororoz.istock.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.auth.dto.CustomUserDetailsDTO;
import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.exception.SelfDeleteAccountException;
import com.pororoz.istock.domain.user.exception.SelfDemoteRoleException;
import com.pororoz.istock.domain.user.exception.UserNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  UserService userService;

  @Mock
  UserRepository userRepository;

  @Mock
  RoleRepository roleRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @Nested
  @DisplayName("계정 수정 API")
  class UpdateUser {

    private Long userId;
    private String username;
    private String password;
    private String newPassword;
    private String roleName;
    private String newRoleName;
    private final Role roleAdmin = Role.builder().roleName("ROLE_ADMIN").build();
    private final Role roleUser = Role.builder().roleName("ROLE_USER").build();

    @BeforeEach
    void setup() {
      userId = 1L;
      username = "user";
      password = "ab1234";
      newPassword = "abc123";
      roleName = "ROLE_ADMIN";
      newRoleName = "ROLE_USER";

      User admin = User.builder().id(2L).username("admin").password("admin").role(roleAdmin)
          .build();
      CustomUserDetailsDTO user = new CustomUserDetailsDTO(admin);
      SecurityContext context = SecurityContextHolder.getContext();
      context.setAuthentication(
          new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
              user.getAuthorities()));
    }

    @AfterEach
    void teatDown() {
      SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @WithMockUser
      @DisplayName("존재하는 유저를 업데이트한다.")
      void updateUser() {
        // given
        UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
            .userId(userId)
            .roleName(newRoleName)
            .password(newPassword)
            .build();

        User targetUser = User.builder()
            .id(userId)
            .username(username)
            .password(password)
            .role(roleAdmin)
            .build();

        UserServiceResponse response = UserServiceResponse.builder()
            .userId(userId)
            .username(username)
            .roleName(newRoleName)
            .build();

        // when
        when(roleRepository.findByRoleName(any())).thenReturn(Optional.of(roleUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(targetUser));
        UserServiceResponse result = userService.updateUser(updateUserServiceRequest);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
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
            .userId(invalidId)
            .password(password)
            .roleName(roleName)
            .build();

        // when
        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(roleAdmin));

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
            .userId(userId)
            .password(password)
            .roleName(invalidRole)
            .build();

        // when

        //then
        assertThrows(RoleNotFoundException.class,
            () -> userService.updateUser(updateUserServiceRequest));
      }

      @Test
      @DisplayName("자신의 role을 강등시키려고 하면 SelfDemoteException이 발생한다.")
      void selfDemote() {
        // given
        UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
            .userId(2L)
            .roleName(newRoleName)
            .password(newPassword)
            .build();

        User targetUser = User.builder()
            .id(2L)
            .username("admin")
            .password("admin")
            .role(roleAdmin)
            .build();

        // when
        when(roleRepository.findByRoleName(newRoleName)).thenReturn(Optional.of(roleUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        //then
        assertThrows(SelfDemoteRoleException.class,
            () -> userService.updateUser(updateUserServiceRequest));
      }
    }
  }

  @Nested
  @DisplayName("계정 삭제 API")
  class DeleteUser {

    private Long userId;
    private String username;
    private String password;
    private String roleName;
    private Role role;
    private User admin;
    private final Role roleAdmin = Role.builder().roleName("ROLE_ADMIN").build();

    @BeforeEach
    void setup() {
      userId = 1L;
      username = "test";
      password = "1234";
      role = Role.builder().roleName("ROLE_ADMIN").build();
      roleName = "ROLE_ADMIN";

      admin = User.builder().id(2L).username("admin").password("admin").role(roleAdmin).build();
      CustomUserDetailsDTO user = new CustomUserDetailsDTO(admin);
      SecurityContext context = SecurityContextHolder.getContext();
      context.setAuthentication(
          new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
              user.getAuthorities()));
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 유저를 삭제한다.")
      void deleteUser() {
        // given
        DeleteUserServiceRequest deleteUserServiceRequest = DeleteUserServiceRequest.builder()
            .userId(userId).build();

        User resultUser = User.builder()
            .id(userId)
            .username(username)
            .password(password)
            .role(role)
            .build();

        UserServiceResponse response = UserServiceResponse.builder()
            .userId(userId)
            .username(username)
            .roleName(roleName)
            .build();

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(resultUser));
        UserServiceResponse result = userService.deleteUser(deleteUserServiceRequest);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
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
            .userId(invalidId)
            .build();

        // when

        // then
        assertThrows(UserNotFoundException.class,
            () -> userService.deleteUser(deleteUserServiceRequest));
      }

      @Test
      @DisplayName("본인의 ID로 요청했을 때 SelfDeleteAccountException을 반환한다.")
      void selfDeleteAccount() {
        //given
        DeleteUserServiceRequest deleteUserServiceRequest = DeleteUserServiceRequest.builder()
            .userId(2L)
            .build();

        // when
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        // then
        assertThrows(SelfDeleteAccountException.class,
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
    private final String roleName = "ROLE_ADMIN";
    private final Role role = Role.builder().roleName(roleName).build();

    @BeforeEach
    void setup() {
      id = 1L;
      username = "test";
      password = "1234";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("유저를 생성한다.")
      void saveUser() {
        // given
        String encodedPassword = "newEncode123PW";
        SaveUserServiceRequest saveUserServiceRequest = SaveUserServiceRequest.builder()
            .username(username).password(password).roleName(roleName).build();

        User resultUser = User.builder()
            .id(id)
            .username(username)
            .password(password)
            .role(role)
            .build();

        UserServiceResponse response = UserServiceResponse.builder()
            .userId(id)
            .username(username)
            .roleName(roleName)
            .build();

        // when
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any())).thenReturn(resultUser);
        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(role));
        UserServiceResponse result = userService.saveUser(saveUserServiceRequest);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
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

    Role userRole = Role.builder().roleName("ROLE_USER").build();

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
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<User> pages = new PageImpl<>(users, PageRequest.of(page, size), totalUsers);
        List<UserServiceResponse> userServiceResponses = getUserServiceResponses(users);

        //when
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(pages);
        Page<UserServiceResponse> result = userService.findUsers(pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(totalUsers);
        assertThat(result.getTotalPages()).isEqualTo((int) (totalUsers + size) / size);
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0)).usingRecursiveComparison()
            .isEqualTo(userServiceResponses.get(0));
        assertThat(result.getContent().get(1)).usingRecursiveComparison()
            .isEqualTo(userServiceResponses.get(1));
      }

      @Test
      @DisplayName("page와 size가 null이면 전체를 조회한다.")
      void findAll() {
        //given
        Pageable pageable = Pageable.unpaged();
        List<UserServiceResponse> userServiceResponses = getUserServiceResponses(users);

        //when
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(users));
        Page<UserServiceResponse> result = userService.findUsers(pageable);

        //then
        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(userServiceResponses);
        assertEquals(result.getTotalElements(), result.getNumberOfElements());
        assertEquals(result.getTotalPages(), 1);
      }

      @Test
      @DisplayName("DB에 유저가 없어도 정상적으로 조회한다.")
      void findEmptyAll() {
        //given
        List<User> empty = List.of();
        Pageable pageable = Pageable.unpaged();
        List<UserServiceResponse> userServiceResponses = List.of();

        //when
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(empty));
        Page<UserServiceResponse> result = userService.findUsers(pageable);

        //then
        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(userServiceResponses);
        assertEquals(result.getTotalElements(), 0);
        assertEquals(result.getNumberOfElements(), 0);
        assertEquals(result.getTotalPages(), 1);
      }
    }

    private List<UserServiceResponse> getUserServiceResponses(List<User> users) {
      return users.stream().map(
          user -> UserServiceResponse.builder().userId(user.getId()).username(user.getUsername())
              .roleName(user.getRole().getRoleName()).build()).toList();
    }
  }
}