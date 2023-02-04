package com.pororoz.istock.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.user.dto.request.FindUserRequest;
import com.pororoz.istock.domain.user.dto.request.SaveUserRequest;
import com.pororoz.istock.domain.user.dto.request.UpdateUserRequest;
import com.pororoz.istock.domain.user.dto.response.FindUserResponse;
import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.dto.service.FindUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @InjectMocks
  UserController userController;

  @Mock
  UserService userService;

  @Nested
  @DisplayName("계정 수정하기")
  class UpdateUser {

    private Long id;
    private String username;
    private String roleName;
    private String password;

    @BeforeEach
    void setup() {
      id = 1L;
      username = "test";
      password = "1234ab";
      roleName = "ROLE_USER";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 유저의 update를 요청하면 수정된 유저의 정보를 받는다.")
      void updateUser() {
        // given
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder().id(id).roleName(roleName)
            .password(password).build();
        UserServiceResponse userServiceResponse = UserServiceResponse.builder().id(id)
            .username(username).roleName(roleName).build();
        UserResponse userResponse = UserResponse.builder().id(id).username(username)
            .roleName(roleName).build();

        // when
        when(userService.updateUser(any())).thenReturn(userServiceResponse);
        ResponseEntity<ResultDTO<UserResponse>> response = userController.updateUser(
            updateUserRequest);

        // then
        assertThat(Objects.requireNonNull(response.getBody()).getData()).usingRecursiveComparison()
            .isEqualTo(userResponse);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(
            ResponseStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(
            ResponseMessage.UPDATE_USER);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }

  @Nested
  @DisplayName("계정 삭제하기")
  class DeleteUser {

    private Long id;
    private String username;
    private String roleName;

    @BeforeEach
    void setup() {
      id = 1L;
      username = "test";
      roleName = "ROLE_USER";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 유저를 삭제하면 삭제된 유저를 반환한다.")
      void deleteUser() {
        // given
        UserServiceResponse userServiceResponse = UserServiceResponse.builder().id(id)
            .username(username).roleName(roleName).build();
        UserResponse userResponse = UserResponse.builder().id(id).username(username)
            .roleName(roleName).build();

        // when
        when(userService.deleteUser(any())).thenReturn(userServiceResponse);
        ResponseEntity<ResultDTO<UserResponse>> response = userController.deleteUser(id);

        // then
        assertThat(Objects.requireNonNull(response.getBody()).getData()).usingRecursiveComparison()
            .isEqualTo(userResponse);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(
            ResponseStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(
            ResponseMessage.DELETE_USER);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }
  }

  @Nested
  @DisplayName("계정 생성하기")
  class SaveUser {

    private Long id;
    private String username;
    private String password;
    private String roleName;

    @BeforeEach
    void setup() {
      id = 1L;
      username = "test";
      password = "1234ab";
      roleName = "ROLE_USER";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("유저 생성하기를 성공하면 User 값을 반환받는다.")
      void saveUser() {
        // given
        SaveUserRequest saveUserRequest = SaveUserRequest.builder().username(username)
            .password(password).roleName(roleName).build();
        UserServiceResponse userServiceResponse = UserServiceResponse.builder().id(id)
            .username(username).roleName(roleName).build();
        UserResponse userResponse = UserResponse.builder().id(id).username(username)
            .roleName(roleName).build();

        // when
        when(userService.saveUser(any())).thenReturn(userServiceResponse);
        ResponseEntity<ResultDTO<UserResponse>> response = userController.saveUser(saveUserRequest);

        // then
        assertThat(Objects.requireNonNull(response.getBody()).getData()).usingRecursiveComparison()
            .isEqualTo(userResponse);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(
            ResponseStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(
            ResponseMessage.SAVE_USER);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

    }

  }

  @Nested
  @DisplayName("계정 조회")
  class FindUser {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("계정을 조회한다.")
      void findUsers() {
        //given
        long totalUsers = 11L;
        int countPerPages = 2;
        LocalDateTime today = LocalDateTime.now();
        FindUserRequest request = FindUserRequest.builder().page(3).size(countPerPages).build();
        UserServiceResponse response1 = UserServiceResponse.builder().id(1L).username("user1")
            .roleName("ROLE_USER").createdAt(today).updatedAt(today).build();
        UserServiceResponse response2 = UserServiceResponse.builder().id(2L).username("user2")
            .roleName("ROLE_USER").createdAt(today).updatedAt(today).build();
        List<UserServiceResponse> userServiceResponses = List.of(response1, response2);
        Page<UserServiceResponse> page = new PageImpl<>(userServiceResponses,
            PageRequest.of(3, countPerPages), totalUsers);
        List<FindUserResponse> findUserResponse = List.of(response1.toFindResponse(),
            response2.toFindResponse());

        //when
        when(userService.findUsers(any(FindUserServiceRequest.class))).thenReturn(page);
        ResponseEntity<ResultDTO<PageResponse<FindUserResponse>>> response = userController.findUsers(
            request);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(
            ResponseStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(
            ResponseMessage.FIND_USER);

        PageResponse<FindUserResponse> data = Objects.requireNonNull(response.getBody()).getData();
        assertThat(data.getTotalPages()).isEqualTo(
            (int) (totalUsers + countPerPages) / countPerPages);
        assertThat(data.getTotalElements()).isEqualTo(totalUsers);
        assertThat(data.getCurrentSize()).isEqualTo(countPerPages);
        assertFalse(data.isFirst());
        assertFalse(data.isLast());
        assertThat(data.getContents()).usingRecursiveComparison().isEqualTo(findUserResponse);

        FindUserResponse first = data.getContents().get(0);
        String format = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertThat(first.getCreatedAt()).isEqualTo(format);
        assertThat(first.getUpdatedAt()).isEqualTo(format);
      }
    }
  }
}