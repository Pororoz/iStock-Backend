package com.pororoz.istock.service;

import com.pororoz.istock.repository.UserRepository;
import com.pororoz.istock.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
