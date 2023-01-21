package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.exception.UserNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserResponse updateUser(UpdateUserServiceRequest updateUserServiceRequest) {
        Role role = roleRepository.findByName(updateUserServiceRequest.getRoleName())
                .orElseThrow(RoleNotFoundException::new);
        userRepository.updateUser(updateUserServiceRequest.getPassword(), role, updateUserServiceRequest.getId());
        User user = userRepository.findById(updateUserServiceRequest.getId())
                .orElseThrow(UserNotFoundException::new);
        return UserServiceResponse.of(user).toResponse();
    }

    public UserResponse deleteUser(DeleteUserServiceRequest deleteUserServiceRequest) {
        User user = userRepository.findById(deleteUserServiceRequest.getId())
                .orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(deleteUserServiceRequest.getId());
        return UserServiceResponse.of(user).toResponse();
    }

    public UserResponse saveUser(SaveUserServiceRequest saveUserServiceRequest) {
        Role role = roleRepository.findByName(saveUserServiceRequest.getRoleName())
                .orElseThrow(RoleNotFoundException::new);
        User user = saveUserServiceRequest.toUser(role);
        User result = userRepository.save(user);
        return UserServiceResponse.of(result).toResponse();
    }
}
