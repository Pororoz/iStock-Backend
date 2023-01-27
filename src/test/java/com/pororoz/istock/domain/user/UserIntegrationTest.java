package com.pororoz.istock.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.common.service.DatabaseCleanup;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.user.dto.request.SaveUserRequest;
import com.pororoz.istock.domain.user.dto.request.UpdateUserRequest;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @AfterEach
    public void afterEach() {
        databaseCleanup.execute();
    }

    @Nested
    @DisplayName("PUT /v1/users 계정 수정 API")
    @Transactional
    class UpdateUser {
        private final String url = "/v1/users";
        private Long id;
        private String username;
        private String password;
        private String newPassword;
        private String roleName;
        private String newRoleName;

        @BeforeEach
        public void beforeEach() {
            id = 1L;
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
            void updateUser() throws Exception{
                // given
                Role role = roleRepository.findByName(roleName).orElseThrow(RoleNotFoundException::new);
                User user = User.builder().username(username).password(password).role(role).build();
                userRepository.save(user);
                UpdateUserRequest request = UpdateUserRequest.builder()
                        .id(id)
                        .password(newPassword)
                        .roleName(newRoleName).build();

                // when
                ResultActions actions = mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
                        .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_USER))
                        .andExpect(jsonPath("$.data.id").value(id))
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
                        .id(-1L)
                        .password(newPassword)
                        .roleName(newRoleName).build();

                //when
                ResultActions actions = mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                actions.andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
                        .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_ID))
                        .andDo(print());
            }

            @Test
            @DisplayName("영어로만 이루어진 비밀번호는 에러가 발생하고 400 code를 반환한다.")
            void onlyEnglish() throws Exception {
                //given
                Role role = roleRepository.findByName(roleName).orElseThrow(RoleNotFoundException::new);
                User user = User.builder().username(username).password(password).role(role).build();
                userRepository.save(user);
                String onlyStr = "asdafw";
                UpdateUserRequest request = UpdateUserRequest.builder()
                        .id(id)
                        .password(onlyStr)
                        .roleName(newRoleName)
                        .build();

                //when
                ResultActions actions = mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

                //then
                actions.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
                        .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_PASSWORD))
                        .andDo(print());
            }

            @Test
            @DisplayName("role이 빈값이면 400code를 반환한다.")
            void roleEmpty() throws Exception {
                //given
                Role role = roleRepository.findByName(roleName).orElseThrow(RoleNotFoundException::new);
                User user = User.builder().username(username).password(password).role(role).build();
                userRepository.save(user);
                UpdateUserRequest request = UpdateUserRequest.builder()
                        .id(id)
                        .password(password)
                        .roleName("")
                        .build();

                //when
                ResultActions actions = mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

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
                        .id(notExistUserId)
                        .password(newPassword)
                        .roleName(newRoleName)
                        .build();

                //when
                ResultActions actions = mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                actions.andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status").value(ExceptionStatus.USER_NOT_FOUND))
                        .andExpect(jsonPath("$.message").value(ExceptionMessage.USER_NOT_FOUND))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 role name이 들어오면 Error가 발생하고 404 코드를 반환한다.")
            void notFoundRoleName() throws Exception{
                //given
                UpdateUserRequest request= UpdateUserRequest.builder()
                        .id(id)
                        .roleName("nothing")
                        .password(newPassword)
                        .build();

                //when
                ResultActions actions = mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

                //then
                actions.andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.status").value(ExceptionStatus.ROLE_NOT_FOUND))
                        .andExpect(jsonPath("$.message").value(ExceptionMessage.ROLE_NOT_FOUND))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("DELETE /v1/users/{id} 계정 삭제 API")
    @Transactional
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
            void deleteUser() throws Exception{
                // given
                Role role = roleRepository.findByName(roleName).orElseThrow(RoleNotFoundException::new);
                User user = User.builder().username(username).password(password).role(role).build();
                userRepository.save(user);

                // when
                ResultActions actions = mockMvc.perform(delete(url(id))
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
                        .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_USER))
                        .andExpect(jsonPath("$.data.id").value(id))
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
                ResultActions actions = mockMvc.perform(delete(url(id))
                        .contentType(MediaType.APPLICATION_JSON));

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
                ResultActions actions = mockMvc.perform(delete(url(-1L))
                        .contentType(MediaType.APPLICATION_JSON));

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
                ResultActions actions = mockMvc.perform(delete("/v1/users/nothing")
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                actions.andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
                        .andExpect(jsonPath("$.message").value(ExceptionMessage.TYPE_MISMATCH))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("POST /v1/users 계정 생성 API")
    @Transactional
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
            void saveUser() throws Exception{
                // given
                String request = objectMapper.writeValueAsString(SaveUserRequest.builder()
                        .username(username)
                        .password(password)
                        .roleName(roleName).build());

                // when
                ResultActions actions = mockMvc.perform(post(url)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
                        .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_USER))
                        .andExpect(jsonPath("$.data.id").value(1L))
                        .andExpect(jsonPath("$.data.username").value(username))
                        .andExpect(jsonPath("$.data.roleName").value(roleName))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {
            @Test
            @DisplayName("영어로만 이루어진 비밀번호는 에러가 발생한다")
            void onlyEnglish() throws Exception {
                //given
                SaveUserRequest request = SaveUserRequest.builder()
                        .username("agridjlid")
                        .password("abcdefgh")
                        .roleName("ROLE_ADMIN")
                        .build();

                //when
                ResultActions actions = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                //then
                actions.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
                        .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_PASSWORD))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 role name이 들어오면 Error가 발생한다.")
            void notFoundRoleName() throws Exception{
                //given
                SaveUserRequest request= SaveUserRequest.builder().username("test")
                        .roleName("nothing").password("1234abcd").build();

                //when
                ResultActions actions = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                //then
                actions.andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.status").value(ExceptionStatus.ROLE_NOT_FOUND))
                        .andExpect(jsonPath("$.message").value(ExceptionMessage.ROLE_NOT_FOUND))
                        .andDo(print());
            }
        }
    }
}
