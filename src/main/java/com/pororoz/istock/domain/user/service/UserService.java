package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceResponse;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public SaveUserServiceResponse saveUser(SaveUserServiceRequest saveUserServiceRequest) {
        User user = saveUserServiceRequest.toUser();
        User result = userRepository.save(user);
        return SaveUserServiceResponse.of(result);
    }
}
