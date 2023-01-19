package com.pororoz.istock.domain.user.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.user.dto.request.SaveUserRequest;
import com.pororoz.istock.domain.user.dto.response.SaveUserResponse;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceResponse;
import com.pororoz.istock.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<ResultDTO<SaveUserResponse>> saveUser(@RequestBody SaveUserRequest saveUserRequest) {
        SaveUserServiceResponse response = userService.saveUser(saveUserRequest.toService());
        return ResponseEntity.ok(new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_USER, response.toResponse()));
    }
}
