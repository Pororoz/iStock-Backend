package com.pororoz.istock.domain.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.auth.dto.CustomUserDetailsDTO;
import com.pororoz.istock.domain.user.dto.request.SaveUserRequest;
import com.pororoz.istock.domain.user.dto.request.UpdateUserRequest;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WithMockUser(roles = "ADMIN")
public class UserIntegrationTest extends IntegrationTest {

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Nested
  @DisplayName("PUT /v1/users 계정 수정 API")
  class UpdateUser {

    private final String url = "/v1/users";
    private Long userId;
    private String username;
    private String password;
    private String newPassword;
    private String roleName;
    private String newRoleName;

    @BeforeEach
    public void beforeEach() {
      userId = 1L;
      username = "test";
      password = "1234a";
      newPassword = "123asb";
      roleName = "ROLE_USER";
      newRoleName = "ROLE_ADMIN";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 유저를 수정하면 수정이 이행되고, 해당 유저의 정보를 받아볼 수 있다.")
      void updateUser() throws Exception {
        // given
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(RoleNotFoundException::new);
        User user = User.builder().username(username).password(password).role(role).build();
        userRepository.save(user);
        UpdateUserRequest request = UpdateUserRequest.builder()
            .userId(userId)
            .password(newPassword)
            .roleName(newRoleName).build();

        // when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_USER))
            .andExpect(jsonPath("$.data.userId").value(userId))
            .andExpect(jsonPath("$.data.username").value(username))
            .andExpect(jsonPath("$.data.roleName").value(newRoleName))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("아이디 값이 음수 값이 들어오면 validation error가 발생하고 400 code를 반환한다.")
      void pathNegativeError() throws Exception {
        //given
        UpdateUserRequest request = UpdateUserRequest.builder()
            .userId(-1L)
            .password(newPassword)
            .roleName(newRoleName).build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_ID))
            .andDo(print());
      }

      @Test
      @DisplayName("role이 빈값이면 400code를 반환한다.")
      void roleEmpty() throws Exception {
        //given
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(RoleNotFoundException::new);
        User user = User.builder().username(username).password(password).role(role).build();
        userRepository.save(user);
        UpdateUserRequest request = UpdateUserRequest.builder()
            .userId(userId)
            .password(password)
            .roleName("")
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_ROLENAME))
            .andDo(print());
      }

      @Test
      @DisplayName("존재하지 않는 유저를 수정하면 404 Error와 USER_NOT_FOUND error를 반환받는다.")
      void userNotFound() throws Exception {
        //given
        long notExistUserId = 10000L;
        UpdateUserRequest request = UpdateUserRequest.builder()
            .userId(notExistUserId)
            .password(newPassword)
            .roleName(newRoleName)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.USER_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.USER_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @DisplayName("존재하지 않는 role name이 들어오면 Error가 발생하고 404 코드를 반환한다.")
      void notFoundRoleName() throws Exception {
        //given
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(RoleNotFoundException::new);
        userRepository.save(
            User.builder().username(username).password(password).role(role).build());
        UpdateUserRequest request = UpdateUserRequest.builder()
            .userId(userId)
            .roleName("nothing")
            .password(newPassword)
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        //then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.ROLE_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.ROLE_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @DisplayName("자신의 role을 강등시키려고 하면 Self Demote Error가 발생하고 400 코드를 반환한다.")
      void selfDemote() throws Exception {
        // given
        Role role = roleRepository.findByRoleName("ROLE_ADMIN")
            .orElseThrow(RoleNotFoundException::new);
        User user = User.builder().username(username).password(password).role(role).build();

        CustomUserDetailsDTO userDetail = new CustomUserDetailsDTO(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetail.getUsername(),
            userDetail.getPassword(), userDetail.getAuthorities()));
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
            .userId(userId)
            .password(newPassword)
            .roleName("ROLE_USER").build();

        // when
        ResultActions actions = getResultActions(url, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.SELF_DEMOTE_ROLE))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.SELF_DEMOTE_ROLE))
            .andDo(print());

        SecurityContextHolder.clearContext();
      }
    }
  }

  @Nested
  @DisplayName("DELETE /v1/users/{id} 계정 삭제 API")
  class DeleteUser {

    private String url(long id) {
      return "/v1/users" + "/" + id;
    }

    MultiValueMap<String, String> params;

    private Long id;
    private String username;
    private String password;
    private String roleName;

    @BeforeEach
    public void beforeEach() {
      id = 1L;
      username = "test";
      password = "1234a";
      roleName = "ROLE_USER";
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("존재하는 유저를 삭제하면 삭제에 성공한다.")
      void deleteUser() throws Exception {
        // given
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(RoleNotFoundException::new);
        User user = User.builder().username(username).password(password).role(role).build();
        userRepository.save(user);

        // when
        ResultActions actions = getResultActions(url(id), HttpMethod.DELETE);

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_USER))
            .andExpect(jsonPath("$.data.userId").value(id))
            .andExpect(jsonPath("$.data.username").value(username))
            .andExpect(jsonPath("$.data.roleName").value(roleName))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("존재하지 않는 유저를 삭제하면 404 Error와 USER_NOT_FOUND error를 반환받는다.")
      void userNotFound() throws Exception {
        //given

        //when
        ResultActions actions = getResultActions(url(id), HttpMethod.DELETE);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.USER_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.USER_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @DisplayName("아이디 값이 음수 값이 들어오면 validation error를 뱉는다.")
      void pathNegativeError() throws Exception {
        //given

        //when
        ResultActions actions = getResultActions(url(-1L), HttpMethod.DELETE);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_PATH))
            .andDo(print());
      }

      @Test
      @DisplayName("아이디 값이 문자열로 들어오면 validation error를 뱉는다.")
      void pathStringError() throws Exception {
        //given

        //when
        ResultActions actions = getResultActions("/v1/users/nothing", HttpMethod.DELETE);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.TYPE_MISMATCH))
            .andDo(print());
      }

      @Test
      @DisplayName("자신의 계정을 삭제하려고 하면 Self Delete Error가 발생하고 400 코드를 반환한다.")
      void selfDelete() throws Exception {
        // given
        Role role = roleRepository.findByRoleName("ROLE_ADMIN")
            .orElseThrow(RoleNotFoundException::new);
        User user = User.builder().username(username).password(password).role(role).build();

        CustomUserDetailsDTO userDetail = new CustomUserDetailsDTO(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetail.getUsername(),
            userDetail.getPassword(), userDetail.getAuthorities()));
        userRepository.save(user);

        // when
        ResultActions actions = getResultActions(url(id), HttpMethod.DELETE);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ExceptionStatus.SELF_DELETE_ACCOUNT))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.SELF_DELETE_ACCOUNT))
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("POST /v1/users 계정 생성 API")
  class SaveUser {

    private final String url = "/v1/users";

    private String username;
    private String password;
    private String roleName;

    @BeforeEach
    public void beforeEach() {
      username = "test";
      password = "1234a";
      roleName = "ROLE_USER";
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("중복되지 않는 유저 정보를 건네주면 계정 생성에 성공한다.")
      void saveUser() throws Exception {
        // given
        SaveUserRequest request = SaveUserRequest.builder()
            .username(username)
            .password(password)
            .roleName(roleName).build();

        // when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_USER))
            .andExpect(jsonPath("$.data.userId").value(1L))
            .andExpect(jsonPath("$.data.username").value(username))
            .andExpect(jsonPath("$.data.roleName").value(roleName))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("1자리 비밀번호는 에러가 발생한다")
      void onlyEnglish() throws Exception {
        //given
        SaveUserRequest request = SaveUserRequest.builder()
            .username("agridjlid")
            .password("1")
            .roleName("ROLE_ADMIN")
            .build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_PASSWORD))
            .andDo(print());
      }

      @Test
      @DisplayName("존재하지 않는 role name이 들어오면 Error가 발생한다.")
      void notFoundRoleName() throws Exception {
        //given
        SaveUserRequest request = SaveUserRequest.builder().username("test")
            .roleName("nothing").password("1234abcd").build();

        //when
        ResultActions actions = getResultActions(url, HttpMethod.POST, request);

        //then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.ROLE_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.ROLE_NOT_FOUND))
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("GET /v1/users 계정 조회 API")
  class FindUsers {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      String url = "http://localhost:8080/v1/users";
      int userCounts = 10;

      @BeforeEach
      void setUp() {
        for (int i = 0; i < userCounts; i++) {
          Role role = roleRepository.findByRoleName("ROLE_USER").orElseThrow();
          User user = User.builder().username("ROLE_USER" + i).role(role).password("12345678")
              .build();
          userRepository.save(user);
        }
      }

      @Test
      @DisplayName("첫 페이지를 조회한다.")
      void findFirstPage() throws Exception {
        //given
        int page = 0;
        int size = 3;

        //when
        ResultActions actions = mockMvc.perform(get(url)
            .param("size", String.valueOf(size))
            .param("page", String.valueOf(page))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_USER))
            .andExpect(jsonPath("$.data.totalPages").value((userCounts + size) / size))
            .andExpect(jsonPath("$.data.totalElements").value(userCounts))
            .andExpect(jsonPath("$.data.first").value(true))
            .andExpect(jsonPath("$.data.last").value(false))
            .andExpect(jsonPath("$.data.currentSize").value(3))
            .andDo(print());

        for (int i = 0; i < size; i++) {
          actions.andExpect(jsonPath("$.data.contents[" + i + "].username").value("ROLE_USER" + i));
        }
      }

      @Test
      @DisplayName("마지막 페이지를 조회한다.")
      void findLastPage() throws Exception {
        //given
        int page = 3;
        int size = 3;

        //when
        ResultActions actions = mockMvc.perform(get(url)
            .param("size", String.valueOf(size))
            .param("page", String.valueOf(page))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_USER))
            .andExpect(jsonPath("$.data.totalPages").value((userCounts + size) / size))
            .andExpect(jsonPath("$.data.totalElements").value(userCounts))
            .andExpect(jsonPath("$.data.first").value(false))
            .andExpect(jsonPath("$.data.last").value(true))
            .andExpect(jsonPath("$.data.currentSize").value(1))
            .andExpect(jsonPath("$.data.contents[0].username").value("ROLE_USER9"))
            .andDo(print());
      }

      @Test
      @DisplayName("파라미터가 없으면 전체를 조회한다.")
      void findAll() throws Exception {
        //when
        ResultActions actions = mockMvc.perform(get(url)
            .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_USER))
            .andExpect(jsonPath("$.data.totalPages").value(1))
            .andExpect(jsonPath("$.data.totalElements").value(userCounts))
            .andExpect(jsonPath("$.data.first").value(true))
            .andExpect(jsonPath("$.data.last").value(true))
            .andExpect(jsonPath("$.data.currentSize").value(userCounts))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      String url = "http://localhost:8080/v1/users";

      @Test
      @DisplayName("page 파라미터가 음수이면 오류를 반환한다.")
      void pageNegative() throws Exception {
        //given
        int size = 10;
        int page = -1;

        //when
        ResultActions actions = mockMvc.perform(get(url)
            .param("size", String.valueOf(size))
            .param("page", String.valueOf(page))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_PAGE_REQUEST))
            .andDo(print());
      }

      @Test
      @DisplayName("size 파라미터가 0 이하이면 오류를 반환한다.")
      void sizeNegativeOrZero() throws Exception {
        //given
        int size = 0;
        int page = 10;

        //when
        ResultActions actions = mockMvc.perform(get(url)
            .param("size", String.valueOf(size))
            .param("page", String.valueOf(page))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_PAGE_REQUEST))
            .andDo(print());
      }
    }
  }
}
