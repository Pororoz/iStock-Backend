package com.pororoz.istock.domain.user.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.user.dto.request.SaveUserRequest;
import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.service.UserService;
import com.pororoz.istock.domain.user.swagger.exception.RoleNotFoundExceptionSwagger;
import com.pororoz.istock.domain.user.swagger.response.SaveUserResponseSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDTO<UserResponse>> deleteUser(@PathVariable Long id) {
        UserResponse response = userService.deleteUser(DeleteUserServiceRequest.builder().id(id).build());
        return ResponseEntity.ok(new ResultDTO<>(ResponseStatus.OK, ResponseMessage.DELETE_USER, response));
    }

    @Operation(summary = "save user", description = "유저 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = ResponseMessage.SAVE_USER,
                content = {@Content(schema = @Schema(implementation = SaveUserResponseSwagger.class))}),
            @ApiResponse(responseCode = "404", description = ExceptionMessage.ROLE_NOT_FOUND,
                content = {@Content(schema = @Schema(implementation = RoleNotFoundExceptionSwagger.class))})
    })
    @PostMapping
    public ResponseEntity<ResultDTO<UserResponse>> saveUser(@Valid @RequestBody SaveUserRequest saveUserRequest) {
        UserResponse response = userService.saveUser(saveUserRequest.toService());
        return ResponseEntity.ok(new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_USER, response));
    }
}
