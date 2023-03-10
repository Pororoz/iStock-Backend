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
  @DisplayName("PUT /v1/users ?????? ?????? API")
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
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("???????????? ????????? ???????????? ????????? ????????????, ?????? ????????? ????????? ????????? ??? ??????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("????????? ?????? ?????? ?????? ???????????? validation error??? ???????????? 400 code??? ????????????.")
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
      @DisplayName("role??? ???????????? 400code??? ????????????.")
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
      @DisplayName("???????????? ?????? ????????? ???????????? 404 Error??? USER_NOT_FOUND error??? ???????????????.")
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
      @DisplayName("???????????? ?????? role name??? ???????????? Error??? ???????????? 404 ????????? ????????????.")
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
      @DisplayName("????????? role??? ?????????????????? ?????? Self Demote Error??? ???????????? 400 ????????? ????????????.")
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
  @DisplayName("DELETE /v1/users/{id} ?????? ?????? API")
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
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("???????????? ????????? ???????????? ????????? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("???????????? ?????? ????????? ???????????? 404 Error??? USER_NOT_FOUND error??? ???????????????.")
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
      @DisplayName("????????? ?????? ?????? ?????? ???????????? validation error??? ?????????.")
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
      @DisplayName("????????? ?????? ???????????? ???????????? validation error??? ?????????.")
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
      @DisplayName("????????? ????????? ??????????????? ?????? Self Delete Error??? ???????????? 400 ????????? ????????????.")
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
  @DisplayName("POST /v1/users ?????? ?????? API")
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
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("???????????? ?????? ?????? ????????? ???????????? ?????? ????????? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("1?????? ??????????????? ????????? ????????????")
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
      @DisplayName("???????????? ?????? role name??? ???????????? Error??? ????????????.")
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
  @DisplayName("GET /v1/users ?????? ?????? API")
  class FindUsers {

    @Nested
    @DisplayName("?????? ?????????")
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
      @DisplayName("??? ???????????? ????????????.")
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
      @DisplayName("????????? ???????????? ????????????.")
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
      @DisplayName("??????????????? ????????? ????????? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      String url = "http://localhost:8080/v1/users";

      @Test
      @DisplayName("page ??????????????? ???????????? ????????? ????????????.")
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
      @DisplayName("size ??????????????? 0 ???????????? ????????? ????????????.")
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
