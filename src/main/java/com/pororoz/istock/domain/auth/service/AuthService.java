package com.pororoz.istock.domain.auth.service;

import com.pororoz.istock.domain.auth.dto.CustomUserDetailsDTO;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import com.pororoz.istock.domain.auth.dto.service.LoginDTO;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class AuthService{

    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetailsDTO principal = (CustomUserDetailsDTO) authentication.getPrincipal();

        LoginResponse responseDto = LoginResponse.of(principal.getUser());
        System.out.println(responseDto);

        return responseDto;
    }

}
