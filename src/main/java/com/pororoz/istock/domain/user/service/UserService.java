package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceResponse saveUser(SaveUserServiceRequest saveUserServiceRequest) {
        Role role = roleRepository.findByName(saveUserServiceRequest.getRoleName())
                .orElseThrow(RoleNotFoundException::new);
        User user = saveUserServiceRequest.toUser(role);
        User result = userRepository.save(user);
        return UserServiceResponse.of(result);
    }
}
